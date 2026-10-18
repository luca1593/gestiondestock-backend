package com.devtech.gestiondestock.interceptor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests du filtre multi-tenant.
 *
 * <p>Règle de sécurité (voir ADR-002) : dans un contexte authentifié
 * (MDC {@code userEmail} présent), l'absence d'entreprise valide doit faire
 * échouer la requête (fail-closed) au lieu de la laisser passer sans filtre.</p>
 */
class InterceptorTest {

    private Interceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new Interceptor();
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    // ---------- Filtrage nominal ----------

    @Test
    void testSelectArticleWithWhereClause() {
        MDC.put("idEntreprise", "1");
        String sql = "select article0_.id as id1_0_, article0_.codearticle as codearti2_0_ from article article0_ where article0_.identreprise is null";

        String result = interceptor.inspect(sql);

        assertTrue(result.contains("AND (article0_.identreprise = 1 OR article0_.identreprise IS NULL)"));
    }

    @Test
    void testSelectArticleWithoutWhereClause() {
        MDC.put("idEntreprise", "2");
        String sql = "select article0_.id as id1_0_ from article article0_";

        String result = interceptor.inspect(sql);

        assertTrue(result.contains("WHERE (article0_.identreprise = 2 OR article0_.identreprise IS NULL)"));
    }

    @Test
    void testSelectWithJoin() {
        MDC.put("idEntreprise", "3");
        String sql = "select a.id, c.designation from article a inner join category c on a.idCategory=c.id";

        String result = interceptor.inspect(sql);

        assertTrue(result.contains("WHERE (a.identreprise = 3 OR a.identreprise IS NULL)"));
    }

    @Test
    void testSelectWithMultipleJoins() {
        MDC.put("idEntreprise", "2");
        String sql = "select a.id, c.nom as category, e.nom as entreprise from article a left join category c on a.idCategory=c.id left join entreprise e on a.identreprise=e.id";

        String result = interceptor.inspect(sql);

        assertTrue(result.contains("WHERE (a.identreprise = 2 OR a.identreprise IS NULL)"));
        // La condition de JOIN sur identreprise ne doit PAS être confondue avec un filtre de sécurité.
        assertTrue(result.contains("left join entreprise e on a.identreprise=e.id"));
    }

    @Test
    void testSelectClient() {
        MDC.put("idEntreprise", "1");
        String sql = "select c.id, c.nom from client c";

        String result = interceptor.inspect(sql);

        assertTrue(result.contains("WHERE (c.identreprise = 1 OR c.identreprise IS NULL)"));
    }

    @Test
    void testSelectUtilisateurNowFiltered() {
        MDC.put("idEntreprise", "1");
        String sql = "select u.id, u.email from utilisateur u";

        String result = interceptor.inspect(sql);

        assertTrue(result.contains("WHERE (u.identreprise = 1 OR u.identreprise IS NULL)"));
    }

    @Test
    void testSelectUnAliasedTableAtEndOfQuery() {
        MDC.put("idEntreprise", "7");
        String sql = "select count(*) from utilisateur";

        String result = interceptor.inspect(sql);

        assertEquals("select count(*) from utilisateur WHERE (utilisateur.identreprise = 7 OR utilisateur.identreprise IS NULL)", result);
    }

    // ---------- Position du filtre (correction E-1) ----------

    @Test
    void testFilterInsertedBeforeOrderBy() {
        MDC.put("idEntreprise", "1");
        String sql = "select a.id from article a where a.actif = true order by a.id";

        String result = interceptor.inspect(sql);

        int andPos = result.indexOf("AND (a.identreprise = 1");
        int orderPos = result.indexOf("order by");
        assertTrue(andPos > 0, "le filtre doit être présent");
        assertTrue(andPos < orderPos, "le filtre doit précéder ORDER BY");
        assertFalse(result.contains("order by a.id AND"), "le filtre ne doit pas être ajouté après ORDER BY");
    }

    @Test
    void testFilterInsertedBeforeGroupBy() {
        MDC.put("idEntreprise", "4");
        String sql = "select a.id, count(*) from article a group by a.id";

        String result = interceptor.inspect(sql);

        assertTrue(result.contains("WHERE (a.identreprise = 4 OR a.identreprise IS NULL) group by a.id"));
    }

    @Test
    void testFilterInsertedBeforeLimit() {
        MDC.put("idEntreprise", "1");
        String sql = "select a.id from article a where a.actif = true limit 10";

        String result = interceptor.inspect(sql);

        assertTrue(result.contains("AND (a.identreprise = 1 OR a.identreprise IS NULL) limit 10"));
    }

    // ---------- Contextes système (non filtrés) ----------

    @Test
    void testContextSystemeSansMdcNonFiltre() {
        // Aucun MDC : login / démarrage / tâche planifiée -> pas de filtre.
        String sql = "select article0_.id from article article0_";

        String result = interceptor.inspect(sql);

        assertEquals(sql, result);
    }

    @Test
    void testEmptySql() {
        MDC.put("idEntreprise", "1");

        String result = interceptor.inspect("");

        assertEquals("", result);
    }

    @Test
    void testNullSql() {
        MDC.put("idEntreprise", "1");

        String result = interceptor.inspect(null);

        assertNull(result);
    }

    // ---------- Fail-closed (correction C-1) ----------

    @Test
    void testInvalidIdEntrepriseLeveUneException() {
        MDC.put("idEntreprise", "notANumber");
        String sql = "select article0_.id from article article0_";

        assertThrows(IllegalStateException.class, () -> interceptor.inspect(sql));
    }

    @Test
    void testUtilisateurAuthentifieSansEntrepriseLeveUneException() {
        // userEmail est posé par ApplicationRequestFilter quand un JWT a identifié l'utilisateur.
        MDC.put("userEmail", "admin@default.com");
        String sql = "select article0_.id from article article0_";

        assertThrows(IllegalStateException.class, () -> interceptor.inspect(sql));
    }

    // ---------- Tables exclues / requêtes non SELECT ----------

    @Test
    void testSelectOnExcludedTableEntreprise() {
        MDC.put("idEntreprise", "1");
        String sql = "select e.id, e.nom from entreprise e";

        String result = interceptor.inspect(sql);

        assertEquals(sql, result);
    }

    @Test
    void testSelectOnExcludedTableRoles() {
        MDC.put("idEntreprise", "1");
        String sql = "select r.id, r.nom from roles r";

        String result = interceptor.inspect(sql);

        assertEquals(sql, result);
    }

    @Test
    void testInsertStatementNotModified() {
        MDC.put("idEntreprise", "1");
        String sql = "insert into article (codearticle, designation) values ('A001', 'Article Test')";

        String result = interceptor.inspect(sql);

        assertEquals(sql, result);
    }

    @Test
    void testUpdateStatementNotModified() {
        MDC.put("idEntreprise", "1");
        String sql = "update article set designation='New Name' where id=1";

        String result = interceptor.inspect(sql);

        assertEquals(sql, result);
    }

    // ---------- Double filtrage ----------

    @Test
    void testSqlAlreadyHasIdentrepriseFilter() {
        MDC.put("idEntreprise", "1");
        String sql = "select article0_.id from article article0_ where article0_.identreprise = 5";

        String result = interceptor.inspect(sql);

        assertEquals(sql, result);
    }

    @Test
    void testCompteurFactureDejaFiltre() {
        // Requête JPQL explicite de CompteurFactureRepository : ne doit pas être filtrée deux fois.
        MDC.put("idEntreprise", "1");
        String sql = "select c.id, c.identreprise, c.annee, c.valeur from compteur_facture c where c.identreprise=? and c.annee=?";

        String result = interceptor.inspect(sql);

        assertEquals(sql, result);
    }

    // ---------- Requêtes non supportées : refus explicite ----------

    @Test
    void testUnionRefusee() {
        MDC.put("idEntreprise", "1");
        String sql = "select a.id from article a union select c.id from client c";

        assertThrows(IllegalStateException.class, () -> interceptor.inspect(sql));
    }
}
