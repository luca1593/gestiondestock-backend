package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.services.ClientService;
import com.devtech.gestiondestock.services.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service("clientStrategy")
@Slf4j
public class SaveClientPhoto implements Strategy<ClientDto> {

    private final ClientService clientService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public SaveClientPhoto(ClientService clientService, CloudinaryService cloudinaryService) {
        this.clientService = clientService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public ClientDto savePhoto(Integer id, InputStream photo, String titre) throws Exception {
        ClientDto client = this.clientService.findById(id);
        String urlPhoto = this.cloudinaryService.savePhoto(photo, titre, id);
        if (!StringUtils.hasLength(urlPhoto)) {
            throw new InvalidOpperatioException("Impossible de mettre a jour la photo du client",
                    ErrorsCode.UPDATE_PHOTO_EXEPTION);
        }
        client.setPhoto(urlPhoto);
        return this.clientService.save(client);
    }
}
