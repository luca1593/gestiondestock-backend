# ADR-002 — Filtrage multi-tenant : passer du « fail-open » au « fail-closed »

- **Statut** : Accepté
- **Date** : 2026-10-17
- **Concerne** : `interceptor/Interceptor.java`, `config/ApplicationRequestFilter.java`,
  `config/SecurityConfiguration.java`, `services/auth/ApplicationUserDetailsService.java`

## Contexte

Le cloisonnement des données par entreprise repose sur un `StatementInspector` Hibernate
(`Interceptor`) qui réécrit à la volée les `SELECT` en injectant
`(alias.identreprise = :id OR alias.identreprise IS NULL)`.

L'audit (voir `CODE-REVIEW-QA.md`) a mis en évidence des défauts graves :

1. **Fail-open** : si `idEntreprise` est absent ou non numérique dans le MDC, l'intercepteur
   renvoie le SQL **sans filtre** (il loggue seulement une erreur). Toute requête exécutée dans
   un contexte authentifié sans entreprise lit donc les données de **toutes** les entreprises.
2. **Réécriture SQL incorrecte** : quand la requête contient déjà un `WHERE`, le filtre est
   ajouté **à la fin** de la chaîne, donc *après* `ORDER BY` / `GROUP BY` / `LIMIT` →
   `SQLSyntaxErrorException` ; et quand la table est en fin de requête sans alias
   (`select count(*) from utilisateur`), aucune table n'est extraite → pas de filtre.

## Contraintes

- Le `login` doit pouvoir retrouver un utilisateur **avant** de connaître son entreprise
  (le tenant est dérivé du JWT, pas encore émis).
- `DataInitializer` (démarrage) et la tâche planifiée `AlertStockServiceImpl` (toutes les 5 min)
  s'exécutent **hors requête HTTP**, donc sans MDC : ils doivent pouvoir parcourir tous les
  tenants.
- Les tables `entreprise`, `role` n'ont pas de colonne `identreprise` et ne doivent pas être filtrées.

## Décision

**Principe : échouer fermé dès qu'une requête est émise dans un contexte authentifié.**

L'intercepteur distingue trois situations à partir du MDC :

| MDC `idEntreprise` | MDC `userEmail` | Contexte | Comportement |
|---|---|---|---|
| présent & valide | — | requête authentifiée | filtre par entreprise (position corrigée) |
| présent & invalide | — | anomalie | **exception** (refus d'exécuter sans filtre) |
| absent | présent | requête authentifiée sans entreprise | **exception** (fail-closed) |
| absent | absent | démarrage / tâche planifiée / login | aucun filtre (contexte système, loggué en debug) |

`userEmail` est la clé MDC posée par `ApplicationRequestFilter` **uniquement** quand un JWT
valide a fourni une identité : sa présence est donc le marqueur d'un contexte authentifié.

Les deux seuls flux légitimes sans entreprise (login, jobs système) ont, par construction,
`userEmail` absent : ils ne sont pas impactés.

## Conséquences

- **Positives** : plus aucune fuite inter-tenant sur une requête authentifiée ; les requêtes
  `WHERE ... ORDER BY ...` ne cassent plus ; le login reste fonctionnel ; le défaut est
  *bruyant* (500) plutôt que silencieux.
- **Négatives / limites assumées** : le filtre reste une réécriture textuelle (pas un
  `@Filter` Hibernate). Une requête `UNION` est refusée explicitement (impossible à filtrer
  sans risque). Les endpoints publics (`/authenticate`, `/**/entreprise/save`) et les jobs
  s'exécutent sans filtre — comportement inchangé, à surveiller si de nouveaux endpoints publics
  interrogent des tables multi-tenant.

## Alternatives écartées

- **`@FilterDef`/`@Filter` Hibernate** : solution la plus propre à terme, mais refonte large
  (toutes les sessions, `enableFilter` par transaction) incompatible avec un correctif ciblé.
- **Fail-closed absolu** (toujours lever) : casse le login, le démarrage et les alertes.
- **Marqueur de contexte système explicite** (MDC `systemContext`) : plus verbeux, impose de
  toucher chaque appel système ; l'heuristique `userEmail` couvre les mêmes cas sans risque de
  « fail-open silencieux » supplémentaire.
