package com.devtech.gestiondestock.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class InterceptorTest {

    private Interceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new Interceptor();
        MDC.clear();
    }

    @Test
    void testSelectArticleWithWhereClause() {
        MDC.put("idEntreprise", "1");
        String sql = "select article0_.id as id1_0_, article0_.codearticle as codearti2_0_ from article article0_ where article0_.identreprise is null";
        
        String result = interceptor.inspect(sql);
        
        assertTrue(result.contains("AND article0_.identreprise = 1"));
    }

    @Test
    void testSelectArticleWithoutWhereClause() {
        MDC.put("idEntreprise", "2");
        String sql = "select article0_.id as id1_0_ from article article0_";
        
        String result = interceptor.inspect(sql);
        
        assertTrue(result.contains("WHERE article0_.identreprise = 2"));
    }

    @Test
    void testSelectWithJoin() {
        MDC.put("idEntreprise", "3");
        String sql = "select a.id, c.designation from article a inner join category c on a.idCategory=c.id";
        
        String result = interceptor.inspect(sql);
        
        assertTrue(result.contains("WHERE a.identreprise = 3"));
    }

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
    void testSelectOnExcludedTableUtilisateur() {
        MDC.put("idEntreprise", "1");
        String sql = "select u.id, u.email from utilisateur u";
        
        String result = interceptor.inspect(sql);
        
        assertEquals(sql, result);
    }

    @Test
    void testNoIdEntrepriseInMDC() {
        String sql = "select article0_.id from article article0_";
        
        String result = interceptor.inspect(sql);
        
        assertEquals(sql, result);
    }

    @Test
    void testInvalidIdEntreprise() {
        MDC.put("idEntreprise", "notANumber");
        String sql = "select article0_.id from article article0_";
        
        String result = interceptor.inspect(sql);
        
        assertEquals(sql, result);
    }

    @Test
    void testSqlAlreadyHasIdentrepriseFilter() {
        MDC.put("idEntreprise", "1");
        String sql = "select article0_.id from article article0_ where article0_.identreprise = 5";
        
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

    @Test
    void testSelectWithSubquery() {
        MDC.put("idEntreprise", "1");
        String sql = "select a.id from article a";
        
        String result = interceptor.inspect(sql);
        
        assertTrue(result.contains("WHERE a.identreprise = 1"));
    }

    @Test
    void testSelectWithMultipleJoins() {
        MDC.put("idEntreprise", "2");
        String sql = "select a.id, c.nom as category, e.nom asentreprise from article a left join category c on a.idCategory=c.id left join entreprise e on a.identreprise=e.id";
        
        String result = interceptor.inspect(sql);
        
        assertTrue(result.contains("WHERE a.identreprise = 2"));
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

    @Test
    void testSelectClient() {
        MDC.put("idEntreprise", "1");
        String sql = "select c.id, c.nom from client c";
        
        String result = interceptor.inspect(sql);
        
        assertTrue(result.contains("WHERE c.identreprise = 1"));
    }

    @Test
    void testSelectFournisseur() {
        MDC.put("idEntreprise", "3");
        String sql = "select f.id, f.raisonsociale from fournisseur f";
        
        String result = interceptor.inspect(sql);
        
        assertTrue(result.contains("WHERE f.identreprise = 3"));
    }

    @Test
    void testSelectCommandeFournisseur() {
        MDC.put("idEntreprise", "1");
        String sql = "select cf.id, cf.code from commandefournisseur cf";
        
        String result = interceptor.inspect(sql);
        
        assertTrue(result.contains("WHERE cf.identreprise = 1"));
    }
}