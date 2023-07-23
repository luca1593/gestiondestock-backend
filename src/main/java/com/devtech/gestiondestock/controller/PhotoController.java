package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.PhotoApi;
import com.devtech.gestiondestock.services.strategy.StrategyProtoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author luca
 */
@RestController
public class PhotoController implements PhotoApi {

    private StrategyProtoContext strategyProtoContext;

    @Autowired
    public PhotoController(StrategyProtoContext strategyProtoContext) {
        this.strategyProtoContext = strategyProtoContext;
    }

    @Override
    public Object savePhoto(
            String context,
            Integer id,
            String title,
            MultipartFile photo) throws Exception {
        return this.strategyProtoContext.savePhoto(
                context, id, title, photo.getInputStream());
    }
}
