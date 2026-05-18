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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final RoleRepository roleRepository;

    public DataInitializer(UtilisateurRepository utilisateurRepository,
                           EntrepriseRepository entrepriseRepository,
                           RoleRepository roleRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
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
}
