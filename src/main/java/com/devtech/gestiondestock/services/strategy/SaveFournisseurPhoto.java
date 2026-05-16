package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.services.CloudinaryService;
import com.devtech.gestiondestock.services.FournisseurService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service("fournisseurStrategy")
@Slf4j
public class SaveFournisseurPhoto implements Strategy<FournisseurDto> {

    private final FournisseurService fournisseurService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public SaveFournisseurPhoto(FournisseurService fournisseurService, CloudinaryService cloudinaryService) {
        this.fournisseurService = fournisseurService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public FournisseurDto savePhoto(Integer id, InputStream photo, String titre) throws Exception {
        FournisseurDto fournisseur = this.fournisseurService.findById(id);
        String urlPhoto = this.cloudinaryService.savePhoto(photo, titre, id);
        if (!StringUtils.hasLength(urlPhoto)) {
            throw new InvalidOpperatioException("Impossible de mettre a jour la photo du fournisseur",
                    ErrorsCode.UPDATE_PHOTO_EXEPTION);
        }
        fournisseur.setPhoto(urlPhoto);
        return this.fournisseurService.save(fournisseur);
    }
}
