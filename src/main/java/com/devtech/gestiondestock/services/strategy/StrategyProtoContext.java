package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.flickr4java.flickr.FlickrException;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class StrategyProtoContext {

    private BeanFactory beanFactory;
    private Strategy strategy;
    @Setter
    private String context;

    @Autowired
    public StrategyProtoContext(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Object savePhoto(String context, Integer id, InputStream photo, String title) throws FlickrException {
        determineContext(context);
        return strategy.savePhoto(id, photo, title);
    }

    private void determineContext(String context){
        final String beansname = context + "StrategyPhoto";
        switch (context){
            case "article" :
                strategy = beanFactory.getBean(beansname, SaveArticlePhoto.class);
                break;
            case "client" :
                strategy = beanFactory.getBean(beansname, SaveClientPhoto.class);
                break;
            case "entreprise" :
                strategy = beanFactory.getBean(beansname, SaveEntreprisePhoto.class);
                break;
            case "fournisseur" :
                strategy = beanFactory.getBean(beansname, SaveFournisseurPhoto.class);
                break;
            case "utilisateur" :
                strategy = beanFactory.getBean(beansname, SaveUtilisateurPhoto.class);
                break;
            default: throw new InvalidOpperatioException("Context inconue pour l'enregistrement de la photo",
                    ErrorsCode.UNKNOWN_CONTEXT);
        }
    }
}
