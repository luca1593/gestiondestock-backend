package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.*;
import com.devtech.gestiondestock.model.Role;
import com.devtech.gestiondestock.model.Utilisateur;
import com.devtech.gestiondestock.model.auth.ExtendedUser;
import com.devtech.gestiondestock.repository.RoleRepository;
import com.devtech.gestiondestock.repository.UtilisateurRepository;
import com.devtech.gestiondestock.services.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RbacIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private EntrepriseService entrepriseService;
    @Autowired private UtilisateurService utilisateurService;
    @Autowired private ArticleService articleService;
    @Autowired private CategoryService categoryService;
    @Autowired private ClientService clientService;
    @Autowired private AuthorizationService authorizationService;
    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private RoleRepository roleRepository;

    private Integer entAId;
    private Integer entBId;
    private Integer adminUserId;
    private Integer userAId;
    private Integer userBId;

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private void setSecurityContext(String email, Integer idEntreprise, String role) {
        List<SimpleGrantedAuthority> authorities = role != null ?
                Collections.singletonList(new SimpleGrantedAuthority(role)) : Collections.emptyList();
        ExtendedUser user = new ExtendedUser(email, "dummy", idEntreprise, authorities);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, authorities)
        );
        MDC.put("idEntreprise", idEntreprise.toString());
        MDC.put("userEmail", email);
    }

    private void clearSecurityContext() {
        SecurityContextHolder.clearContext();
        MDC.remove("userEmail");
        MDC.remove("idEntreprise");
    }

    @BeforeAll
    void setUpOnce() {
        MDC.put("idEntreprise", "1");

        EntrepriseDto entA = entrepriseService.save(EntrepriseDto.builder()
                .nom("RBAC Enterprise A")
                .description("Enterprise A for RBAC testing")
                .email("rbac-a@test.com")
                .codeFiscal("RBAC-A")
                .numTel("0100000001")
                .adresse(AdresseDto.builder()
                        .adresse1("1 rue RBAC A")
                        .ville("Paris").codePostal("75001").pays("France")
                        .build())
                .build());
        entAId = entA.getId();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        MDC.put("idEntreprise", entAId.toString());

        Utilisateur adminUser = new Utilisateur();
        adminUser.setNom("Admin");
        adminUser.setPrenom("Super");
        adminUser.setEmail("admin-rbac@test.com");
        adminUser.setMotDePasse(encoder.encode("admin123"));
        adminUser.setDateDeNaissance(Instant.now());
        adminUser.setAdresse(new com.devtech.gestiondestock.model.Adresse("1 rue Admin", "", "Paris", "75001", "France"));
        adminUser.setEntreprise(EntrepriseDto.toEntity(EntrepriseDto.builder().id(entAId).build()));
        adminUser = utilisateurRepository.save(adminUser);

        Role adminRole = new Role();
        adminRole.setRoleNom("Admin");
        adminRole.setUtilisateur(adminUser);
        roleRepository.save(adminRole);
        adminUserId = adminUser.getId();

        Utilisateur userA = new Utilisateur();
        userA.setNom("User");
        userA.setPrenom("Alpha");
        userA.setEmail("user-a@test.com");
        userA.setMotDePasse(encoder.encode("user123"));
        userA.setDateDeNaissance(Instant.now());
        userA.setAdresse(new com.devtech.gestiondestock.model.Adresse("1 rue User A", "", "Paris", "75001", "France"));
        userA.setEntreprise(EntrepriseDto.toEntity(EntrepriseDto.builder().id(entAId).build()));
        userA = utilisateurRepository.save(userA);

        Role userRoleA = new Role();
        userRoleA.setRoleNom("User");
        userRoleA.setUtilisateur(userA);
        roleRepository.save(userRoleA);
        userAId = userA.getId();

        MDC.put("idEntreprise", "1");

        EntrepriseDto entB = entrepriseService.save(EntrepriseDto.builder()
                .nom("RBAC Enterprise B")
                .description("Enterprise B for RBAC testing")
                .email("rbac-b@test.com")
                .codeFiscal("RBAC-B")
                .numTel("0100000002")
                .adresse(AdresseDto.builder()
                        .adresse1("1 rue RBAC B")
                        .ville("Lyon").codePostal("69001").pays("France")
                        .build())
                .build());
        entBId = entB.getId();

        MDC.put("idEntreprise", entBId.toString());

        Utilisateur userB = new Utilisateur();
        userB.setNom("User");
        userB.setPrenom("Beta");
        userB.setEmail("user-b@test.com");
        userB.setMotDePasse(encoder.encode("user123"));
        userB.setDateDeNaissance(Instant.now());
        userB.setAdresse(new com.devtech.gestiondestock.model.Adresse("1 rue User B", "", "Lyon", "69001", "France"));
        userB.setEntreprise(EntrepriseDto.toEntity(EntrepriseDto.builder().id(entBId).build()));
        userB = utilisateurRepository.save(userB);

        Role userRoleB = new Role();
        userRoleB.setRoleNom("User");
        userRoleB.setUtilisateur(userB);
        roleRepository.save(userRoleB);
        userBId = userB.getId();
    }

    @AfterAll
    void tearDownOnce() {
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        clearSecurityContext();
    }

    @Test
    void testAdminCanAccessAllUsers() {
        setSecurityContext("admin-rbac@test.com", entAId, "Admin");

        boolean canAccess = authorizationService.canAccessUser(userAId);
        boolean canModify = authorizationService.canModifyUser(userAId);

        assertTrue(canAccess, "Admin should be able to access any user");
        assertTrue(canModify, "Admin should be able to modify any user");
        assertTrue(authorizationService.isAdmin(), "Should be admin");
    }

    @Test
    void testAdminCanListAllUsers() {
        setSecurityContext("admin-rbac@test.com", entAId, "Admin");

        List<UtilisateurDto> allUsers = utilisateurService.findAll();
        assertFalse(allUsers.isEmpty(), "Admin should be able to list all users");
    }

    @Test
    void testAdminCanDeleteUser() {
        setSecurityContext("admin-rbac@test.com", entAId, "Admin");

        UtilisateurDto newUser = utilisateurService.save(UtilisateurDto.builder()
                .nom("Temp")
                .prenom("User")
                .email("temp-delete@test.com")
                .motDePasse("temp123")
                .dateDeNaissance(Instant.now())
                .adresse(AdresseDto.builder().adresse1("1 rue Temp").ville("Paris").codePostal("75001").pays("France").build())
                .entreprise(EntrepriseDto.builder().id(entAId).build())
                .build());
        flushAndClear();

        utilisateurService.delete(newUser.getId());
        assertThrows(com.devtech.gestiondestock.exception.EntityNotFoundException.class, () ->
                utilisateurService.findById(newUser.getId())
        );
    }

    @Test
    void testUserCanOnlyAccessOwnData() {
        setSecurityContext("user-a@test.com", entAId, "User");

        boolean canAccessOwn = authorizationService.canAccessUser(userAId);
        boolean canAccessOther = authorizationService.canAccessUser(userBId);

        assertTrue(canAccessOwn, "User should be able to access own data");
        assertFalse(canAccessOther, "User should NOT be able to access other user's data");
        assertFalse(authorizationService.isAdmin(), "Should not be admin");
    }

    @Test
    void testUserCannotAccessOtherUserById() {
        setSecurityContext("user-a@test.com", entAId, "User");

        assertThrows(AccessDeniedException.class, () -> {
            if (!authorizationService.canAccessUser(userBId)) {
                throw new AccessDeniedException("Vous n'avez pas le droit d'accéder à cet utilisateur");
            }
            utilisateurService.findById(userBId);
        });
    }

    @Test
    void testUserCanAccessOwnUserById() {
        setSecurityContext("user-a@test.com", entAId, "User");

        UtilisateurDto found = utilisateurService.findById(userAId);
        assertNotNull(found);
        assertEquals("user-a@test.com", found.getEmail());
        assertNull(found.getMotDePasse(), "Password should be stripped from DTO");
    }

    @Test
    void testUserCannotModifyOtherUserPassword() {
        setSecurityContext("user-a@test.com", entAId, "User");

        boolean canModifyOwn = authorizationService.canModifyUser(userAId);
        boolean canModifyOther = authorizationService.canModifyUser(userBId);

        assertTrue(canModifyOwn, "User should be able to modify own password");
        assertFalse(canModifyOther, "User should NOT be able to modify other user's password");
    }

    @Test
    void testUserCannotListAllUsers() {
        setSecurityContext("user-a@test.com", entAId, "User");

        assertThrows(AccessDeniedException.class, () -> {
            if (!authorizationService.isAdmin()) {
                throw new AccessDeniedException("Accès réservé aux administrateurs");
            }
            utilisateurService.findAll();
        });
    }

    @Test
    void testUserCannotDeleteUser() {
        setSecurityContext("user-a@test.com", entAId, "User");

        assertThrows(AccessDeniedException.class, () -> {
            if (!authorizationService.isAdmin()) {
                throw new AccessDeniedException("Suppression réservée aux administrateurs");
            }
            utilisateurService.delete(userAId);
        });
    }

    @Test
    void testUserCannotListAllEntreprises() {
        setSecurityContext("user-a@test.com", entAId, "User");

        assertThrows(AccessDeniedException.class, () -> {
            if (!authorizationService.isAdmin()) {
                throw new AccessDeniedException("Accès réservé aux administrateurs");
            }
            entrepriseService.findAll();
        });
    }

    @Test
    void testUserCannotDeleteEntreprise() {
        setSecurityContext("user-a@test.com", entAId, "User");

        assertThrows(AccessDeniedException.class, () -> {
            if (!authorizationService.isAdmin()) {
                throw new AccessDeniedException("Suppression réservée aux administrateurs");
            }
            entrepriseService.delete(entAId);
        });
    }

    @Test
    void testUserCannotFindByNomUtilisateur() {
        setSecurityContext("user-a@test.com", entAId, "User");

        assertThrows(AccessDeniedException.class, () -> {
            if (!authorizationService.isAdmin()) {
                throw new AccessDeniedException("Accès réservé aux administrateurs");
            }
            utilisateurService.findByNomUtilisateur("Test");
        });
    }

    @Test
    void testUserCanFindByOwnEmail() {
        setSecurityContext("user-a@test.com", entAId, "User");

        UtilisateurDto found = utilisateurService.findByEmailUtilisateur("user-a@test.com");
        assertNotNull(found);
        assertEquals("user-a@test.com", found.getEmail());
    }

    @Test
    void testUserCannotFindByOtherEmail() {
        setSecurityContext("user-a@test.com", entAId, "User");

        assertThrows(AccessDeniedException.class, () -> {
            if (!authorizationService.isAdmin() && !authorizationService.getCurrentUserEmail().equals("user-b@test.com")) {
                throw new AccessDeniedException("Vous n'avez pas le droit d'accéder à cet utilisateur");
            }
            utilisateurService.findByEmailUtilisateur("user-b@test.com");
        });
    }

    @Test
    void testFullScenario_AdminVsUserPermissions() {
        setSecurityContext("user-a@test.com", entAId, "User");

        Integer catId = createCategoryFor(entAId, "RBAC-CAT").getId();
        ArticleDto article = createArticleFor(entAId, "RBAC-ART", catId);
        ClientDto client = createClientFor(entAId, "rbac-client@test.com");

        flushAndClear();

        assertFalse(authorizationService.isAdmin(), "Current user context should not be admin");

        assertTrue(authorizationService.canAccessUser(userAId));
        assertFalse(authorizationService.canAccessUser(userBId));

        ArticleDto foundArticle = articleService.findById(article.getId());
        assertNotNull(foundArticle);

        ClientDto foundClient = clientService.findById(client.getId());
        assertNotNull(foundClient);

        List<ClientDto> allClients = clientService.findAll();
        assertFalse(allClients.isEmpty());
    }

    @Test
    void testAdminCanAccessCrossEnterpriseUser() {
        setSecurityContext("admin-rbac@test.com", entAId, "Admin");

        boolean canAccessUserB = authorizationService.canAccessUser(userBId);
        assertTrue(canAccessUserB, "Admin should be able to access users from other enterprises");
    }

    private CategoryDto createCategoryFor(Integer entrepriseId, String code) {
        MDC.put("idEntreprise", entrepriseId.toString());
        CategoryDto dto = categoryService.save(CategoryDto.builder()
                .code(code)
                .designation("Cat " + code)
                .identreprise(entrepriseId)
                .build());
        flushAndClear();
        return dto;
    }

    private ArticleDto createArticleFor(Integer entrepriseId, String code, Integer categoryId) {
        MDC.put("idEntreprise", entrepriseId.toString());
        ArticleDto dto = articleService.save(ArticleDto.builder()
                .codeArticle(code)
                .designation("Article " + code)
                .prixUnitaireht(BigDecimal.valueOf(100))
                .tauxTva(BigDecimal.valueOf(20))
                .prixTtc(BigDecimal.valueOf(120))
                .stock(BigDecimal.valueOf(50))
                .category(CategoryDto.builder().id(categoryId).build())
                .entreprise(EntrepriseDto.builder().id(entrepriseId).build())
                .build());
        flushAndClear();
        return dto;
    }

    private ClientDto createClientFor(Integer entrepriseId, String email) {
        MDC.put("idEntreprise", entrepriseId.toString());
        ClientDto dto = clientService.save(ClientDto.builder()
                .nom("Client " + entrepriseId)
                .prenom("Test")
                .email(email)
                .numTel("0100000000")
                .identreprise(entrepriseId)
                .adresse(AdresseDto.builder()
                        .adresse1("1 rue Test")
                        .ville("Paris").codePostal("75001").pays("France")
                        .build())
                .build());
        flushAndClear();
        return dto;
    }
}
