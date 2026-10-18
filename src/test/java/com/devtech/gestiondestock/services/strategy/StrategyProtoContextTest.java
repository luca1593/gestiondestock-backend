package com.devtech.gestiondestock.services.strategy;

import com.devtech.gestiondestock.dto.ArticleDto;
import com.devtech.gestiondestock.dto.ClientDto;
import com.devtech.gestiondestock.dto.EntrepriseDto;
import com.devtech.gestiondestock.dto.FournisseurDto;
import com.devtech.gestiondestock.dto.UtilisateurDto;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires (Mockito) de {@link StrategyProtoContext}.
 *
 * <p>Vérifie notamment l'absence d'état mutable partagé (correctif de la course
 * entre deux appels concurrents) : chaque appel résout sa propre stratégie.</p>
 */
@ExtendWith(MockitoExtension.class)
class StrategyProtoContextTest {

    @Mock
    private BeanFactory beanFactory;

    @InjectMocks
    private StrategyProtoContext strategyProtoContext;

    private final InputStream photo = new ByteArrayInputStream(new byte[]{1, 2, 3});

    @Test
    void savePhoto_contexteArticle_resoutLaBonneStrategie() throws Exception {
        SaveArticlePhoto strategy = mock(SaveArticlePhoto.class);
        ArticleDto attendu = ArticleDto.builder().id(1).build();
        when(beanFactory.getBean("articleStrategy", SaveArticlePhoto.class)).thenReturn(strategy);
        when(strategy.savePhoto(1, photo, "titre")).thenReturn(attendu);

        Object result = strategyProtoContext.savePhoto("article", 1, "titre", photo);

        assertSame(attendu, result);
        verify(strategy).savePhoto(1, photo, "titre");
    }

    @Test
    void savePhoto_contexteClient_resoutLaBonneStrategie() throws Exception {
        SaveClientPhoto strategy = mock(SaveClientPhoto.class);
        ClientDto attendu = ClientDto.builder().id(2).build();
        when(beanFactory.getBean("clientStrategy", SaveClientPhoto.class)).thenReturn(strategy);
        when(strategy.savePhoto(2, photo, "titre")).thenReturn(attendu);

        Object result = strategyProtoContext.savePhoto("client", 2, "titre", photo);

        assertSame(attendu, result);
    }

    @Test
    void savePhoto_resoutLaStrategieAChaqueAppel_sansEtatPartage() throws Exception {
        SaveArticlePhoto article = mock(SaveArticlePhoto.class);
        SaveClientPhoto client = mock(SaveClientPhoto.class);
        ArticleDto articleDto = ArticleDto.builder().id(1).build();
        ClientDto clientDto = ClientDto.builder().id(1).build();
        when(beanFactory.getBean("articleStrategy", SaveArticlePhoto.class)).thenReturn(article);
        when(beanFactory.getBean("clientStrategy", SaveClientPhoto.class)).thenReturn(client);
        when(article.savePhoto(1, photo, "t")).thenReturn(articleDto);
        when(client.savePhoto(1, photo, "t")).thenReturn(clientDto);

        // Un appel "client" entre deux appels "article" ne doit pas polluer la résolution.
        assertSame(articleDto, strategyProtoContext.savePhoto("article", 1, "t", photo));
        assertSame(clientDto, strategyProtoContext.savePhoto("client", 1, "t", photo));
        assertSame(articleDto, strategyProtoContext.savePhoto("article", 1, "t", photo));

        verify(beanFactory, times(2)).getBean("articleStrategy", SaveArticlePhoto.class);
        verify(beanFactory, times(1)).getBean("clientStrategy", SaveClientPhoto.class);
    }

    @Test
    void savePhoto_contexteInconnu_leveInvalidOpperatioException() {
        assertThrows(InvalidOpperatioException.class,
                () -> strategyProtoContext.savePhoto("inconnu", 1, "t", photo));
        verifyNoInteractions(beanFactory);
    }

    @Test
    void savePhoto_contexteNull_leveInvalidOpperatioExceptionSansNpe() {
        assertThrows(InvalidOpperatioException.class,
                () -> strategyProtoContext.savePhoto(null, 1, "t", photo));
        verifyNoInteractions(beanFactory);
    }

    @Test
    void savePhoto_contexteFournisseur_resoutLaBonneStrategie() throws Exception {
        SaveFournisseurPhoto strategy = mock(SaveFournisseurPhoto.class);
        FournisseurDto attendu = FournisseurDto.builder().id(3).build();
        when(beanFactory.getBean("fournisseurStrategy", SaveFournisseurPhoto.class)).thenReturn(strategy);
        when(strategy.savePhoto(3, photo, "t")).thenReturn(attendu);

        assertSame(attendu, strategyProtoContext.savePhoto("fournisseur", 3, "t", photo));
    }

    @Test
    void savePhoto_contexteUtilisateur_resoutLaBonneStrategie() throws Exception {
        SaveUtilisateurPhoto strategy = mock(SaveUtilisateurPhoto.class);
        UtilisateurDto attendu = UtilisateurDto.builder().id(4).build();
        when(beanFactory.getBean("utilisateurStrategy", SaveUtilisateurPhoto.class)).thenReturn(strategy);
        when(strategy.savePhoto(4, photo, "t")).thenReturn(attendu);

        assertSame(attendu, strategyProtoContext.savePhoto("utilisateur", 4, "t", photo));
    }

    @Test
    void savePhoto_contexteEntreprise_resoutLaBonneStrategie() throws Exception {
        SaveEntreprisePhoto strategy = mock(SaveEntreprisePhoto.class);
        EntrepriseDto attendu = EntrepriseDto.builder().id(5).build();
        when(beanFactory.getBean("entrepriseStrategy", SaveEntreprisePhoto.class)).thenReturn(strategy);
        when(strategy.savePhoto(5, photo, "t")).thenReturn(attendu);

        assertSame(attendu, strategyProtoContext.savePhoto("entreprise", 5, "t", photo));
    }
}
