CREATE TABLE IF NOT EXISTS `entreprise` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `adresse1` varchar(255) DEFAULT NULL,
  `adresse2` varchar(255) DEFAULT NULL,
  `ville` varchar(255) DEFAULT NULL,
  `code_postal` varchar(255) DEFAULT NULL,
  `pays` varchar(255) DEFAULT NULL,
  `code_fiscal` varchar(255) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `num_tel` varchar(255) DEFAULT NULL,
  `site_web` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `utilisateur` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `prenom` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `date_de_naissance` datetime(6) DEFAULT NULL,
  `mot_de_passe` varchar(255) DEFAULT NULL,
  `adresse1` varchar(255) DEFAULT NULL,
  `adresse2` varchar(255) DEFAULT NULL,
  `ville` varchar(255) DEFAULT NULL,
  `code_postal` varchar(255) DEFAULT NULL,
  `pays` varchar(255) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `identreprise` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_utilisateur_email` (`email`),
  KEY `FK_utilisateur_entreprise` (`identreprise`),
  CONSTRAINT `FK_utilisateur_entreprise` FOREIGN KEY (`identreprise`) REFERENCES `entreprise` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `client` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `prenom` varchar(255) DEFAULT NULL,
  `adresse1` varchar(255) DEFAULT NULL,
  `adresse2` varchar(255) DEFAULT NULL,
  `ville` varchar(255) DEFAULT NULL,
  `code_postal` varchar(255) DEFAULT NULL,
  `pays` varchar(255) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `num_tel` varchar(255) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `fournisseur` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `prenom` varchar(255) DEFAULT NULL,
  `adresse1` varchar(255) DEFAULT NULL,
  `adresse2` varchar(255) DEFAULT NULL,
  `ville` varchar(255) DEFAULT NULL,
  `code_postal` varchar(255) DEFAULT NULL,
  `pays` varchar(255) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `num_tel` varchar(255) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `article` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `codearticle` varchar(255) DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `prixunitaireht` decimal(38,2) DEFAULT NULL,
  `tauxtva` decimal(38,2) DEFAULT NULL,
  `prixttc` decimal(38,2) DEFAULT NULL,
  `stock` decimal(38,2) DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL,
  `id_category` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_article_category` (`id_category`),
  KEY `FK_article_entreprise` (`identreprise`),
  CONSTRAINT `FK_article_category` FOREIGN KEY (`id_category`) REFERENCES `category` (`id`),
  CONSTRAINT `FK_article_entreprise` FOREIGN KEY (`identreprise`) REFERENCES `entreprise` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `vente` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `date_vente` datetime(6) DEFAULT NULL,
  `commentaire` varchar(255) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `avoir` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `date_avoir` datetime(6) DEFAULT NULL,
  `montant` double DEFAULT NULL,
  `raison` varchar(255) DEFAULT NULL,
  `etat` varchar(255) DEFAULT NULL,
  `idclient` int(11) DEFAULT NULL,
  `idvente` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_avoir_client` (`idclient`),
  KEY `FK_avoir_vente` (`idvente`),
  CONSTRAINT `FK_avoir_client` FOREIGN KEY (`idclient`) REFERENCES `client` (`id`),
  CONSTRAINT `FK_avoir_vente` FOREIGN KEY (`idvente`) REFERENCES `vente` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `commande_client` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `datecommande` datetime(6) DEFAULT NULL,
  `etatcommande` tinyint(4) DEFAULT NULL,
  `idclient` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_commande_client_client` (`idclient`),
  CONSTRAINT `FK_commande_client_client` FOREIGN KEY (`idclient`) REFERENCES `client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `commande_fournisseur` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `datecommande` datetime(6) DEFAULT NULL,
  `etatcommande` tinyint(4) DEFAULT NULL,
  `idfournisseur` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_commande_fournisseur_fournisseur` (`idfournisseur`),
  CONSTRAINT `FK_commande_fournisseur_fournisseur` FOREIGN KEY (`idfournisseur`) REFERENCES `fournisseur` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `entrepot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `adresse1` varchar(255) DEFAULT NULL,
  `adresse2` varchar(255) DEFAULT NULL,
  `ville` varchar(255) DEFAULT NULL,
  `code_postal` varchar(255) DEFAULT NULL,
  `pays` varchar(255) DEFAULT NULL,
  `est_principal` bit(1) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_entrepot_entreprise` (`identreprise`),
  CONSTRAINT `FK_entrepot_entreprise` FOREIGN KEY (`identreprise`) REFERENCES `entreprise` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `facture` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `numero_facture` varchar(255) NOT NULL,
  `date_facture` datetime(6) DEFAULT NULL,
  `date_echeance` datetime(6) DEFAULT NULL,
  `statut` enum('ANNULEE','EN_ATTENTE','EN_RETARD','PARTIELLEMENT_PAYEE','PAYEE') DEFAULT NULL,
  `montantht` decimal(38,2) DEFAULT NULL,
  `montanttva` decimal(38,2) DEFAULT NULL,
  `montantttc` decimal(38,2) DEFAULT NULL,
  `montant_paye` decimal(38,2) DEFAULT NULL,
  `montant_restant` decimal(38,2) DEFAULT NULL,
  `id_client` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_facture_numero` (`numero_facture`),
  KEY `FK_facture_client` (`id_client`),
  KEY `FK_facture_entreprise` (`identreprise`),
  CONSTRAINT `FK_facture_client` FOREIGN KEY (`id_client`) REFERENCES `client` (`id`),
  CONSTRAINT `FK_facture_entreprise` FOREIGN KEY (`identreprise`) REFERENCES `entreprise` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `inventaire` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `date_debut` datetime(6) DEFAULT NULL,
  `date_fin` datetime(6) DEFAULT NULL,
  `statut` enum('ANNULE','EN_COURS','TERMINE') DEFAULT NULL,
  `entrepot_id` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_inventaire_entrepot` (`entrepot_id`),
  KEY `FK_inventaire_entreprise` (`identreprise`),
  CONSTRAINT `FK_inventaire_entrepot` FOREIGN KEY (`entrepot_id`) REFERENCES `entrepot` (`id`),
  CONSTRAINT `FK_inventaire_entreprise` FOREIGN KEY (`identreprise`) REFERENCES `entreprise` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `ligne_commande_client` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `quantite` decimal(38,2) DEFAULT NULL,
  `prixunitaire` decimal(38,2) DEFAULT NULL,
  `idarticle` int(11) DEFAULT NULL,
  `idcommandeclient` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_lcc_article` (`idarticle`),
  KEY `FK_lcc_commande_client` (`idcommandeclient`),
  CONSTRAINT `FK_lcc_article` FOREIGN KEY (`idarticle`) REFERENCES `article` (`id`),
  CONSTRAINT `FK_lcc_commande_client` FOREIGN KEY (`idcommandeclient`) REFERENCES `commande_client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `ligne_command_fournisseur` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `quantite` decimal(38,2) DEFAULT NULL,
  `prixunitaire` decimal(38,2) DEFAULT NULL,
  `idarticle` int(11) DEFAULT NULL,
  `idcommandefournisseur` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_lcf_article` (`idarticle`),
  KEY `FK_lcf_commande_fournisseur` (`idcommandefournisseur`),
  CONSTRAINT `FK_lcf_article` FOREIGN KEY (`idarticle`) REFERENCES `article` (`id`),
  CONSTRAINT `FK_lcf_commande_fournisseur` FOREIGN KEY (`idcommandefournisseur`) REFERENCES `commande_fournisseur` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `ligne_vente` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `quantite` decimal(38,2) DEFAULT NULL,
  `prix_unitaire` decimal(38,2) DEFAULT NULL,
  `idarticle` int(11) DEFAULT NULL,
  `idvente` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_lv_article` (`idarticle`),
  KEY `FK_lv_vente` (`idvente`),
  CONSTRAINT `FK_lv_article` FOREIGN KEY (`idarticle`) REFERENCES `article` (`id`),
  CONSTRAINT `FK_lv_vente` FOREIGN KEY (`idvente`) REFERENCES `vente` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `lot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `numero_lot` varchar(255) NOT NULL,
  `quantite` decimal(38,2) DEFAULT NULL,
  `quantite_restante` decimal(38,2) DEFAULT NULL,
  `date_fabrication` datetime(6) DEFAULT NULL,
  `date_expiration` datetime(6) DEFAULT NULL,
  `prix_achat` decimal(38,2) DEFAULT NULL,
  `id_article` int(11) DEFAULT NULL,
  `entrepot_id` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  `est_completement_utilise` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_lot_numero` (`numero_lot`),
  KEY `FK_lot_article` (`id_article`),
  KEY `FK_lot_entrepot` (`entrepot_id`),
  KEY `FK_lot_entreprise` (`identreprise`),
  CONSTRAINT `FK_lot_article` FOREIGN KEY (`id_article`) REFERENCES `article` (`id`),
  CONSTRAINT `FK_lot_entrepot` FOREIGN KEY (`entrepot_id`) REFERENCES `entrepot` (`id`),
  CONSTRAINT `FK_lot_entreprise` FOREIGN KEY (`identreprise`) REFERENCES `entreprise` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `mvt_stk` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `date_mvt` datetime(6) DEFAULT NULL,
  `quantite` decimal(38,2) DEFAULT NULL,
  `typemvt` tinyint(4) DEFAULT NULL,
  `sourcemvtstk` tinyint(4) DEFAULT NULL,
  `idarticle` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_mvtstk_article` (`idarticle`),
  CONSTRAINT `FK_mvtstk_article` FOREIGN KEY (`idarticle`) REFERENCES `article` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `paiement` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `montant` decimal(38,2) DEFAULT NULL,
  `date_paiement` datetime(6) DEFAULT NULL,
  `mode_paiement` enum('AUTRE','CARTE_BANCAIRE','CHEQUE','ESPECES','MOBILE_MONEY','VIREMENT_BANCAIRE') DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `facture_id` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_paiement_facture` (`facture_id`),
  KEY `FK_paiement_entreprise` (`identreprise`),
  CONSTRAINT `FK_paiement_facture` FOREIGN KEY (`facture_id`) REFERENCES `facture` (`id`),
  CONSTRAINT `FK_paiement_entreprise` FOREIGN KEY (`identreprise`) REFERENCES `entreprise` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `regle_tarifaire` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `nom` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `type_regle` enum('PRIX_SPECIFIQUE','PROMOTION','REMISE_MONTANT_FIXE','REMISE_POURCENTAGE') DEFAULT NULL,
  `valeur` decimal(38,2) DEFAULT NULL,
  `est_actif` bit(1) DEFAULT NULL,
  `date_debut` datetime(6) DEFAULT NULL,
  `date_fin` datetime(6) DEFAULT NULL,
  `quantite_minimale` decimal(38,2) DEFAULT NULL,
  `montant_minimal` decimal(38,2) DEFAULT NULL,
  `id_category` int(11) DEFAULT NULL,
  `id_client` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_regletarifaire_category` (`id_category`),
  KEY `FK_regletarifaire_client` (`id_client`),
  KEY `FK_regletarifaire_entreprise` (`identreprise`),
  CONSTRAINT `FK_regletarifaire_category` FOREIGN KEY (`id_category`) REFERENCES `category` (`id`),
  CONSTRAINT `FK_regletarifaire_client` FOREIGN KEY (`id_client`) REFERENCES `client` (`id`),
  CONSTRAINT `FK_regletarifaire_entreprise` FOREIGN KEY (`identreprise`) REFERENCES `entreprise` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `role_nom` varchar(255) DEFAULT NULL,
  `idutilisateur` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_role_utilisateur` (`idutilisateur`),
  CONSTRAINT `FK_role_utilisateur` FOREIGN KEY (`idutilisateur`) REFERENCES `utilisateur` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `transfert_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `date_transfert` datetime(6) DEFAULT NULL,
  `statut` enum('ANNULE','EN_ATTENTE','EN_TRANSIT','RECU') DEFAULT NULL,
  `entrepot_source_id` int(11) DEFAULT NULL,
  `entrepot_destination_id` int(11) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_transfert_source` (`entrepot_source_id`),
  KEY `FK_transfert_destination` (`entrepot_destination_id`),
  KEY `FK_transfert_entreprise` (`identreprise`),
  CONSTRAINT `FK_transfert_source` FOREIGN KEY (`entrepot_source_id`) REFERENCES `entrepot` (`id`),
  CONSTRAINT `FK_transfert_destination` FOREIGN KEY (`entrepot_destination_id`) REFERENCES `entrepot` (`id`),
  CONSTRAINT `FK_transfert_entreprise` FOREIGN KEY (`identreprise`) REFERENCES `entreprise` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `alerte_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `article_id` int(11) DEFAULT NULL,
  `designation` varchar(255) DEFAULT NULL,
  `stock_actuel` double DEFAULT NULL,
  `seuil_minimum` double DEFAULT NULL,
  `seuil_critique` double DEFAULT NULL,
  `niveau_alerte` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
