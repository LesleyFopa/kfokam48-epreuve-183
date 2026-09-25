# KFOKAM48 — Suivi de présence, dépôts et relectures

Épreuve finale fullstack KFOKAM48. Voir [`docs/CAHIER_DES_CHARGES.md`](docs/CAHIER_DES_CHARGES.md) pour l'analyse complète (contexte, exigences, règles de gestion, diagrammes).

## Stack

- **Backend** : Java 17, Spring Boot 4.1, Maven (wrapper `mvnw` commité), H2 en mémoire, Flyway
- **Frontend** : React 19 (Vite), choisi parce que la maquette ne compte que 3 écrans simples et que l'écosystème React réduit le risque de blocage sur un point technique dans le temps imparti

## Démarrer en local (3 commandes)

Deux terminaux, backend puis frontend.

```bash
cd backend
./mvnw spring-boot:run
```

Le backend démarre sur `http://localhost:8080`, migre le schéma (Flyway) et charge des données de démonstration (1 promotion, 6 étudiants, 1 session ouverte) à chaque démarrage.

```bash
cd frontend
npm install && npm run dev
```

Le frontend démarre sur `http://localhost:5173`. Si le backend tourne sur un autre port, copier `frontend/.env.example` en `frontend/.env` et ajuster `VITE_API_BASE_URL`.

## Utiliser l'application

1. Onglet **Formateur** : ouvrir une session pour obtenir un code, ou utiliser la session de démo déjà ouverte
2. Onglet **Étudiant** : choisir un nom dans la liste, saisir le code pour marquer sa présence, déposer un exercice (l'identifiant de session est visible dans l'écran Formateur)
3. Onglet **Relecteur** : choisir le nom de l'étudiant assigné comme relecteur (visible dans le tableau de bord Formateur), noter l'exercice qui lui est assigné
4. Onglet **Formateur** : suivre le tableau de bord, clôturer la session quand terminé

## Tests

```bash
cd backend
./mvnw test
```

6 tests : 1 unitaire sur RG3 (blocage après 5 codes erronés), 2 d'intégration sur `POST /api/sessions`, 1 smoke test de contexte Spring.

## Structure

```
/docs      cahier des charges, journal, diagrammes
/api       contrat.yaml (OpenAPI)
/backend   Spring Boot — contrôleur / service / repository / DTO
/frontend  React — couche API dédiée, thème clair/sombre
```

## État du projet

Toutes les stories **Must** du backlog sont livrées (voir les [issues fermées](../../issues?q=is%3Aissue+is%3Aclosed)). Les stories **Should** restantes (correction de note après envoi, remplacement du lien d'exercice) sont prévues pour la version finale.
