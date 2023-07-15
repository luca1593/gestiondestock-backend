package com.devtech.gestiondestock.controller.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import static com.devtech.gestiondestock.utils.Constants.APP_ROOT;

/**
 * @author luca
 */
public interface PhotoApi {
    
    @PostMapping(value = APP_ROOT + "/photos/{context}/{id}/{title}", consumes = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    Object savePhoto(
        @PathVariable("context") String context,
        @PathVariable("id") Integer id,
        @PathVariable("title") String title,
        @RequestPart("file") MultipartFile photo) throws Exception;
}
