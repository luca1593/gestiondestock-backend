package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.services.CloudinaryService;
import com.devtech.gestiondestock.services.UtilisateurService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service("utilisateurStrategy")
@Slf4j
public class SaveUtilisateurPhoto implements Strategy<UtilisateurDto> {

    private final UtilisateurService utilisateurService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public SaveUtilisateurPhoto(UtilisateurService utilisateurService, CloudinaryService cloudinaryService) {
        this.utilisateurService = utilisateurService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public UtilisateurDto savePhoto(Integer id, InputStream photo, String titre) throws Exception {
        UtilisateurDto utilisateur = this.utilisateurService.findById(id);
        String urlPhoto = this.cloudinaryService.savePhoto(photo, titre, id);
        if (!StringUtils.hasLength(urlPhoto)) {
            throw new InvalidOpperatioException("Impossible de mettre a jour la photo de l'utilisateur",
                    ErrorsCode.UPDATE_PHOTO_EXEPTION);
        }
        utilisateur.setPhoto(urlPhoto);
        return this.utilisateurService.save(utilisateur);
    }
}
