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

Le backend démarre sur `http://localhost:8080`, migre le schéma (Flyway) et charge des données de démonstration (1 promotion, 10 étudiants, 2 sessions — 1 ouverte, 1 clôturée) à chaque démarrage.

```bash
cd frontend
npm install && npm run dev
```

Le frontend démarre sur `http://localhost:5173`. Si le backend tourne sur un autre port, copier `frontend/.env.example` en `frontend/.env` et ajuster `VITE_API_BASE_URL`.

## Utiliser l'application

1. Onglet **Formateur** : ouvrir une session pour obtenir un code, ou utiliser la session de démo déjà ouverte
2. Onglet **Étudiant** : choisir un nom dans la liste, saisir le code pour marquer sa présence, déposer un exercice (l'identifiant de session est visible dans l'écran Formateur) — le lien reste remplaçable tant qu'aucune relecture n'a été rendue
3. Onglet **Relecteur** : chaque exercice est assigné à **deux** relecteurs distincts (visibles dans le tableau de bord Formateur) ; chacun note et commente séparément, et peut corriger sa note tant que la session est ouverte
4. Onglet **Étudiant** : la note affichée est la moyenne des deux relectures, marquée « provisoire » tant qu'une seule des deux est rendue
5. Onglet **Formateur** : suivre le tableau de bord, clôturer la session quand terminé

## Tests

```bash
cd backend
./mvnw test
```

11 tests : RG3 (blocage après 5 codes erronés), RG4/RG8 (auto-relecture, note invalide), RG15 (condition de course sur le marquage de présence, deux vrais threads), 2 tests d'intégration sur `POST /api/sessions`, 1 smoke test de contexte Spring.

## Structure

```
/docs      cahier des charges, journal, diagrammes
/api       contrat.yaml (OpenAPI)
/backend   Spring Boot — contrôleur / service / repository / DTO
/frontend  React — couche API dédiée, thème clair/sombre
```

## État du projet

Toutes les stories du backlog sont livrées, Must et Should (voir les [issues fermées](../../issues?q=is%3Aissue+is%3Aclosed)) — plus rien en attente. Voir [`CHANGELOG.md`](CHANGELOG.md) pour le détail par jalon.
