package com.devtech.gestiondestock.config;

import com.devtech.gestiondestock.model.Adresse;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Role;
import com.devtech.gestiondestock.model.Utilisateur;
import com.devtech.gestiondestock.repository.EntrepriseRepository;
import com.devtech.gestiondestock.repository.RoleRepository;
import com.devtech.gestiondestock.repository.UtilisateurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final RoleRepository roleRepository;
    private final JdbcTemplate jdbcTemplate;

    public DataInitializer(UtilisateurRepository utilisateurRepository,
                           EntrepriseRepository entrepriseRepository,
                           RoleRepository roleRepository,
                           JdbcTemplate jdbcTemplate) {
        this.utilisateurRepository = utilisateurRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.roleRepository = roleRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        fixAutoIncrement();
        if (utilisateurRepository.findUtilisateurByEmail("admin@default.com").isPresent()) {
            log.info("Admin user already exists, skipping initialization");
            return;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Entreprise entreprise = entrepriseRepository.findEntrepriseByEmail("admin@default.com")
                .orElseGet(() -> {
                    Entreprise e = new Entreprise();
                    e.setNom("Default Enterprise");
                    e.setCodeFiscal("DEFAULT001");
                    e.setEmail("admin@default.com");
                    e.setNumTel("0123456789");
                    e.setSiteWeb("https://default.com");
                    e.setAdresse(new Adresse("1 rue de la Paix", "", "Paris", "75001", "France"));
                    return entrepriseRepository.save(e);
                });

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom("Admin");
        utilisateur.setPrenom("Super");
        utilisateur.setEmail("admin@default.com");
        utilisateur.setMotDePasse(encoder.encode("admin123"));
        utilisateur.setDateDeNaissance(Instant.now());
        utilisateur.setAdresse(new Adresse("1 rue de la Paix", "", "Paris", "75001", "France"));
        utilisateur.setEntreprise(entreprise);
        utilisateur = utilisateurRepository.save(utilisateur);

        Role role = new Role();
        role.setRoleNom("Admin");
        role.setUtilisateur(utilisateur);
        roleRepository.save(role);

        log.info("Default admin user created: admin@default.com / admin123");
    }

    private void fixAutoIncrement() {
        try {
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
            List<String> tables = jdbcTemplate.queryForList(
                "SELECT TABLE_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'gestiondestock' AND COLUMN_NAME = 'id' AND COLUMN_TYPE LIKE '%int%' AND EXTRA NOT LIKE '%auto_increment%'",
                String.class
            );
            if (!tables.isEmpty()) {
                log.info("Fixing AUTO_INCREMENT on {} tables: {}", tables.size(), tables);
                for (String table : tables) {
                    jdbcTemplate.execute("ALTER TABLE " + table + " MODIFY id BIGINT NOT NULL AUTO_INCREMENT");
                }
            }
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
        } catch (Exception e) {
            log.warn("Could not fix AUTO_INCREMENT (non-critical): {}", e.getMessage());
        }
    }
}
