package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.services.FlickrService;
import com.devtech.gestiondestock.services.FournisseurService;
import com.flickr4java.flickr.FlickrException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service("fournisseurStrategy")
@Slf4j
public class SaveFournisseurPhoto implements Strategy<FournisseurDto> {

    private final FournisseurService fournisseurService;
    private final FlickrService flickrService;

    @Autowired
    public SaveFournisseurPhoto(FournisseurService fournisseurService, FlickrService flickrService) {
        this.fournisseurService = fournisseurService;
        this.flickrService = flickrService;
    }

    @Override
    public FournisseurDto savePhoto(Integer id, InputStream photo, String titre) throws FlickrException {
        FournisseurDto fournisseur = this.fournisseurService.findById(id);
        String urlPhoto = this.flickrService.savePhoto(photo, titre, id);
        if (!StringUtils.hasLength(urlPhoto)) {
            throw new InvalidOpperatioException("Impossible de mettre a jour la photo du fournisseur",
                    ErrorsCode.UPDATE_PHOTO_EXEPTION);
        }
        fournisseur.setPhoto(urlPhoto);
        return this.fournisseurService.save(fournisseur);
    }
}
