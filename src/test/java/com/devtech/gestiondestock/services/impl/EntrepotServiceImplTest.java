package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.EntrepotDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.model.Entreprise;
import com.devtech.gestiondestock.model.Entrepot;
import com.devtech.gestiondestock.repository.EntrepotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito, sans contexte Spring) de {@link EntrepotServiceImpl}.
 *
 * <p>Particularite : ce service ne possede aucune validation de DTO. Les cas
 * "DTO invalide" n'existent donc pas et l'id n'est pas controle en entree
 * (voir le test de suppression avec un id null).</p>
 */
@ExtendWith(MockitoExtension.class)
class EntrepotServiceImplTest {

    @Mock
    private EntrepotRepository entrepotRepository;

    @InjectMocks
    private EntrepotServiceImpl entrepotService;

    private Entrepot entrepotAvecEntreprise() {
        Entreprise entreprise = new Entreprise();
        entreprise.setId(1);
        entreprise.setNom("MaSociete");

        Entrepot entrepot = new Entrepot();
        entrepot.setId(10);
        entrepot.setCode("ENT-001");
        entrepot.setNom("Entrepot principal");
        entrepot.setDescription("Entrepot de test");
        entrepot.setEstPrincipal(true);
        entrepot.setEntreprise(entreprise);
        return entrepot;
    }

    // ------------------------------------------------------------------
    // save(...)
    // ------------------------------------------------------------------

    @Test
    void save_shouldMapDtoToEntityAndReturnSavedDto() {
        EntrepotDto dto = new EntrepotDto();
        dto.setCode("ENT-001");
        dto.setNom("Entrepot principal");
        dto.setDescription("Entrepot de test");
        dto.setEstPrincipal(true);
        dto.setEntrepriseId(1);

        when(entrepotRepository.save(any(Entrepot.class))).thenReturn(entrepotAvecEntreprise());

        EntrepotDto result = entrepotService.save(dto);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("ENT-001", result.getCode());
        assertEquals("Entrepot principal", result.getNom());
        assertEquals(1, result.getEntrepriseId());
        assertEquals("MaSociete", result.getEntrepriseNom());

        // L'entreprise liee a l'entite persistee ne porte que l'id fourni par le DTO
        ArgumentCaptor<Entrepot> captor = ArgumentCaptor.forClass(Entrepot.class);
        verify(entrepotRepository, times(1)).save(captor.capture());
        assertNotNull(captor.getValue().getEntreprise());
        assertEquals(1, captor.getValue().getEntreprise().getId());
    }

    @Test
    void save_whenEntrepriseIdNull_shouldNotLierEntreprise() {
        EntrepotDto dto = new EntrepotDto();
        dto.setCode("ENT-002");
        dto.setNom("Entrepot secondaire");

        Entrepot entrepot = new Entrepot();
        entrepot.setId(11);
        entrepot.setCode("ENT-002");
        entrepot.setNom("Entrepot secondaire");
        when(entrepotRepository.save(any(Entrepot.class))).thenReturn(entrepot);

        EntrepotDto result = entrepotService.save(dto);

        assertEquals(11, result.getId());
        assertNull(result.getEntrepriseId());
        assertNull(result.getEntrepriseNom());

        ArgumentCaptor<Entrepot> captor = ArgumentCaptor.forClass(Entrepot.class);
        verify(entrepotRepository).save(captor.capture());
        assertNull(captor.getValue().getEntreprise());
    }

    // ------------------------------------------------------------------
    // findById(...)
    // ------------------------------------------------------------------

    @Test
    void findById_whenIdNull_shouldThrowEntityNotFoundException() {
        // Aucun controle d'id : le repository renvoie Optional.empty() (defaut Mockito)
        assertThrows(EntityNotFoundException.class, () -> entrepotService.findById(null));
    }

    @Test
    void findById_whenEntrepotInexistant_shouldThrowEntityNotFoundException() {
        when(entrepotRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> entrepotService.findById(99));
    }

    @Test
    void findById_whenEntrepotExistant_shouldReturnDto() {
        when(entrepotRepository.findById(10)).thenReturn(Optional.of(entrepotAvecEntreprise()));

        EntrepotDto result = entrepotService.findById(10);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("ENT-001", result.getCode());
        assertEquals(1, result.getEntrepriseId());
    }

    // ------------------------------------------------------------------
    // findAll()
    // ------------------------------------------------------------------

    @Test
    void findAll_whenListeVide_shouldReturnListeVide() {
        when(entrepotRepository.findAll()).thenReturn(Collections.emptyList());

        List<EntrepotDto> result = entrepotService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_whenListeNonVide_shouldReturnDtos() {
        when(entrepotRepository.findAll()).thenReturn(Arrays.asList(entrepotAvecEntreprise()));

        List<EntrepotDto> result = entrepotService.findAll();

        assertEquals(1, result.size());
        assertEquals("ENT-001", result.get(0).getCode());
    }

    // ------------------------------------------------------------------
    // findByEntreprise(...)
    // ------------------------------------------------------------------

    @Test
    void findByEntreprise_shouldReturnDtos() {
        when(entrepotRepository.findByEntrepriseId(1)).thenReturn(Arrays.asList(entrepotAvecEntreprise()));

        List<EntrepotDto> result = entrepotService.findByEntreprise(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getEntrepriseId());
    }

    @Test
    void findByEntreprise_whenAucunResultat_shouldReturnListeVide() {
        when(entrepotRepository.findByEntrepriseId(2)).thenReturn(Collections.emptyList());

        List<EntrepotDto> result = entrepotService.findByEntreprise(2);

        assertTrue(result.isEmpty());
    }

    // ------------------------------------------------------------------
    // delete(...)
    // ------------------------------------------------------------------

    @Test
    void delete_shouldCallRepository() {
        entrepotService.delete(10);

        verify(entrepotRepository, times(1)).deleteById(10);
    }

    @Test
    void delete_whenIdNull_shouldStillCallRepository() {
        // Pas de garde sur l'id : le service delegue tel quel au repository
        entrepotService.delete(null);

        verify(entrepotRepository, times(1)).deleteById(null);
    }
}
