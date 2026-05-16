package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.services.ArticleService;
import com.devtech.gestiondestock.services.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service("articleStrategy")
@Slf4j
public class SaveArticlePhoto implements Strategy<ArticleDto> {

    private final ArticleService articleService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public SaveArticlePhoto(ArticleService articleService, CloudinaryService cloudinaryService) {
        this.articleService = articleService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public ArticleDto savePhoto(Integer id, InputStream photo, String titre) throws Exception {
        ArticleDto article = this.articleService.findById(id);
        String urlPhoto = this.cloudinaryService.savePhoto(photo, titre, id);
        if (!StringUtils.hasLength(urlPhoto)) {
            throw new InvalidOpperatioException("Impossible de mettre a jour la photo de l'article", ErrorsCode.UPDATE_PHOTO_EXEPTION);
        }

        article.setPhoto(urlPhoto);
        return this.articleService.save(article);
    }
}
