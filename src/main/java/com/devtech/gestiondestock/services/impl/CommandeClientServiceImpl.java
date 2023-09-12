package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.dto.*;
import com.devtech.gestiondestock.exception.EntityNotFoundException;
import com.devtech.gestiondestock.exception.ErrorsCode;
import com.devtech.gestiondestock.exception.InvalidEntityException;
import com.devtech.gestiondestock.exception.InvalidOpperatioException;
import com.devtech.gestiondestock.model.*;
import com.devtech.gestiondestock.repository.ArticleRepository;
import com.devtech.gestiondestock.repository.ClientRepository;
import com.devtech.gestiondestock.repository.CommandeClientRepository;
import com.devtech.gestiondestock.repository.LigneCommandeClientRepository;
import com.devtech.gestiondestock.services.CommandeClientService;
import com.devtech.gestiondestock.services.MvtStkService;
import com.devtech.gestiondestock.validator.ArticleValidator;
import com.devtech.gestiondestock.validator.ClientValidator;
import com.devtech.gestiondestock.validator.CommandeClientValidator;
import com.devtech.gestiondestock.validator.LigneCommandeClientValidator;
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
public class CommandeClientServiceImpl implements CommandeClientService {

    private final String msgIdCommandNull = "Commande client ID is null";

    private final CommandeClientRepository commandeClientRepository;
    private final LigneCommandeClientRepository ligneCommandeClientRepository;
    private final ClientRepository clientRepository;
    private final ArticleRepository articleRepository;
    private final MvtStkService mvtStkService;

    @Autowired
    public CommandeClientServiceImpl(CommandeClientRepository commandeClientRepository,
                                     ClientRepository clientRepository,
                                     ArticleRepository articleRepository,
                                     LigneCommandeClientRepository ligneCommandeClientRepository,
                                     MvtStkService mvtStkService) {
        this.commandeClientRepository = commandeClientRepository;
        this.ligneCommandeClientRepository = ligneCommandeClientRepository;
        this.clientRepository = clientRepository;
        this.articleRepository = articleRepository;
        this.mvtStkService = mvtStkService;
    }

    @Override
    public CommandeClientDto save(CommandeClientDto dto) {
        List<String> errors = CommandeClientValidator.validate(dto);
        if(!CollectionUtils.isEmpty(errors)){
            log.error("Commande Client is not valid", dto);
            throw new InvalidEntityException(
                "La commande client n'est pas valide", 
                ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE, errors
            );
        }

    if(dto.getId()!=null&&dto.isCommandeLivree()){
        throw new InvalidOpperatioException("La commande client est livrée et ne peut plus etre modifier",
                ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
    }

    Optional<Client> client = this.clientRepository.findById(dto.getClient().getId());if(!client.isPresent()){
        log.warn("Client with ID {} was not found in the DB", dto.getClient().getId());
        throw new EntityNotFoundException(
                "Le client avec l'Id = "
                        + dto.getClient().getCommandeClients() +
                        " n'existe pas dans la BDD",
                ErrorsCode.CLIENT_NOT_FOUND);
    }

    List<String> articleErrors = new ArrayList<>();

    if(dto.getLigneCommandeClients()!=null){
        dto.getLigneCommandeClients().forEach(ligneCmdC -> {
            if (ligneCmdC.getArticle() != null) {
                Optional<Article> article = this.articleRepository.findById(
                        ligneCmdC.getArticle().getId());
                if (!article.isPresent()) {
                    articleErrors.add("L'article avec l'Id = "
                            + ligneCmdC.getArticle().getId() +
                            " n'existe pas dans la BDD");
                }
            } else {
                articleErrors.add("Impossible d'enregistrer un commande client avec un article NULL");
            }
        });
    }

    if(!CollectionUtils.isEmpty(articleErrors)){
        log.warn("Enregistrement impossible");
        throw new InvalidEntityException("L'article n'existe pas dans la BDD", ErrorsCode.ARTICLE_NOT_FOUND,
                articleErrors);
    }

    List<String> ligneCmdErrors = new ArrayList<>();
    if(!CollectionUtils.isEmpty(dto.getLigneCommandeClients())){
        for(LigneCommandeClientDto ligneCmdClt : dto.getLigneCommandeClients()){
            ligneCmdErrors.addAll(LigneCommandeClientValidator.validate(ligneCmdClt));
        }
    }

    if (!CollectionUtils.isEmpty(ligneCmdErrors)) {
        log.error("Commande Client is not valid", dto);
        throw new InvalidEntityException(
            "La commande client n'est pas valide",
            ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE, ligneCmdErrors
        );
    }

    CommandeClient cmdtClt = this.commandeClientRepository.save(CommandeClientDto.toEntity(dto));
    if(!CollectionUtils.isEmpty(dto.getLigneCommandeClients())){
        dto.getLigneCommandeClients().forEach(ligneCmdClt -> {
            LigneCommandeClient ligneCommandeClient = LigneCommandeClientDto.toEntity(ligneCmdClt);
            ligneCommandeClient.setCommandeClient(cmdtClt);
            ligneCommandeClient.setIdentreprise(dto.getIdentreprise());
            this.ligneCommandeClientRepository.save(ligneCommandeClient);
            updateMvtStk(cmdtClt.getId());
        });
    }

    return CommandeClientDto.fromEntity(cmdtClt);
    }

    @Override
    public CommandeClientDto updateEtatCommande(Integer id, EtatCommande etatCommande) {
        checkIdCommande(id, ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
        if (!StringUtils.hasLength(String.valueOf(etatCommande))){
            log.error("Etat commande value is null");
            throw new InvalidOpperatioException("Impossible de modifier une commande client avec un etat null",
                    ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE
            );
        }

        CommandeClientDto commandeClient = getCommandeClientDto(id, ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);

        commandeClient.setEtatcommande(etatCommande);
        CommandeClient savedCommandeClient = this.commandeClientRepository.save(CommandeClientDto.toEntity(commandeClient));
        if (commandeClient.isCommandeLivree()){
            updateMvtStk(id);
        }
        return CommandeClientDto.fromEntity(savedCommandeClient);
    }

    @Override
    public CommandeClientDto updateQuantiterCommande(Integer idCommande, Integer idLigneCommande, BigDecimal quantite) {
        checkIdCommande(idCommande, ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
        checkIdLigneCommande(idLigneCommande);

        if (quantite == null || quantite.compareTo(BigDecimal.ZERO) == 0){
            log.error("Quantite commande is null");
            throw new InvalidOpperatioException("Impossible de modifier une commande client avec une quantite null ou ZERRO",
                    ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE
            );
        }

        CommandeClientDto commandeClient = getCommandeClientDto(idCommande, ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
        LigneCommandeClient ligneCommandeClient = getLigneCommandeClient(idLigneCommande);
        ligneCommandeClient.setQuantite(quantite);
        this.ligneCommandeClientRepository.save(ligneCommandeClient);

        return commandeClient;
    }

    @Override
    public CommandeClientDto updateClient(Integer idCommande, Integer idClient) {
        checkIdCommande(idCommande, ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
        if (idClient == null){
            log.error("Client ID is null");
            throw new InvalidOpperatioException("Impossible de modifier une commande client avec un client null",
                    ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE
            );
        }

        CommandeClientDto commandeClient = getCommandeClientDto(idCommande, ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);

        Optional<Client> clientOptional = this.clientRepository.findById(idClient);
        if (!clientOptional.isPresent()){
            throw new EntityNotFoundException(
                    "Aucun client n'a ete trouver avec l'ID = " + idClient,
                    ErrorsCode.CLIENT_NOT_FOUND
            );
        }
        commandeClient.setClient(ClientDto.fromEntity(clientOptional.get()));
        return CommandeClientDto.fromEntity(
                this.commandeClientRepository.save(CommandeClientDto.toEntity(commandeClient))
        );
    }

    @Override
    public CommandeClientDto updateArticle(Integer idCommande, Integer idLigneCommande, Integer newIdArticle) {
        checkIdCommande(idCommande, ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
        Article article = checkIdArticle(newIdArticle, "nouveau");
        CommandeClientDto commandeClientDto = getCommandeClientDto(idCommande, ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
        LigneCommandeClient ligneCommandeClient = getLigneCommandeClient(idLigneCommande);
        ligneCommandeClient.setArticle(article);
        this.ligneCommandeClientRepository.save(ligneCommandeClient);
        return commandeClientDto;
    }

    @Override
    public CommandeClientDto deleteArticle(Integer idCommande, Integer idLigneCommande) {
        checkIdCommande(idCommande, ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
        CommandeClientDto commandeClientDto = getCommandeClientDto(idCommande, ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
        checkIdLigneCommande(idLigneCommande);
        getLigneCommandeClient(idLigneCommande);
        this.ligneCommandeClientRepository.deleteById(idLigneCommande);
        return commandeClientDto;
    }

    @Override
    public List<LigneCommandeClientDto> findAllByCommandeClient(Integer idCommande) {
        checkIdCommande(idCommande, ErrorsCode.COMMANDE_CLIENT_NOT_FOUND);
        return this.ligneCommandeClientRepository.findAllByCommandeClientId(idCommande) != null ?
                this.ligneCommandeClientRepository.findAllByCommandeClientId(idCommande).stream()
                .map(LigneCommandeClientDto::fromEntity)
                .collect(Collectors.toList()) : null;
    }

    @Override
    public CommandeClientDto findById(Integer id) {
        checkIdCommande(id, ErrorsCode.COMMANDE_CLIENT_NOT_FOUND);
        return this.commandeClientRepository.findById(id)
                .map(CommandeClientDto::fromEntity)
                .orElseThrow(()-> new EntityNotFoundException(
                        "Aucune commande client trouver avec l'ID = " + id
                        , ErrorsCode.COMMANDE_CLIENT_NOT_FOUND
                ));
    }

    @Override
    public CommandeClientDto findByCodeCommande(String code) {
        if (!StringUtils.hasLength(code)){
            log.error("Commande client code is null");
            return null;
        }
        return this.commandeClientRepository.findCommandeClientByCode(code)
                .map(CommandeClientDto::fromEntity)
                .orElseThrow(()-> new EntityNotFoundException(
                        "Aucune commande client trouver avec le code = " + code,
                        ErrorsCode.COMMANDE_CLIENT_NOT_FOUND
                ));
    }

    @Override
    public List<CommandeClientDto> findByDateCommande(Instant dateCommande) {
        if (dateCommande == null){
            log.error("Commande client date is null");
            return Collections.emptyList();
        }
        return this.commandeClientRepository.findAllByDateCommande(dateCommande) != null ?
                this.commandeClientRepository.findAllByDateCommande(dateCommande).stream()
                        .map(CommandeClientDto::fromEntity)
                        .collect(Collectors.toList()) : null;
    }

    @Override
    public List<LigneCommandeClientDto> findAllByDateInterval(Instant startDate, Instant endDate) {
        List<LigneCommandeClientDto> ligneCommandeClientDtoList = new ArrayList<>();
        List<CommandeClient> commandeClientList = this.commandeClientRepository.getAllBetweenDates(startDate, endDate);
        commandeClientList.forEach(cmd -> ligneCommandeClientDtoList.addAll(findAllByCommandeClient(cmd.getId())));
        return ligneCommandeClientDtoList;
    }

    @Override
    public List<CommandeClientDto> findAll() {
        return this.commandeClientRepository.findAll().stream()
                .map(CommandeClientDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommandeClientDto> findAllByClientDto(ClientDto clientDto){
        List<String> errors = ClientValidator.validate(clientDto);
        if(!CollectionUtils.isEmpty(errors)){
            log.error("Client is not valid", clientDto);
            throw new InvalidEntityException(
                "Le client n'est pas valide ou n'existe pas pour cette recherche", 
                ErrorsCode.CLIENT_NOT_FOUND, errors
            );
        }
        
        return this.commandeClientRepository.findAllByClient(ClientDto.toEntity(clientDto)).stream()
                .map(CommandeClientDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        checkIdCommande(id, ErrorsCode.COMMANDE_CLIENT_NOT_FOUND);
        this.commandeClientRepository.deleteById(id);
    }

    private CommandeClientDto getCommandeClientDto(Integer id, ErrorsCode errorsCode) {
        CommandeClientDto commandeClient = findById(id);
        if (commandeClient.isCommandeLivree()) {
            throw new InvalidOpperatioException("La commande client est livree et ne peut plus etre modifier",
                    errorsCode);
        }
        return commandeClient;
    }

    private void checkIdCommande(Integer id, ErrorsCode errorsCode) {
        if (id == null) {
            log.error(this.msgIdCommandNull);
            throw new InvalidOpperatioException("Impossible de modifier une commande client avec un ID null",
                    errorsCode);
        }
    }

    private void checkIdLigneCommande(Integer idLigneCommande) {
        if (idLigneCommande == null) {
            log.error("Ligne commande client ID is null");
            throw new InvalidOpperatioException(
                    "Impossible de modifier une commande client avec une ligne commande null",
                    ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
    }

    private Article checkIdArticle(Integer idArticle, String msg) {
        if (idArticle == null) {
            log.error("L'ID de " + msg + " est null");
            throw new InvalidOpperatioException(
                    "Impossible de modifier une commande client avec un " + msg + " ID article null",
                    ErrorsCode.COMMANDE_CLIENT_NON_MODIFIABLE);
        }
        Optional<Article> article = this.articleRepository.findById(idArticle);
        if (!article.isPresent()) {
            throw new EntityNotFoundException(
                    "Aucun article n'a ete trouver avec l'ID = " + idArticle,
                    ErrorsCode.ARTICLE_NOT_FOUND);
        }
        List<String> errors = ArticleValidator.validate(ArticleDto.fromEntity(article.get()));
        if (!errors.isEmpty()) {
            log.error("Article is not invalid");
            throw new InvalidEntityException("L'article' n'est pas valid",
                    ErrorsCode.ARTICLE_NOT_VALID, errors);
        }

        return article.get();
    }

    private LigneCommandeClient getLigneCommandeClient(Integer idLigneCommande) {
        Optional<LigneCommandeClient> ligneCommandeClientOptional = this.ligneCommandeClientRepository
                .findById(idLigneCommande);
        if (!ligneCommandeClientOptional.isPresent()) {
            throw new EntityNotFoundException(
                    "Aucune ligne commande client n'a ete trouver avec l'ID = " + idLigneCommande,
                    ErrorsCode.LIGNE_COMMANDE_CLIENT_NOT_FOUND);
        }
        return ligneCommandeClientOptional.get();
    }

    private void updateMvtStk(Integer idCommande) {
        List<LigneCommandeClient> ligneCommandeClients = this.ligneCommandeClientRepository
                .findAllByCommandeClientId(idCommande);
        ligneCommandeClients.forEach(ligne -> {
            MvtStkDto mvtStkDto = MvtStkDto.builder()
                    .article(ArticleDto.fromEntity(ligne.getArticle()))
                    .dateMvt(Instant.now())
                    .typeMvt(TypeMvt.SORTIE)
                    .sourceMvt(SourceMvtStk.COMMANDE_CLIENT)
                    .quantite(ligne.getQuantite())
                    .identreprise(ligne.getIdentreprise())
                    .build();
            this.mvtStkService.sortieMvtStk(mvtStkDto);
        });
    }
}
