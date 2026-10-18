package com.devtech.gestiondestock.interceptor;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author luca
 * Intercepteur Hibernate pour filtrer les données par entreprise.
 *
 * <p>Voir {@code docs/adr/ADR-002-filtrage-multi-tenant-fail-closed.md}.
 * Règle de sécurité : dans un contexte authentifié (MDC {@code userEmail} posé),
 * une requête ne peut JAMAIS s'exécuter sans filtre — en cas d'entreprise absente
 * ou invalide, l'intercepteur lève une exception (fail-closed) au lieu de renvoyer
 * le SQL non filtré. Les contextes système (login, démarrage, tâche planifiée) ne
 * portent pas de {@code userEmail} et restent volontairement non filtrés.</p>
 */
@Component
public class Interceptor implements StatementInspector {

    private static final Logger log = LoggerFactory.getLogger(Interceptor.class);

    /** Clé MDC posée par ApplicationRequestFilter à partir du JWT. */
    static final String MDC_TENANT = "idEntreprise";
    /** Clé MDC posée par ApplicationRequestFilter quand un JWT valide a identifié l'utilisateur. */
    static final String MDC_USER_EMAIL = "userEmail";

    /** Tables sans colonne {@code identreprise} : jamais filtrées. */
    private static final Set<String> EXCLUDED_TABLES = Set.of(
            "entreprise", "entreprises", "roles", "role");

    /** Mots-clés qui terminent, au premier niveau, la clause de filtrage. */
    private static final Set<String> TRAILING_KEYWORDS_WITH_BY = Set.of("group", "order");
    private static final Set<String> TRAILING_KEYWORDS_SIMPLE = Set.of(
            "having", "limit", "offset", "window", "fetch");

    private static final String JOIN_OR_CLAUSE_KEYWORDS =
            "where|group|order|having|limit|offset|union|for|join|left|right|inner|outer|cross|on|"
                    + "fetch|window|set|values|using|natural|full|straight_join";

    private static final Pattern FROM_PATTERN = Pattern.compile(
            "(?i)\\bfrom\\s+([a-zA-Z_][a-zA-Z0-9_]*)"
                    + "(?:\\s+(?:as\\s+)?(?!(?:" + JOIN_OR_CLAUSE_KEYWORDS + ")\\b)([a-zA-Z_][a-zA-Z0-9_]*))?",
            Pattern.CASE_INSENSITIVE);

    public Interceptor() {
        super();
    }

    @Override
    public String inspect(String sql) {
        if (!StringUtils.hasLength(sql)) {
            return sql;
        }

        String trimmedSql = sql.trim();
        if (!trimmedSql.toLowerCase(Locale.ROOT).startsWith("select")) {
            return sql;
        }

        Integer idEntreprise = resolveTenantOrFailClosed();
        if (idEntreprise == null) {
            // Contexte système (login, démarrage, tâche planifiée) : pas d'entreprise -> pas de filtre.
            log.debug("INTERCEPTOR: contexte système sans entreprise - aucune restriction appliquée");
            return sql;
        }

        TableInfo tableInfo = extractTableInfo(sql);
        if (tableInfo.tableName == null) {
            if (containsTopLevelFrom(sql)) {
                throw new IllegalStateException(
                        "INTERCEPTOR: table principale introuvable dans une requête authentifiée - "
                                + "requête refusée par sécurité multi-tenant. SQL: " + sql);
            }
            return sql;
        }
        if (EXCLUDED_TABLES.contains(tableInfo.tableName.toLowerCase(Locale.ROOT))) {
            return sql;
        }

        String aliasOrTable = tableInfo.alias != null ? tableInfo.alias : tableInfo.tableName;

        // Filtre déjà posé sur la table principale (ex. CompteurFacture) : ne pas le dupliquer.
        if (mainTableAlreadyFiltered(sql, aliasOrTable)) {
            return sql;
        }

        String filterCondition = "(" + aliasOrTable + ".identreprise = " + idEntreprise
                + " OR " + aliasOrTable + ".identreprise IS NULL)";

        String result = applyTenantFilter(sql, filterCondition);
        log.debug("INTERCEPTOR MODIFIÉ: {}", result);
        return result;
    }

    private boolean mainTableAlreadyFiltered(String sql, String aliasOrTable) {
        int whereStart = -1;
        for (Token token : topLevelTokens(sql)) {
            if (token.value().equals("where")) {
                whereStart = token.start();
                break;
            }
        }
        if (whereStart < 0) {
            return false;
        }
        // On n'examine que la clause WHERE : une condition de JOIN (ex. on a.identreprise = e.id)
        // n'est pas un filtre de sécurité.
        String whereClause = sql.substring(whereStart);
        Pattern alreadyFiltered = Pattern.compile(
                "(?i)" + Pattern.quote(aliasOrTable) + "\\.identreprise\\s*=");
        return alreadyFiltered.matcher(whereClause).find();
    }

    private boolean containsTopLevelFrom(String sql) {
        return topLevelTokens(sql).stream().anyMatch(t -> t.value().equals("from"));
    }

    /**
     * Détermine l'entreprise courante à partir du MDC.
     *
     * @return l'id de l'entreprise, ou {@code null} pour un contexte système (non authentifié).
     * @throws IllegalStateException si un contexte authentifié ne permet pas de résoudre l'entreprise
     *                               (fail-closed : on refuse d'exécuter une requête non filtrée).
     */
    private Integer resolveTenantOrFailClosed() {
        String raw = MDC.get(MDC_TENANT);
        if (StringUtils.hasText(raw)) {
            try {
                return Integer.valueOf(raw.trim());
            } catch (NumberFormatException e) {
                throw new IllegalStateException(
                        "INTERCEPTOR: idEntreprise invalide dans le contexte ('" + raw
                                + "') - requête refusée par sécurité multi-tenant");
            }
        }

        String userEmail = MDC.get(MDC_USER_EMAIL);
        if (StringUtils.hasText(userEmail)) {
            throw new IllegalStateException(
                    "INTERCEPTOR: utilisateur authentifié (" + userEmail
                            + ") sans idEntreprise - requête refusée par sécurité multi-tenant");
        }

        return null;
    }

    private static class TableInfo {
        final String tableName;
        final String alias;

        TableInfo(String tableName, String alias) {
            this.tableName = tableName;
            this.alias = alias;
        }
    }

    private TableInfo extractTableInfo(String sql) {
        String normalizedSql = removeSubqueries(sql);
        Matcher matcher = FROM_PATTERN.matcher(normalizedSql);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            String alias = matcher.group(2);
            log.debug("EXTRACTED TABLE: {} alias: {} from SQL: {}", tableName, alias, sql);
            return new TableInfo(tableName, alias);
        }

        log.debug("INTERCEPTOR: aucune table principale identifiée dans le SQL: {}", sql);
        return new TableInfo(null, null);
    }

    /**
     * Neutralise les sous-requêtes afin d'identifier la table principale du FROM.
     */
    private String removeSubqueries(String sql) {
        StringBuilder result = new StringBuilder();
        int parenthesisCount = 0;
        boolean inSubquery = false;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);

            if (c == '(' && !inSubquery) {
                int from = Math.max(0, i - 7);
                if (sql.substring(from, i).toLowerCase(Locale.ROOT).contains("select")) {
                    inSubquery = true;
                    parenthesisCount = 0;
                }
            }

            if (inSubquery) {
                if (c == '(') {
                    parenthesisCount++;
                } else if (c == ')') {
                    parenthesisCount--;
                    if (parenthesisCount == 0) {
                        inSubquery = false;
                        result.append(" NULL ");
                        continue;
                    }
                }
                continue;
            }

            result.append(c);
        }

        return result.toString();
    }

    /**
     * Insère la condition de filtrage à un emplacement syntaxiquement valide :
     * juste avant {@code GROUP BY / ORDER BY / HAVING / LIMIT / OFFSET / WINDOW / FETCH},
     * ou en fin de requête. {@code AND} est utilisé si un {@code WHERE} de premier niveau
     * existe déjà, {@code WHERE} sinon.
     */
    private String applyTenantFilter(String sql, String filterCondition) {
        // Isole un éventuel point-virgule final.
        int end = sql.length();
        while (end > 0 && Character.isWhitespace(sql.charAt(end - 1))) {
            end--;
        }
        String terminator = "";
        if (end > 0 && sql.charAt(end - 1) == ';') {
            terminator = ";";
            end--;
            while (end > 0 && Character.isWhitespace(sql.charAt(end - 1))) {
                end--;
            }
        }
        String body = sql.substring(0, end);

        List<Token> tokens = topLevelTokens(body);
        for (Token token : tokens) {
            if (token.value().equals("union")) {
                throw new IllegalStateException(
                        "INTERCEPTOR: requête UNION non supportée par le filtre multi-tenant - "
                                + "requête refusée par sécurité");
            }
        }

        boolean hasWhere = tokens.stream().anyMatch(t -> t.value().equals("where"));
        int insertPos = findTrailingClauseStart(tokens, body.length());

        String head = body.substring(0, insertPos);
        String tail = body.substring(insertPos);

        StringBuilder sb = new StringBuilder();
        sb.append(stripTrailingSpaces(head));
        sb.append(hasWhere ? " AND " : " WHERE ");
        sb.append(filterCondition);
        if (!tail.isEmpty()) {
            sb.append(' ').append(tail);
        }
        sb.append(terminator);
        return sb.toString();
    }

    private int findTrailingClauseStart(List<Token> tokens, int fallback) {
        for (int i = 0; i < tokens.size(); i++) {
            String word = tokens.get(i).value();
            String next = (i + 1 < tokens.size()) ? tokens.get(i + 1).value() : "";
            boolean isTrailing =
                    (TRAILING_KEYWORDS_WITH_BY.contains(word) && "by".equals(next))
                            || (word.equals("for") && "update".equals(next))
                            || TRAILING_KEYWORDS_SIMPLE.contains(word)
                            || word.equals("union");
            if (isTrailing) {
                return tokens.get(i).start();
            }
        }
        return fallback;
    }

    private String stripTrailingSpaces(String value) {
        int end = value.length();
        while (end > 0 && Character.isWhitespace(value.charAt(end - 1))) {
            end--;
        }
        return value.substring(0, end);
    }

    /**
     * Tokenise la requête au premier niveau (hors parenthèses et hors littéraux chaîne).
     */
    private List<Token> topLevelTokens(String sql) {
        List<Token> tokens = new ArrayList<>();
        int depth = 0;
        boolean inString = false;
        int i = 0;
        int n = sql.length();

        while (i < n) {
            char c = sql.charAt(i);

            if (inString) {
                if (c == '\'') {
                    if (i + 1 < n && sql.charAt(i + 1) == '\'') {
                        i += 2;
                        continue;
                    }
                    inString = false;
                }
                i++;
                continue;
            }

            if (c == '\'') {
                inString = true;
                i++;
                continue;
            }
            if (c == '(') {
                depth++;
                i++;
                continue;
            }
            if (c == ')') {
                depth = Math.max(0, depth - 1);
                i++;
                continue;
            }
            if (depth == 0 && (Character.isLetter(c) || c == '_')) {
                int start = i;
                while (i < n && (Character.isLetterOrDigit(sql.charAt(i)) || sql.charAt(i) == '_')) {
                    i++;
                }
                tokens.add(new Token(start, sql.substring(start, i).toLowerCase(Locale.ROOT)));
                continue;
            }
            i++;
        }

        return tokens;
    }

    private record Token(int start, String value) {
    }
}
