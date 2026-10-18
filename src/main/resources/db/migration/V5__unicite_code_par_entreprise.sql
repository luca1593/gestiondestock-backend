-- V5 : unicite du code article et du code categorie PAR ENTREPRISE
-- ================================================================
--
-- Regle metier : le code d'un article et le code d'une categorie doivent etre
-- uniques AU SEIN d'une meme entreprise. Deux entreprises differentes peuvent
-- donc parfaitement utiliser le meme code (le cloisonnement multi-tenant le permet).
--
-- PREREQUIS IMPERATIF : aucune duplication (identreprise, code) ne doit subsister.
-- La base de production issue des campagnes de test passees en contient
-- (article ART_PM x3, categorie POSTMAN x12, ...) : il faut d'abord nettoyer avec
--   sql/dedupe_codes_par_entreprise.sql
-- sinon cette migration s'interrompt avec un message explicite.
--
-- NB : MySQL autorise plusieurs NULL dans un index unique. Un article/categorie
-- sans code -- ou sans entreprise -- n'est donc pas contraint par l'index ; le
-- controle applicatif (services Article/Category) exige deja un code.

-- 1) Controle des doublons ARTICLE : message actionnable plutot qu'une erreur SQL brute.
SET @dup_article := (
  SELECT COUNT(*) FROM (
    SELECT identreprise, codearticle FROM article
    WHERE codearticle IS NOT NULL
    GROUP BY identreprise, codearticle
    HAVING COUNT(*) > 1
  ) d);
SET @sql := IF(@dup_article = 0, 'DO 0',
  CONCAT('SIGNAL SQLSTATE ''45000'' SET MESSAGE_TEXT = ''MIGRATION V5 : ',
         @dup_article,
         ' code(s) article duplique(s) par entreprise. Executez sql/dedupe_codes_par_entreprise.sql puis relancez.'''));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) Controle des doublons CATEGORY.
SET @dup_category := (
  SELECT COUNT(*) FROM (
    SELECT identreprise, code FROM category
    WHERE code IS NOT NULL
    GROUP BY identreprise, code
    HAVING COUNT(*) > 1
  ) d);
SET @sql := IF(@dup_category = 0, 'DO 0',
  CONCAT('SIGNAL SQLSTATE ''45000'' SET MESSAGE_TEXT = ''MIGRATION V5 : ',
         @dup_category,
         ' code(s) categorie duplique(s) par entreprise. Executez sql/dedupe_codes_par_entreprise.sql puis relancez.'''));
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) Index unique (identreprise, codearticle) sur article.
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'article'
       AND INDEX_NAME = 'UK_article_entreprise_codearticle') = 0,
  'ALTER TABLE `article` ADD UNIQUE KEY `UK_article_entreprise_codearticle` (`identreprise`, `codearticle`)',
  'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 4) Index unique (identreprise, code) sur category.
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'category'
       AND INDEX_NAME = 'UK_category_entreprise_code') = 0,
  'ALTER TABLE `category` ADD UNIQUE KEY `UK_category_entreprise_code` (`identreprise`, `code`)',
  'DO 0');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
