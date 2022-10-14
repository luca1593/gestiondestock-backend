package com.devtech.gestiondestock.services;

import com.devtech.gestiondestock.dto.ClientDto;

import java.util.List;


public interface ClientService {

    ClientDto save(ClientDto dto);
    ClientDto findById(Integer id);
    ClientDto findByNomClient(String nom);
    ClientDto findByEmailClient(String email);
    List<ClientDto> findAll();
    void delete(Integer id);

}
