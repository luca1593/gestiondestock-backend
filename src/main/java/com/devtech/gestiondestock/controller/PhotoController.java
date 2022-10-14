package com.devtech.gestiondestock.controller;

import com.devtech.gestiondestock.controller.api.PhotoApi;
import com.devtech.gestiondestock.services.strategy.StrategyProtoContext;
import com.flickr4java.flickr.FlickrException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class PhotoController implements PhotoApi {

    private StrategyProtoContext strategyProtoContext;

    @Autowired
    public PhotoController(StrategyProtoContext strategyProtoContext) {
        this.strategyProtoContext = strategyProtoContext;
    }

    @Override
    public Object savePhoto(String context, Integer id, MultipartFile photo, String title) throws IOException, FlickrException {
        return strategyProtoContext.savePhoto(context, id, photo.getInputStream(), title);
    }
}
