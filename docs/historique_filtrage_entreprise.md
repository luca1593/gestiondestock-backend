# Historique de conversation - Filtrage par entreprise

## Contexte
Correction des erreurs de build dans le projet de gestion de stock, notamment liées au filtrage des données par entreprise.

## Problèmes résolus

### 1. Erreur findAllByIdentreprise pour Utilisateur
- **Erreur**: `No property 'identreprise' found for type 'Utilisateur'; Did you mean 'entreprise'`
- **Cause**: La méthode `findAllByIdentreprise` dans UtilisateurRepository utilisait un nom de propriété invalide
- **Solution**: Remplacer par `findAllByEntreprise` avec une requête JPQL explicite

### 2. Erreur de type pour findAllByEntreprise
- **Erreur**: `Can't compare test expression of type [Entreprise] with element of type [basicType@2(java.lang.Integer,4)]`
- **Cause**: Spring Data ne peut pas comparer automatiquement `Entreprise` (entité) avec `Integer` (id)
- **Solution**: Utiliser une requête JPQL explicite avec `@Query`

### 3. Simplification finale
- **Décision**: Supprimer les méthodes de filtrage explicite et utiliser l'intercepteur Hibernate
- **Justification**: L'intercepteur Hibernate filtre déjà automatiquement par entreprise via MDC
- **Exception**: `findAllByIdentreprise` garder dans ArticleRepository pour DashboardServiceImpl (qui ne passe pas par le contrôleur standard)

### 4. Contrainte NOT NULL sur entreprise
- **Modification**: Ajouter `nullable = false` sur `Utilisateur.entreprise`
- **Justification**: L'entreprise est un attribut obligatoire pour un utilisateur

## Fichiers modifiés

### UtilisateurRepository.java
- Supprimé: `findAllByIdentreprise`, `findAllByEntreprise`
- L'intercepteur Hibernate gère maintenant le filtrage

### Utilisateur.java (Model)
- Modifié: `@JoinColumn(name = "identreprise", nullable = false)`

### ArticleRepository.java
- Gardé: `findAllByIdentreprise` avec `@Query` (nécessaire pour DashboardServiceImpl)

### UtilisateurServiceImpl.java
- Modifié: `findAll()` utilise maintenant `findAll()` standard

### ArticleServiceImpl.java
- Modifié: `findAll()` utilise maintenant `findAll()` standard

### DashboardServiceImpl.java
-inchangé: Utilise toujours `findAllByIdentreprise` pour les statistiques

## Commits créés

1. `5e51b48` - Utiliser findAllByEntreprise au lieu de findAllByIdentreprise
2. `76d929d` - Corriger findAllByEntreprise avec Query JPQL pour Utilisateur et Article
3. `41bc9ee` - Supprimer findAllByEntreprise du ArticleRepository - utiliser interceptor Hibernate
4. `2001bb9` - Supprimer findAllByEntreprise de UtilisateurRepository - utiliser interceptor Hibernate
5. `b046199` - Rendre entreprise obligatoire dans Utilisateur (nullable=false)

## Notes importantes

- L'intercepteur Hibernate filtre automatiquement les requêtes SQL par entreprise via le MDC
- Pour Utilisateur, l'entreprise est maintenant obligatoire (NOT NULL)
- Le Dashboard utilise toujours des requêtes explicites car les statistiques nécessitent un filtrage par entreprise

## Date: 12 Avril 2026
