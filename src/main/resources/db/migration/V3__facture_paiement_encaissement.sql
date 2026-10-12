-- V3 : encaissement des factures
-- 1) Type de facture (client/fournisseur) + rattachement polymorphe a un document
--    source (vente, commande client, commande fournisseur) + lien fournisseur.
-- 2) Somme remise par le payeur et monnaie rendue (paiement en especes).
-- 3) Compteur de numerotation des factures par entreprise et par annee.
--
-- NB : version 3 car un ancien script local "fix auto increment" a deja occupe la version 2.

-- 1) Facture
ALTER TABLE `facture`
  ADD COLUMN `type_facture` enum('CLIENT','FOURNISSEUR') NOT NULL DEFAULT 'CLIENT',
  ADD COLUMN `source_type` varchar(30) DEFAULT NULL,
  ADD COLUMN `source_id` int(11) DEFAULT NULL,
  ADD COLUMN `idfournisseur` int(11) DEFAULT NULL,
  ADD KEY `IDX_facture_source` (`source_type`,`source_id`),
  ADD CONSTRAINT `FK_facture_fournisseur` FOREIGN KEY (`idfournisseur`) REFERENCES `fournisseur` (`id`);

-- 2) Paiement
ALTER TABLE `paiement`
  ADD COLUMN `montant_donne` decimal(38,2) DEFAULT NULL,
  ADD COLUMN `monnaie` decimal(38,2) DEFAULT NULL;

-- 3) Compteur de numerotation
CREATE TABLE IF NOT EXISTS `compteur_facture` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `identreprise` int(11) NOT NULL,
  `annee` int(11) NOT NULL,
  `valeur` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_compteur_facture` (`identreprise`,`annee`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
