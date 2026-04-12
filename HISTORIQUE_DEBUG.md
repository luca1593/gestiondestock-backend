# Historique de conversation - Débogage Filtre Entreprise

## Date: 2026-04-12

## Problème
Les services et contrôleurs ne renvoyaient aucune donnée. Le filtre Hibernate sur `identreprise` ne fonctionnait pas.

## Analyse effectuée

### 1. Structure du code
- `Interceptor.java` : Implement StatementInspector pour filtrer les requêtes SQL par idEntreprise
- `HibernateConfig.java` : Enregistre l'interceptor via HibernatePropertiesCustomizer
- `ApplicationRequestFilter.java` : Définit le MDC avec idEntreprise depuis le JWT
- `JwtUtil.java` : Génère et extrait le claim `identreprise` du token

### 2. Cause racine identifiée (depuis les logs en prod)
```
You have an error in your SQL syntax; check the manual...
```
Le SQL généré était corrupté - il manquait le WHERE clause.

L'erreur:
```sql
select c1_0.id,... from commande_client c1_0
```
(Devrait être):
```sql
select c1_0.id,... from commande_client c1_0 WHERE (c1_0.identreprise = X OR c1_0.identreprise IS NULL)
```

### 3. Bug dans findMainFromIndex()
La méthode retournait `-1` dans certains cas, causant la corruption du SQL lors de la construction de la clause WHERE.

## Modifications apportées

### Interceptor.java
- Ajout logging ERROR quand idEntreprise non défini dans MDC
- Ajout logging ERROR quand le parsing échoue
- Ajout logging DEBUG pour tracer l'extraction de table
- Amélioration du logging pour hasWhere et fromIndex
- Correction de findMainFromIndex()

### logback-spring.xml
- Ajout APP_FILE pour les logs de l'intercepteur

## Commits
```
1a09db5 Débogage du filtre Hibernate par entreprise
ae3320e Ajouter volume Docker pour persister les logs en prod
21db04f Ajouter l'email de l'utilisateur dans les logs
```

## Prochaine étape
Rebuild et redéployer pour vérifier que le filtre fonctionne maintenant.