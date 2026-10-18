package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author luca
 *
 * <p>Sélectionne la stratégie d'enregistrement de photo selon le contexte.
 * La stratégie est résolue localement à chaque appel (aucun état mutable partagé),
 * ce qui rend le service sûr en environnement concurrent (singleton sans état).</p>
 */
@Service
public class StrategyProtoContext {

    private final BeanFactory beanFactory;

    @Autowired
    public StrategyProtoContext(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Object savePhoto(String context, Integer id, String title, InputStream photo) throws Exception {
        return resolveStrategy(context).savePhoto(id, photo, title);
    }

    private Strategy resolveStrategy(String context) {
        if (context == null) {
            throw new InvalidOpperatioException("Context manquant pour l'enregistrement de la photo",
                    ErrorsCode.UNKNOWN_CONTEXT);
        }
        final String beansname = context + "Strategy";
        switch (context) {
            case "article":
                return this.beanFactory.getBean(beansname, SaveArticlePhoto.class);
            case "client":
                return this.beanFactory.getBean(beansname, SaveClientPhoto.class);
            case "entreprise":
                return this.beanFactory.getBean(beansname, SaveEntreprisePhoto.class);
            case "fournisseur":
                return this.beanFactory.getBean(beansname, SaveFournisseurPhoto.class);
            case "utilisateur":
                return this.beanFactory.getBean(beansname, SaveUtilisateurPhoto.class);
            default:
                throw new InvalidOpperatioException("Context inconue pour l'enregistrement de la photo",
                        ErrorsCode.UNKNOWN_CONTEXT);
        }
    }
}
