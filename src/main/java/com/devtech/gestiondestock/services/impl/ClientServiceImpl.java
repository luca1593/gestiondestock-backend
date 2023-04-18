package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.CommandeClientDto;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.Client;
import com.devtech.gestiondestock.model.CommandeClient;
import com.devtech.gestiondestock.repository.ClientRepository;
import com.devtech.gestiondestock.services.ClientService;
import com.devtech.gestiondestock.services.CommandeClientService;
import com.devtech.gestiondestock.validator.ClientValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final CommandeClientService commandeClientService;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, CommandeClientService commandeClientService){
        this.clientRepository = clientRepository;
        this.commandeClientService = commandeClientService;
    }

    @Override
    public ClientDto save(ClientDto dto) {
        List<String> errors = ClientValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Client is invalid {}");
            throw new InvalidEntityException("Le client n'est pas valide", ErrorsCode.CLIENT_NOT_VALID, errors);
        }
        return ClientDto.fromEntity(
                clientRepository.save(ClientDto.toEntity(dto))
        );
    }

    @Override
    public ClientDto findById(Integer id) {
        if (id == null){
            log.error("Client ID is not null");
            return null;
        }
        Optional<Client> client = clientRepository.findById(id);
        return Optional.of(ClientDto.fromEntity(client.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun client trouver avec l'id = " + id + " dans la BDD",
                        ErrorsCode.CLIENT_NOT_FOUND
                )
        );
    }

    @Override
    public ClientDto findByNomClient(String nom) {
        if (!StringUtils.hasLength(nom)){
            log.error("Client nom is null");
            return null;
        }
        Optional<Client> client = clientRepository.findClientByNom(nom);
        return Optional.of(ClientDto.fromEntity(client.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun client trouver avec le nom  = " + nom + " dans la BDD",
                        ErrorsCode.CLIENT_NOT_FOUND
                )
        );
    }

    @Override
    public ClientDto findByEmailClient(String email) {
        if (!StringUtils.hasLength(email)){
            log.error("Client email is null");
            return null;
        }
        Optional<Client> client = clientRepository.findClientByEmail(email);
        return Optional.of(ClientDto.fromEntity(client.get())).orElseThrow(() ->
                new EntityNotFoundException(
                        "Aucun client trouver avec l'email  = " + email + " dans la BDD",
                        ErrorsCode.CLIENT_NOT_FOUND
                )
        );
    }

    @Override
    public List<ClientDto> findAll() {
        return clientRepository.findAll().stream()
                .map(ClientDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        checkIdClientBeforeDelete(id);
        clientRepository.deleteById(id);
    }

    private void checkIdClientBeforeDelete(Integer idClient){
        ClientDto dto = findById(idClient);
        List<CommandeClientDto> commandeClients = commandeClientService.findAllByClientDto(dto);
        if (!CollectionUtils.isEmpty(commandeClients)) {
            log.error("Client alredy used");
            throw new InvalidOpperatioException("Operaton impossible : une ou plusieur commande client existe deja pour ce client",
                    ErrorsCode.CLIENT_ALREADY_IN_USE
            );
        }
    }
}
