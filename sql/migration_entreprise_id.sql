-- Migration: peupler entreprise_id depuis identreprise pour les lignes existantes
-- Le fix dual-mapping a changé la colonne FK de identreprise vers entreprise_id
-- Hibernate a créé entreprise_id vide, il faut copier les valeurs depuis identreprise

UPDATE utilisateur SET entreprise_id = identreprise WHERE entreprise_id IS NULL AND identreprise IS NOT NULL;
UPDATE article SET entreprise_id = identreprise WHERE entreprise_id IS NULL AND identreprise IS NOT NULL;
UPDATE entrepot SET entreprise_id = identreprise WHERE entreprise_id IS NULL AND identreprise IS NOT NULL;
UPDATE facture SET entreprise_id = identreprise WHERE entreprise_id IS NULL AND identreprise IS NOT NULL;
UPDATE inventaire SET entreprise_id = identreprise WHERE entreprise_id IS NULL AND identreprise IS NOT NULL;
UPDATE lot SET entreprise_id = identreprise WHERE entreprise_id IS NULL AND identreprise IS NOT NULL;
UPDATE paiement SET entreprise_id = identreprise WHERE entreprise_id IS NULL AND identreprise IS NOT NULL;
UPDATE regle_tarifaire SET entreprise_id = identreprise WHERE entreprise_id IS NULL AND identreprise IS NOT NULL;
UPDATE transfert_stock SET entreprise_id = identreprise WHERE entreprise_id IS NULL AND identreprise IS NOT NULL;
