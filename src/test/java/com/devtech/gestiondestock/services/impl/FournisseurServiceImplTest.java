package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.CommandeFournisseurDto;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Fournisseur;
import com.devtech.gestiondestock.repository.FournisseurRepository;
import com.devtech.gestiondestock.services.CommandeFournisseurService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * Tests unitaires (Mockito, sans contexte Spring) de {@link FournisseurServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class FournisseurServiceImplTest {

    @Mock
    private FournisseurRepository fournisseurRepository;
    @Mock
    private CommandeFournisseurService commandeFournisseurService;

    @InjectMocks
    private FournisseurServiceImpl fournisseurService;

    private Fournisseur fournisseur;

    @BeforeEach
    void setUp() {
        fournisseur = new Fournisseur();
        fournisseur.setId(1);
        fournisseur.setNom("Transporteur");
        fournisseur.setPrenom("Paul");
        fournisseur.setEmail("contact@transporteur.com");
        fournisseur.setNumTel("0102030405");
        fournisseur.setIdentreprise(1);
    }

    private FournisseurDto fournisseurDtoValide() {
        return FournisseurDto.builder()
                .id(1)
                .nom("Transporteur")
                .prenom("Paul")
                .email("contact@transporteur.com")
                .numTel("0102030405")
                .adresse(AdresseDto.builder()
                        .adresse1("10 avenue des Champs")
                        .ville("Lyon")
                        .codePostal("69001")
                        .pays("France")
                        .build())
                .identreprise(1)
                .build();
    }

    // ------------------------------------------------------------------
    // save(...)
    // ------------------------------------------------------------------

    @Test
    void save_whenDtoInvalide_shouldThrowInvalidEntityException() {
        FournisseurDto dto = FournisseurDto.builder().build();

        assertThrows(InvalidEntityException.class, () -> fournisseurService.save(dto));

        verifyNoInteractions(fournisseurRepository);
    }

    @Test
    void save_whenDtoValide_shouldReturnSavedDto() {
        FournisseurDto dto = fournisseurDtoValide();
        when(fournisseurRepository.save(any(Fournisseur.class))).thenReturn(fournisseur);

        FournisseurDto result = fournisseurService.save(dto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Transporteur", result.getNom());
        verify(fournisseurRepository, times(1)).save(any(Fournisseur.class));
    }

    // ------------------------------------------------------------------
    // findById(...)
    // ------------------------------------------------------------------

    @Test
    void findById_whenIdNull_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> fournisseurService.findById(null));
        verifyNoInteractions(fournisseurRepository);
    }

    @Test
    void findById_whenFournisseurInexistant_shouldThrowEntityNotFoundException() {
        when(fournisseurRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> fournisseurService.findById(99));
    }

    @Test
    void findById_whenFournisseurExistant_shouldReturnDto() {
        when(fournisseurRepository.findById(1)).thenReturn(Optional.of(fournisseur));

        FournisseurDto result = fournisseurService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Transporteur", result.getNom());
    }

    // ------------------------------------------------------------------
    // findByNomFournisseur(...)
    // ------------------------------------------------------------------

    @Test
    void findByNomFournisseur_whenNomVide_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> fournisseurService.findByNomFournisseur(null));
        assertThrows(EntityNotFoundException.class, () -> fournisseurService.findByNomFournisseur(""));
        verifyNoInteractions(fournisseurRepository);
    }

    @Test
    void findByNomFournisseur_whenFournisseurInexistant_shouldThrowEntityNotFoundException() {
        when(fournisseurRepository.findFournisseurByNom("Inconnu")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> fournisseurService.findByNomFournisseur("Inconnu"));
    }

    @Test
    void findByNomFournisseur_whenFournisseurExistant_shouldReturnDto() {
        when(fournisseurRepository.findFournisseurByNom("Transporteur")).thenReturn(Optional.of(fournisseur));

        FournisseurDto result = fournisseurService.findByNomFournisseur("Transporteur");

        assertNotNull(result);
        assertEquals("Transporteur", result.getNom());
    }

    // ------------------------------------------------------------------
    // findByEmailFournisseur(...)
    // ------------------------------------------------------------------

    @Test
    void findByEmailFournisseur_whenEmailVide_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> fournisseurService.findByEmailFournisseur(null));
        assertThrows(EntityNotFoundException.class, () -> fournisseurService.findByEmailFournisseur(""));
        verifyNoInteractions(fournisseurRepository);
    }

    @Test
    void findByEmailFournisseur_whenFournisseurExistant_shouldReturnDto() {
        when(fournisseurRepository.findFournisseurByEmail("contact@transporteur.com"))
                .thenReturn(Optional.of(fournisseur));

        FournisseurDto result = fournisseurService.findByEmailFournisseur("contact@transporteur.com");

        assertNotNull(result);
        assertEquals("contact@transporteur.com", result.getEmail());
    }

    // ------------------------------------------------------------------
    // findAll()
    // ------------------------------------------------------------------

    @Test
    void findAll_whenListeVide_shouldReturnListeVide() {
        when(fournisseurRepository.findAll()).thenReturn(Collections.emptyList());

        List<FournisseurDto> result = fournisseurService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_whenListeNonVide_shouldReturnDtos() {
        when(fournisseurRepository.findAll()).thenReturn(Arrays.asList(fournisseur));

        List<FournisseurDto> result = fournisseurService.findAll();

        assertEquals(1, result.size());
        assertEquals("Transporteur", result.get(0).getNom());
    }

    // ------------------------------------------------------------------
    // delete(...)
    // ------------------------------------------------------------------

    @Test
    void delete_whenIdNull_shouldThrowEntityNotFoundExceptionSansAppelRepository() {
        assertThrows(EntityNotFoundException.class, () -> fournisseurService.delete(null));
        verifyNoInteractions(fournisseurRepository);
    }

    @Test
    void delete_whenFournisseurPossedeCommandes_shouldThrowInvalidOpperatioException() {
        when(fournisseurRepository.findById(1)).thenReturn(Optional.of(fournisseur));
        when(commandeFournisseurService.findAllByFournisseurDto(any(FournisseurDto.class)))
                .thenReturn(Arrays.asList(CommandeFournisseurDto.builder().id(42).build()));

        assertThrows(InvalidOpperatioException.class, () -> fournisseurService.delete(1));

        verify(fournisseurRepository, never()).deleteById(anyInt());
    }

    @Test
    void delete_whenFournisseurLibre_shouldDeleteById() {
        when(fournisseurRepository.findById(1)).thenReturn(Optional.of(fournisseur));
        when(commandeFournisseurService.findAllByFournisseurDto(any(FournisseurDto.class)))
                .thenReturn(Collections.emptyList());

        fournisseurService.delete(1);

        verify(fournisseurRepository, times(1)).deleteById(1);
    }
}
