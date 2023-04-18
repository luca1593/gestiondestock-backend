package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.services.ClientService;
import com.devtech.gestiondestock.services.FlickrService;
import com.flickr4java.flickr.FlickrException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service("clientStrategy")
@Slf4j
public class SaveClientPhoto implements Strategy<ClientDto> {

    private ClientService clientService;
    private FlickrService flickrService;

    @Autowired
    public SaveClientPhoto(ClientService clientService, FlickrService flickrService) {
        this.clientService = clientService;
        this.flickrService = flickrService;
    }

    @Override
    public ClientDto savePhoto(Integer id, InputStream photo, String titre) throws FlickrException {
        ClientDto client = clientService.findById(id);
        String urlPhoto = flickrService.savePhoto(photo, titre, id);
        if (!StringUtils.hasLength(urlPhoto)){
            throw new InvalidOpperatioException("Impossible de mettre a jour la photo du client",
                    ErrorsCode.UPDATE_PHOTO_EXEPTION);
        }
        client.setPhoto(urlPhoto);
        return clientService.save(client);
    }
}
