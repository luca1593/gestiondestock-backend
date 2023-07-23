package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.ClientApi;
import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author luca
 */
@RestController
public class ClientController implements ClientApi {

    private ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    @Override
    public ClientDto save(ClientDto dto) {
        return this.clientService.save(dto);
    }

    @Override
    public ClientDto findById(Integer id) {
        return this.clientService.findById(id);
    }

    @Override
    public ClientDto findbyNomClient(String nom) {
        return this.clientService.findByNomClient(nom);
    }

    @Override
    public ClientDto findbyEmailClient(String email) {
        return this.clientService.findByEmailClient(email);
    }

    @Override
    public List<ClientDto> findAll() {
        return this.clientService.findAll();
    }

    @Override
    public void delete(Integer id) {
        this.clientService.delete(id);
    }
}
