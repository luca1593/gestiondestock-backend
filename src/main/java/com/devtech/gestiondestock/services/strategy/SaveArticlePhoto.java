package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.services.ArticleService;
import com.devtech.gestiondestock.services.FlickrService;
import com.devtech.gestiondestock.services.GoogleDriveService;
import com.flickr4java.flickr.FlickrException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

@Service("articleStrategy")
@Slf4j
public class SaveArticlePhoto implements Strategy<ArticleDto> {

    private ArticleService articleService;
    private FlickrService flickrService;

    @Autowired
    public SaveArticlePhoto(ArticleService articleService, FlickrService flickrService) {
        this.articleService = articleService;
        this.flickrService = flickrService;
    }

    @Override
    public ArticleDto savePhoto(Integer id, InputStream photo, String titre) throws FlickrException {
        ArticleDto article = articleService.findById(id);
        String urlPhoto = flickrService.savePhoto(photo, titre, id);
        if (!StringUtils.hasLength(urlPhoto)){
            throw new InvalidOpperatioException("Impossible de mettre a jour la photo de l'article", ErrorsCode.UPDATE_PHOTO_EXEPTION);
        }
        
        article.setPhoto(urlPhoto);
        return articleService.save(article);
    }
}
