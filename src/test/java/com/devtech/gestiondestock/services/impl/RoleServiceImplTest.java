package com.devtech.gestiondestock.services.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests unitaires de {@link RoleServiceImpl}.
 *
 * <p><strong>Particularite :</strong> cette classe est actuellement vide (aucun champ,
 * aucune dependance, aucune methode metier) et n'implemente meme pas l'interface
 * {@code RoleService} elle-meme vide. Il n'y a donc aucune logique isolable a verrouiller :
 * on se contente d'un test de fumee verifiant que la classe est instanciable.</p>
 */
class RoleServiceImplTest {

    @Test
    void instanciation_shouldBePossible() {
        RoleServiceImpl roleService = new RoleServiceImpl();

        assertNotNull(roleService);
    }
}
