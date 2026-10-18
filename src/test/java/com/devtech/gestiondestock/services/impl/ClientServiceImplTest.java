package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.AdresseDto;
import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.CommandeClientDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.repository.ClientRepository;
import com.devtech.gestiondestock.services.CommandeClientService;
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
 * Tests unitaires (Mockito, sans contexte Spring) de {@link ClientServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private CommandeClientService commandeClientService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setId(1);
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setEmail("jean.dupont@test.com");
        client.setNumTel("0601020304");
        client.setIdentreprise(1);
    }

    private ClientDto clientDtoValide() {
        return ClientDto.builder()
                .id(1)
                .nom("Dupont")
                .prenom("Jean")
                .email("jean.dupont@test.com")
                .numTel("0601020304")
                .adresse(AdresseDto.builder()
                        .adresse1("1 rue de la Paix")
                        .ville("Paris")
                        .codePostal("75001")
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
        ClientDto dto = ClientDto.builder().build();

        assertThrows(InvalidEntityException.class, () -> clientService.save(dto));

        verifyNoInteractions(clientRepository);
    }

    @Test
    void save_whenDtoValide_shouldReturnSavedDto() {
        ClientDto dto = clientDtoValide();
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        ClientDto result = clientService.save(dto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Dupont", result.getNom());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    // ------------------------------------------------------------------
    // findById(...)
    // ------------------------------------------------------------------

    @Test
    void findById_whenIdNull_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> clientService.findById(null));
        verifyNoInteractions(clientRepository);
    }

    @Test
    void findById_whenClientInexistant_shouldThrowEntityNotFoundException() {
        when(clientRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clientService.findById(99));
    }

    @Test
    void findById_whenClientExistant_shouldReturnDto() {
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));

        ClientDto result = clientService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Dupont", result.getNom());
        assertEquals("jean.dupont@test.com", result.getEmail());
    }

    // ------------------------------------------------------------------
    // findByNomClient(...)
    // ------------------------------------------------------------------

    @Test
    void findByNomClient_whenNomVide_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> clientService.findByNomClient(null));
        assertThrows(EntityNotFoundException.class, () -> clientService.findByNomClient(""));
        verifyNoInteractions(clientRepository);
    }

    @Test
    void findByNomClient_whenClientInexistant_shouldThrowEntityNotFoundException() {
        when(clientRepository.findClientByNom("Martin")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clientService.findByNomClient("Martin"));
    }

    @Test
    void findByNomClient_whenClientExistant_shouldReturnDto() {
        when(clientRepository.findClientByNom("Dupont")).thenReturn(Optional.of(client));

        ClientDto result = clientService.findByNomClient("Dupont");

        assertNotNull(result);
        assertEquals("Dupont", result.getNom());
    }

    // ------------------------------------------------------------------
    // findByEmailClient(...)
    // ------------------------------------------------------------------

    @Test
    void findByEmailClient_whenEmailVide_shouldThrowEntityNotFoundException() {
        assertThrows(EntityNotFoundException.class, () -> clientService.findByEmailClient(null));
        assertThrows(EntityNotFoundException.class, () -> clientService.findByEmailClient(""));
        verifyNoInteractions(clientRepository);
    }

    @Test
    void findByEmailClient_whenClientExistant_shouldReturnDto() {
        when(clientRepository.findClientByEmail("jean.dupont@test.com")).thenReturn(Optional.of(client));

        ClientDto result = clientService.findByEmailClient("jean.dupont@test.com");

        assertNotNull(result);
        assertEquals("jean.dupont@test.com", result.getEmail());
    }

    // ------------------------------------------------------------------
    // findAll()
    // ------------------------------------------------------------------

    @Test
    void findAll_whenListeVide_shouldReturnListeVide() {
        when(clientRepository.findAll()).thenReturn(Collections.emptyList());

        List<ClientDto> result = clientService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_whenListeNonVide_shouldReturnDtos() {
        when(clientRepository.findAll()).thenReturn(Arrays.asList(client));

        List<ClientDto> result = clientService.findAll();

        assertEquals(1, result.size());
        assertEquals("Dupont", result.get(0).getNom());
    }

    // ------------------------------------------------------------------
    // delete(...)
    // ------------------------------------------------------------------

    @Test
    void delete_whenIdNull_shouldThrowEntityNotFoundExceptionSansAppelRepository() {
        assertThrows(EntityNotFoundException.class, () -> clientService.delete(null));
        verifyNoInteractions(clientRepository);
    }

    @Test
    void delete_whenClientPossedeCommandes_shouldThrowInvalidOpperatioException() {
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        when(commandeClientService.findAllByClientDto(any(ClientDto.class)))
                .thenReturn(Arrays.asList(CommandeClientDto.builder().id(42).build()));

        assertThrows(InvalidOpperatioException.class, () -> clientService.delete(1));

        verify(clientRepository, never()).deleteById(anyInt());
    }

    @Test
    void delete_whenClientLibre_shouldDeleteById() {
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));
        when(commandeClientService.findAllByClientDto(any(ClientDto.class))).thenReturn(Collections.emptyList());

        clientService.delete(1);

        verify(clientRepository, times(1)).deleteById(1);
    }
}
