package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.dto.EntrepriseDto;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.services.EntrepriseService;
import com.devtech.gestiondestock.services.FlickrService;
import com.flickr4java.flickr.FlickrException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service("entrepriseStrategy")
@Slf4j
public class SaveEntreprisePhoto implements Strategy<EntrepriseDto> {

    private final EntrepriseService entrepriseService;
    private final FlickrService flickrService;

    @Autowired
    public SaveEntreprisePhoto(EntrepriseService entrepriseService, FlickrService flickrService) {
        this.entrepriseService = entrepriseService;
        this.flickrService = flickrService;
    }

    @Override
    public EntrepriseDto savePhoto(Integer id, InputStream photo, String titre) throws FlickrException {
        EntrepriseDto entreprise = this.entrepriseService.findById(id);
        String urlPhoto = this.flickrService.savePhoto(photo, titre, id);
        if (!StringUtils.hasLength(urlPhoto)) {
            throw new InvalidOpperatioException("Impossible de mettre a jour la photo de l'entreprise",
                    ErrorsCode.UPDATE_PHOTO_EXEPTION);
        }
        entreprise.setPhoto(urlPhoto);
        return this.entrepriseService.save(entreprise);
    }
}
