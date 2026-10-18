-- V4 : alignement du schema sur les entites JPA
-- ==============================================
--
-- Contexte (defaut reel : une base NEUVE ne peut pas demarrer).
-- La base de production a ete creee historiquement par Hibernate (ddl-auto=update),
-- PUIS mise sous Flyway via un "baseline" (installed_rank 0). Resultat : deux objets
-- existent en production mais sont ABSENTS des migrations V1/V3 :
--
--   1) `compteur_facture` : V3 la cree SANS les colonnes d'audit de AbstractEntity
--      (`creation_date`, `last_modified_date`). En production la table preexistait,
--      donc le `CREATE TABLE IF NOT EXISTS` de V3 a ete un no-op et les colonnes
--      venaient d'Hibernate. Sur une base neuve, Hibernate genere
--      `select ... c.creation_date ... from compteur_facture` => SQLSyntaxError =>
--      HTTP 500 sur POST /v1/factures/save.
--
--   2) `inventaire_ligne` : table referencee par l'entite InventaireLigne mais creee
--      par AUCUNE migration. Sur une base neuve, tout GET /v1/inventaires/** renvoie
--      500 (`Table 'gestiondestock.inventaire_ligne' doesn't exist`).
--
-- Cette migration est ecrite pour etre IDEMPOTENTE : sur la production (ou les objets
-- existent deja) elle ne fait rien ; sur une base neuve elle complete le schema.
-- On garde volontairement le SQL portable (information_schema + PREPARE), car
-- `ALTER TABLE ... ADD COLUMN IF NOT EXISTS` n'existe pas sur MySQL 8.

-- 1) compteur_facture : ajouter les colonnes d'audit si elles manquent.
SET @ddl := (
  SELECT IF(COUNT(*) = 0,
            'ALTER TABLE `compteur_facture`
               ADD COLUMN `creation_date` datetime(6) NOT NULL,
               ADD COLUMN `last_modified_date` datetime(6) DEFAULT NULL',
            'DO 0')
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME = 'compteur_facture'
    AND COLUMN_NAME = 'creation_date');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) inventaire_ligne : creer la table si elle n'existe pas.
CREATE TABLE IF NOT EXISTS `inventaire_ligne` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `creation_date` datetime(6) NOT NULL,
  `last_modified_date` datetime(6) DEFAULT NULL,
  `identreprise` int(11) DEFAULT NULL,
  `quantite_reelle` decimal(38,2) DEFAULT NULL,
  `quantite_theorique` decimal(38,2) DEFAULT NULL,
  `idarticle` int(11) DEFAULT NULL,
  `idinventaire` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_inventaire_ligne_article` (`idarticle`),
  KEY `FK_inventaire_ligne_inventaire` (`idinventaire`),
  CONSTRAINT `FK_inventaire_ligne_article` FOREIGN KEY (`idarticle`) REFERENCES `article` (`id`),
  CONSTRAINT `FK_inventaire_ligne_inventaire` FOREIGN KEY (`idinventaire`) REFERENCES `inventaire` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
