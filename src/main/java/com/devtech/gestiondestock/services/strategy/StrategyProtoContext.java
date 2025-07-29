package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class StrategyProtoContext {

    private final BeanFactory beanFactory;
    @SuppressWarnings("rawtypes")
    private Strategy strategy;
    @Setter
    private String context;

    @Autowired
    public StrategyProtoContext(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Object savePhoto(String context, Integer id, String title, InputStream photo) throws Exception {
        determineContext(context);
        context += context;
        return this.strategy.savePhoto(id, photo, title);
    }

    private void determineContext(String context) {
        final String beansname = context + "Strategy";
        switch (context) {
            case "article":
                this.strategy = this.beanFactory.getBean(beansname, SaveArticlePhoto.class);
                break;
            case "client":
                this.strategy = this.beanFactory.getBean(beansname, SaveClientPhoto.class);
                break;
            case "entreprise":
                this.strategy = this.beanFactory.getBean(beansname, SaveEntreprisePhoto.class);
                break;
            case "fournisseur":
                this.strategy = this.beanFactory.getBean(beansname, SaveFournisseurPhoto.class);
                break;
            case "utilisateur":
                this.strategy = this.beanFactory.getBean(beansname, SaveUtilisateurPhoto.class);
                break;
            default:
                throw new InvalidOpperatioException("Context inconue pour l'enregistrement de la photo",
                        ErrorsCode.UNKNOWN_CONTEXT);
        }
    }
}
