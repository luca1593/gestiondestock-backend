package com.devtech.gestiondestock.interceptor;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author luca
 * Intercepteur Hibernate pour filtrer les données par entreprise
 */
@Component
public class Interceptor implements StatementInspector {

    private static final Logger log = LoggerFactory.getLogger(Interceptor.class);

    private static final Set<String> EXCLUDED_TABLES = new HashSet<>();
    
    static {
        EXCLUDED_TABLES.add("entreprise");
        EXCLUDED_TABLES.add("entreprises");
        EXCLUDED_TABLES.add("roles");
        EXCLUDED_TABLES.add("role");
    }

    private static final Pattern FROM_PATTERN = Pattern.compile(
        "(?i)\\bfrom\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s+(as\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern WHERE_PATTERN = Pattern.compile(
        "(?i)\\bwhere\\s+",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern IDENTREPRISE_PATTERN = Pattern.compile(
        "(?i)\\.identreprise\\s*=\\s*(\\?|\\d+)",
        Pattern.CASE_INSENSITIVE
    );

    public Interceptor() {
        super();
    }

    @Override
    public String inspect(String sql) {
        if (!StringUtils.hasLength(sql)) {
            return sql;
        }

        String trimmedSql = sql.trim();
        
        if (!trimmedSql.toLowerCase().startsWith("select")) {
            return sql;
        }

        String idEntreprise = MDC.get("idEntreprise");
        if (!StringUtils.hasLength(idEntreprise)) {
            log.error("INTERCEPTOR: idEntreprise NOT SET IN MDC - SQL returned WITHOUT filter! SQL: {}", sql);
            return sql;
        }
        
        log.debug("INTERCEPTOR: idEntreprise={} | SQL: {}", idEntreprise, sql);

        int idEntrepriseValue;
        try {
            idEntrepriseValue = Integer.parseInt(idEntreprise);
            log.debug("Parsed idEntreprise: {}", idEntrepriseValue);
        } catch (NumberFormatException e) {
            log.error("INTERCEPTOR: Failed to parse idEntreprise '{}' - SQL returned WITHOUT filter! SQL: {}", idEntreprise, sql);
            return sql;
        }

        if (IDENTREPRISE_PATTERN.matcher(sql).find()) {
            return sql;
        }

        TableInfo tableInfo = extractTableInfo(sql);
        if (tableInfo.tableName == null) {
            return sql;
        }

        if (EXCLUDED_TABLES.contains(tableInfo.tableName.toLowerCase()) || 
            (tableInfo.alias != null && EXCLUDED_TABLES.contains(tableInfo.alias.toLowerCase()))) {
            return sql;
        }

        String aliasOrTable = tableInfo.alias != null ? tableInfo.alias : tableInfo.tableName;
        String filterCondition = "(" + aliasOrTable + ".identreprise = " + idEntrepriseValue + " OR " + aliasOrTable + ".identreprise IS NULL)";
        
        String result;
        boolean hasWhere = WHERE_PATTERN.matcher(sql).find();
        log.debug("HAS WHERE: {}, fromIndex calculation...", hasWhere);
        
        if (hasWhere) {
            result = sql + " AND " + filterCondition;
        } else {
            int insertPos = findEndOfFromClause(sql);
            log.debug("insertPos={} for SQL: {}", insertPos, sql);
            if (insertPos > 0) {
                result = sql.substring(0, insertPos) + " WHERE " + filterCondition + " " + sql.substring(insertPos);
            } else {
                result = sql + " WHERE " + filterCondition;
            }
        }
        log.debug("INTERCEPTOR MODIFIED: {}", result);
        return result;
    }

    private static class TableInfo {
        String tableName;
        String alias;
        
        TableInfo(String tableName, String alias) {
            this.tableName = tableName;
            this.alias = alias;
        }
    }

    private TableInfo extractTableInfo(String sql) {
        Pattern p = Pattern.compile(
            "(?i)\\bfrom\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s+(?:as\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)?",
            Pattern.CASE_INSENSITIVE
        );
        
        String normalizedSql = removeSubqueries(sql);
        Matcher matcher = p.matcher(normalizedSql);
        
        if (matcher.find()) {
            String tableName = matcher.group(1);
            String alias = matcher.group(2);
            log.debug("EXTRACTED TABLE: {} alias: {} from SQL: {}", tableName, alias, sql);
            return new TableInfo(tableName, alias);
        }
        
        log.error("INTERCEPTOR: Could not extract table info from SQL: {}", sql);
        return new TableInfo(null, null);
    }

    private String removeSubqueries(String sql) {
        StringBuilder result = new StringBuilder();
        int parenthesisCount = 0;
        boolean inSubquery = false;
        int selectCount = 0;
        
        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            
            if (c == '(' && !inSubquery) {
                String beforeParen = sql.substring(Math.max(0, i - 7), i).toLowerCase();
                if (beforeParen.contains("select")) {
                    inSubquery = true;
                    selectCount++;
                }
            }
            
            if (inSubquery) {
                if (c == '(') parenthesisCount++;
                else if (c == ')') {
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

    private boolean isJoinKeyword(String word) {
        String lower = word.toLowerCase();
        return "join".equals(lower) || "from".equals(lower) || "set".equals(lower) || 
               "values".equals(lower) || "on".equals(lower);
    }

    private String findAliasForFilteredTable(String sql, Set<String> tables) {
        if (tables.size() == 1) {
            return tables.iterator().next();
        }
        
        Pattern aliasPattern = Pattern.compile(
            "(?i)\\bfrom\\s+([a-zA-Z_][a-zA-Z0-9_]*)\\s+(?:as\\s+)?([a-zA-Z_][a-zA-Z0-9_]*)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = aliasPattern.matcher(sql);
        while (matcher.find()) {
            String table = matcher.group(1);
            String alias = matcher.group(2);
            
            if (alias != null && !EXCLUDED_TABLES.contains(alias.toLowerCase())) {
                return alias;
            } else if (!EXCLUDED_TABLES.contains(table.toLowerCase()) && alias == null) {
                return table;
            }
        }
        
        return tables.iterator().next();
    }

    private int findMainFromIndex(String sql) {
        Pattern pattern = Pattern.compile("(?i)\\bfrom\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        
        while (matcher.find()) {
            int pos = matcher.start();
            if (pos <= 6) {
                return pos;
            }
            String before = sql.substring(Math.max(0, pos - 7), pos).toLowerCase();
            
            if (!before.contains("select")) {
                return pos;
            }
        }
        
        return -1;
    }
    
    private int findEndOfFromClause(String sql) {
        Pattern pattern = Pattern.compile("(?i)\\bfrom\\s+[a-zA-Z_][a-zA-Z0-9_]*(?:\\s+(?:as\\s+)?[a-zA-Z_][a-zA-Z0-9_]*)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        
        if (matcher.find()) {
            return matcher.end();
        }
        
        return sql.toLowerCase().indexOf(" from ");
    }
}
