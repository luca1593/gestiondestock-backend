package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.services.FlickrService;
import com.devtech.gestiondestock.services.UtilisateurService;
import com.flickr4java.flickr.FlickrException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service("utilisateurStrategy")
@Slf4j
public class SaveUtilisateurPhoto implements Strategy<UtilisateurDto> {

    private final UtilisateurService utilisateurService;
    private final FlickrService flickrService;

    @Autowired
    public SaveUtilisateurPhoto(UtilisateurService utilisateurService, FlickrService flickrService) {
        this.utilisateurService = utilisateurService;
        this.flickrService = flickrService;
    }

    @Override
    public UtilisateurDto savePhoto(Integer id, InputStream photo, String titre) throws FlickrException {
        UtilisateurDto utilisateur = this.utilisateurService.findById(id);
        String urlPhoto = this.flickrService.savePhoto(photo, titre, id);
        if (!StringUtils.hasLength(urlPhoto)) {
            throw new InvalidOpperatioException("Impossible de mettre a jour la photo de l'utilisateur",
                    ErrorsCode.UPDATE_PHOTO_EXEPTION);
        }
        utilisateur.setPhoto(urlPhoto);
        return this.utilisateurService.save(utilisateur);
    }
}
