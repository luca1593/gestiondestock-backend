package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.*;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.*;
import com.devtech.gestiondestock.repository.*;
import com.devtech.gestiondestock.services.CommandeFournisseurService;
import com.devtech.gestiondestock.services.MvtStkService;
import com.devtech.gestiondestock.validator.ArticleValidator;
import com.devtech.gestiondestock.validator.CommandeFournisseurValidator;
import com.devtech.gestiondestock.validator.FournisseurValidator;
import com.devtech.gestiondestock.validator.LigneCommandeFournisseurValidator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommandeFournisseurServiceImpl implements CommandeFournisseurService {

    private CommandeFournisseurRepository commandeFournisseurRepository;
    private LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository;
    private FournisseurRepository fournisseurRepository;
    private ArticleRepository articleRepository;
    private MvtStkService mvtStkService;

    @Autowired
    public CommandeFournisseurServiceImpl(CommandeFournisseurRepository commandeFournisseurRepository,
                                          LigneCommandeFournisseurRepository ligneCommandeFournisseurRepository,
                                          FournisseurRepository fournisseurRepository,
                                          ArticleRepository articleRepository,
                                          MvtStkService mvtStkService) {

        this.commandeFournisseurRepository = commandeFournisseurRepository;
        this.ligneCommandeFournisseurRepository = ligneCommandeFournisseurRepository;
        this.fournisseurRepository = fournisseurRepository;
        this.articleRepository = articleRepository;
        this.mvtStkService = mvtStkService;
    }

    @Override
    public CommandeFournisseurDto save(CommandeFournisseurDto dto) {
        List<String> errors = CommandeFournisseurValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("Commande Fournisseur is not invalid");
            throw new InvalidEntityException("La commande fournisseur n'est pas valid",
                    ErrorsCode.COMMANDE_FOURNISSEUR_NOT_VALID, errors);
        }
        Optional<Fournisseur> fournisseur = this.fournisseurRepository.findById(dto.getFournisseur().getId());
        if (!fournisseur.isPresent()){
            log.warn("Founisseur with ID {} was not found in the DB", dto.getFournisseur().getId());
            throw new EntityNotFoundException(
                    "Le Fournisseur avec l'Id = "
                            + dto.getFournisseur().getCommandeFournisseurs() +
                            " n'existe pas dans la BDD",
                    ErrorsCode.FOURNISSEUR_NOT_FOUND
            );
        }
        List<String> fournisseurErrors = new ArrayList<>();
        if (dto.getLigneCommandeFournisseurs() != null){
            dto.getLigneCommandeFournisseurs().forEach(ligneCmdF ->{
                if (ligneCmdF.getArticle() != null){
                    Optional<Article> article = this.articleRepository.findById(
                            ligneCmdF.getArticle().getId()
                    );
                    if(!article.isPresent()){
                        fournisseurErrors.add("L'article avec l'Id = "
                                + ligneCmdF.getArticle().getId() +
                                " n'existe pas dans la BDD"
                        );
                    }
                }else {
                    fournisseurErrors.add("Impossible d'enregistrer un commande fournisseur avec un article NULL");
                }
            });
        }
        if (!fournisseurErrors.isEmpty()){
            log.warn("Enregistrement impossible");
            throw new InvalidEntityException("L'article n'existe pas dans la BDD", ErrorsCode.FOURNISSEUR_NOT_FOUND, fournisseurErrors);
        }

        List<String> ligneCmdErrors = new ArrayList<>();
        if(!CollectionUtils.isEmpty(dto.getLigneCommandeFournisseurs())){
            for(LigneCommandeFournisseurDto ligneCmdClt : dto.getLigneCommandeFournisseurs()){
                ligneCmdErrors.addAll(LigneCommandeFournisseurValidator.validate(ligneCmdClt));
            }
        }

        if (!CollectionUtils.isEmpty(ligneCmdErrors)) {
            log.error("Commande Client is not invalid", dto);
            throw new InvalidEntityException(
                "La commande client n'est pas valid",
                ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE, ligneCmdErrors
            );
        }

        CommandeFournisseur cmdtFrs = this.commandeFournisseurRepository.save(CommandeFournisseurDto.toEntity(dto));
        if (dto.getLigneCommandeFournisseurs() != null){
            dto.getLigneCommandeFournisseurs().forEach(ligneCmdFrs ->{
                LigneCommandeFournisseur ligneCommandeFournisseur = LigneCommandeFournisseurDto.toEntity(ligneCmdFrs);
                ligneCommandeFournisseur.setCommandeFournisseur(cmdtFrs);
                ligneCommandeFournisseur.setIdentreprise(dto.getIdentreprise());
                this.ligneCommandeFournisseurRepository.save(ligneCommandeFournisseur);
                updateMvtStk(cmdtFrs.getId());
            });
        }
        return CommandeFournisseurDto.fromEntity(cmdtFrs);
    }
    
    @Override
    public CommandeFournisseurDto findById(Integer id) {
        checkIdCommande(id, ErrorsCode.COMMANDE_FOURNISSEUR_NOT_FOUND);
        return this.commandeFournisseurRepository.findById(id)
                .map(CommandeFournisseurDto::fromEntity)
                .orElseThrow(()-> new EntityNotFoundException(
                        "Aucune commande fournisseur trouver avec l'ID = " + id
                        , ErrorsCode.COMMANDE_FOURNISSEUR_NOT_FOUND
                ));
    }

    @Override
    public CommandeFournisseurDto updateEtatCommande(Integer id, EtatCommande etatCommande) {
        checkIdCommande(id, ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        if (!StringUtils.hasLength(String.valueOf(etatCommande))){
            log.error("Etat commande value is null");
            throw new InvalidOpperatioException("Impossible de modifier une commande fournisseur avec un etat null",
                    ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE
            );
        }
        CommandeFournisseurDto commandeFournisseurDto = getCommandeFournisseurDto(id,
                ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        commandeFournisseurDto.setEtatcommande(etatCommande);
        CommandeFournisseur savedCommande = this.commandeFournisseurRepository.
                save(CommandeFournisseurDto.toEntity(commandeFournisseurDto));
        if (commandeFournisseurDto.isCommandeLivree()){
            updateMvtStk(id);
        }
        return CommandeFournisseurDto.fromEntity(savedCommande);
    }

    @Override
    public CommandeFournisseurDto updateQuantiterCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
        checkIdCommande(idCommande, ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        checkIdLigneCommande(idLigneCommande);
        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) == 0){
            log.error("Quantite commande is null");
            throw new InvalidOpperatioException("Impossible de modifier une commande fournisseur avec une quantite null ou ZERRO",
                    ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE
            );
        }
        CommandeFournisseurDto commandeFournisseurDto = getCommandeFournisseurDto(idCommande, ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        LigneCommandeFournisseur ligneCommandeFournisseur = getLigneCommandeFournisseur(idLigneCommande);
        ligneCommandeFournisseur.setQuantite(quantite);
        this.ligneCommandeFournisseurRepository.save(ligneCommandeFournisseur);
        return commandeFournisseurDto;
    }

    @Override
    public CommandeFournisseurDto updateFournisseur(Integer idCommande, Integer idFournisseur) {
        checkIdCommande(idCommande, ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        if (idFournisseur == null){
            log.error("Client ID is null");
            throw new InvalidOpperatioException("Impossible de modifier une commande fournisseur avec un fournisseur null",
                    ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE
            );
        }
        CommandeFournisseurDto commandeFournisseurDto = getCommandeFournisseurDto(idCommande, ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        Optional<Fournisseur> fournisseurOptional = this.fournisseurRepository.findById(idFournisseur);
        if (!fournisseurOptional.isPresent()){
            throw new EntityNotFoundException(
                    "Aucun fournisseur n'a ete trouver avec l'ID = " + idFournisseur,
                    ErrorsCode.FOURNISSEUR_NOT_FOUND
            );
        }
        commandeFournisseurDto.setFournisseur(FournisseurDto.fromEntity(fournisseurOptional.get()));

        return CommandeFournisseurDto.fromEntity(
                this.commandeFournisseurRepository.save(
                        CommandeFournisseurDto.toEntity(commandeFournisseurDto)
                )
        );
    }

    @Override
    public CommandeFournisseurDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer newIdArticle) {
        checkIdCommande(idCommande, ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        Article article = checkIdArticle(newIdArticle, "nouveau");
        CommandeFournisseurDto commandeFournisseurDto = getCommandeFournisseurDto(idCommande, ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        LigneCommandeFournisseur ligneCommandeFournisseur = getLigneCommandeFournisseur(idLigneCommande);
        ligneCommandeFournisseur.setArticle(article);
        this.ligneCommandeFournisseurRepository.save(ligneCommandeFournisseur);
        return commandeFournisseurDto;
    }

    @Override
    public CommandeFournisseurDto deleteArticle(Integer idCommande, Integer idLigneCommande) {
        checkIdCommande(idCommande, ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        CommandeFournisseurDto commandeFournisseurDto = getCommandeFournisseurDto(idCommande, ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE);
        checkIdLigneCommande(idLigneCommande);
        getLigneCommandeFournisseur(idLigneCommande);
        this.ligneCommandeFournisseurRepository.deleteById(idLigneCommande);
        return commandeFournisseurDto;
    }

    @Override
    public List<LigneCommandeFournisseurDto> findAllByCommandeFournisseur(Integer idCommande) {
        checkIdCommande(idCommande, ErrorsCode.COMMANDE_FOURNISSEUR_NOT_FOUND);
        return this.ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(idCommande) != null ?
                this.ligneCommandeFournisseurRepository.findAllByCommandeFournisseurId(idCommande).stream()
                        .map(LigneCommandeFournisseurDto::fromEntity)
                        .collect(Collectors.toList()) : null;
    }

    @Override
    public CommandeFournisseurDto findByCodeCommande(String code) {
        if (!StringUtils.hasLength(code)){
            log.error("Commande Fournisseur code is null");
            return null;
        }
        return this.commandeFournisseurRepository.findCommandeFournisseurByCode(code)
                .map(CommandeFournisseurDto::fromEntity)
                .orElseThrow(()-> new EntityNotFoundException(
                        "Aucune commande fournisseur trouver avec le code = " + code,
                        ErrorsCode.COMMANDE_FOURNISSEUR_NOT_FOUND
                ));
    }

    @Override
    public List<CommandeFournisseurDto> findByDateCommande(Instant dateCommande) {
        if (dateCommande == null){
            log.error("Commande fournisseur date is null");
            return Collections.emptyList();
        }
        return this.commandeFournisseurRepository.findCommandeFournisseurByDateCommande(dateCommande) != null ?
                this.commandeFournisseurRepository.findCommandeFournisseurByDateCommande(dateCommande).stream()
                        .map(CommandeFournisseurDto::fromEntity)
                        .collect(Collectors.toList()) : null;
    }

    @Override
    public List<CommandeFournisseurDto> findAll() {
        return this.commandeFournisseurRepository.findAll().stream()
                .map(CommandeFournisseurDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommandeFournisseurDto> findAllByFournisseurDto(FournisseurDto fournisseurDto){
        List<String> errors = FournisseurValidator.validate(fournisseurDto);
        if(!CollectionUtils.isEmpty(errors)){
            log.error("Fournisseur is not valid", fournisseurDto);
            throw new InvalidEntityException(
                "Le fournisseur n'est pas valide ou n'existe pas pour cette recherche", 
                ErrorsCode.FOURNISSEUR_NOT_VALID, errors
            );
        }

        return this.commandeFournisseurRepository.findAllByFournisseur(
                FournisseurDto.toEntity(fournisseurDto)).stream()
                    .map(CommandeFournisseurDto::fromEntity)
                    .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (id == null){
            log.error("Commande fournisseur ID is null");
            return;
        }
        this.commandeFournisseurRepository.deleteById(id);
    }

    private void checkIdCommande(Integer id, ErrorsCode errorsCode) {
        if (id == null){
            log.error("Commande fournisseur ID is null");
            throw new InvalidOpperatioException("Impossible de modifier une commande fournisseur avec un ID null",
                    errorsCode
            );
        }
    }
    private CommandeFournisseurDto getCommandeFournisseurDto(Integer id, ErrorsCode errorsCode) {
        CommandeFournisseurDto commandeFournisseurDto = findById(id);
        if (commandeFournisseurDto.isCommandeLivree()){
            throw new InvalidOpperatioException("La commande fournisseur est livrée et ne peut plus etre modifier",
                    errorsCode
            );
        }
        return commandeFournisseurDto;
    }
    private void checkIdLigneCommande(Integer idLigneCommande) {
        if (idLigneCommande == null){
            log.error("Ligne commande fournisseur ID is null");
            throw new InvalidOpperatioException("Impossible de modifier une commande fournisseur avec une ligne commande null",
                    ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE
            );
        }
    }
    private LigneCommandeFournisseur getLigneCommandeFournisseur(Integer idLigneCommande) {
        Optional<LigneCommandeFournisseur> ligneCommandeFournisseur = this.ligneCommandeFournisseurRepository.findById(idLigneCommande);
        if (!ligneCommandeFournisseur.isPresent()){
            throw new EntityNotFoundException(
                    "Aucune ligne commande fournisseur n'a ete trouver avec l'ID = " + idLigneCommande,
                    ErrorsCode.LIGNE_COMMANDE_CLIENT_NOT_FOUND
            );
        }
        return ligneCommandeFournisseur.get();
    }
    private Article checkIdArticle(Integer idArticle, String msg) {
        if (idArticle == null){
            log.error("L'ID de " + msg + " est null");
            throw new InvalidOpperatioException("Impossible de modifier une commande fournisseur avec un " + msg + " ID article null",
                    ErrorsCode.COMMANDE_FOURNISSEUR_NON_MODIFIABLE
            );
        }
        Optional<Article> article = this.articleRepository.findById(idArticle);
        if (!article.isPresent()){
            throw new EntityNotFoundException(
                    "Aucun article n'a ete trouver avec l'ID = " + idArticle,
                    ErrorsCode.ARTICLE_NOT_FOUND
            );
        }
        List<String> errors = ArticleValidator.validate(ArticleDto.fromEntity(article.get()));
        if (!errors.isEmpty()){
            log.error("Article is not invalid");
            throw new InvalidEntityException("L'article' n'est pas valid",
                    ErrorsCode.ARTICLE_NOT_VALID, errors);
        }
        return article.get();
    }

    private void updateMvtStk(Integer idCommande){
        List<LigneCommandeFournisseur> ligneCommandeFournisseurs = this.ligneCommandeFournisseurRepository.
                findAllByCommandeFournisseurId(idCommande);
        ligneCommandeFournisseurs.forEach(ligne -> {
            MvtStkDto mvtStkDto = MvtStkDto.builder()
                    .article(ArticleDto.fromEntity(ligne.getArticle()))
                    .dateMvt(Instant.now())
                    .typeMvt(TypeMvt.ENTRER)
                    .sourceMvt(SourceMvtStk.COMMANDE_FOURNISSEUR)
                    .quantite(ligne.getQuantite())
                    .identreprise(ligne.getIdentreprise())
                    .build();
            this.mvtStkService.entreMvtStk(mvtStkDto);
        });
    }
}
