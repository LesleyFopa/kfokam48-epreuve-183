# Rapport de tests — v0.1

Couvre tout ce qui a été vérifié depuis le jalon `[JALON] v0.1` : les 10 endpoints Must, les 3 écrans, et les correctifs qui ont suivi (`GET /api/sessions/active`, données de démo enrichies). Deux niveaux de preuve : les tests automatisés (rejouables par `./mvnw test`) et les tests manuels de bout en bout (rejoués via navigateur, résultats consignés ici car non automatisés).

## 1. Tests automatisés (backend)

```bash
cd backend && ./mvnw test
```

**Résultat : 10/10 tests passent.**

| Classe | Tests | Ce qui est vérifié |
|---|---|---|
| `TentativeCodeTrackerTest` | 3 | RG3 — pas de blocage avant 5 échecs, blocage au 5e, réinitialisation du compteur après succès |
| `RelectureServiceTest` | 4 | RG8 — note > 20 et note négative rejetées ; RG4 — un relecteur ne peut pas relire son propre exercice ; cas nominal accepté (statuts mis à jour) |
| `SessionControllerTest` | 2 | EF1 bout en bout via MockMvc + vraie base H2/Flyway — 201 avec code et dates ; 400 avec le format d'erreur imposé si un champ manque |
| `PresenceApplicationTests` | 1 | Le contexte Spring démarre sans erreur (migrations comprises) |

Les deux premières classes sont des tests unitaires purs (repositories mockés ou aucune dépendance), sans contexte Spring — rapides et ciblés sur une règle métier précise. La troisième est le test d'intégration bout en bout exigé par B6.

## 2. Tests manuels — conformité au contrat d'API

Chaque opération testée avec `curl` contre le format d'erreur imposé (`{code, message}`) et les codes HTTP déclarés dans `api/contrat.yaml`.

| Opération | Cas testés | Résultat |
|---|---|---|
| `GET /api/promotions/{id}/etudiants` | Liste valide · promotion inconnue | 200 liste · 404 `PROMOTION_INCONNUE` ✅ |
| `POST /api/sessions` | Création valide · titre manquant | 201 avec code+dates · 400 `CHAMP_INVALIDE` ✅ |
| `GET /api/sessions/active` | Session ouverte existante | 200 avec code+dates ✅ |
| `POST /api/presences` | Succès · déjà présent · code inconnu · 5 échecs consécutifs | 201 · 409 `DEJA_PRESENT` · 400 `CODE_INCONNU` · 429 `TROP_DE_TENTATIVES` au 6e essai ✅ |
| `POST /api/sessions/{id}/presences` | Ajout manuel · session clôturée | 201 source `FORMATEUR` · 409 `SESSION_CLOTUREE` ✅ |
| `POST /api/exercices` | Dépôt + assignation auto · lien invalide · déjà déposé | 201 avec statut `EN_ATTENTE` (relecteur assigné) · 400 `LIEN_INVALIDE` · 409 `EXERCICE_DEJA_DEPOSE` ✅ |
| `POST /api/relectures/{id}` | Note valide · note hors 0-20 · déjà rendue | 200 · 400 `NOTE_INVALIDE` · 409 `RELECTURE_DEJA_RENDUE` ✅ |
| `GET /api/relectures?relecteurId=` | Relecteur avec relecture assignée · sans relecture | Liste correcte dans les deux cas ✅ |
| `GET /api/exercices?etudiantId=` | Exercice relu · exercice en attente | Note/commentaire visibles seulement si `RENDUE`, relecteur jamais exposé ✅ |
| `PATCH /api/sessions/{id}/cloturer` | Clôture · double clôture | 204 · 409 `SESSION_DEJA_CLOTUREE` ✅ |
| `GET /api/tableau?promotionId=` | Agrégation par étudiant · promotion inconnue | Valeurs correctes (présences, dépôts, moyenne, relectures dues) · 404 ✅ |

Aucune stack trace ni page d'erreur Spring par défaut observée sur aucun cas testé (B4).

## 3. Tests manuels — parcours de bout en bout (navigateur)

Parcours complet rejoué dans le navigateur (Formateur → Étudiant → Relecteur → Formateur), aucune erreur console à aucune étape :

1. **Formateur** ouvre une session → code généré et affiché
2. **Étudiant** choisit son nom, saisit le code → « Présence enregistrée »
3. **Étudiant** dépose un exercice → « Exercice déposé », un relecteur est assigné automatiquement parmi les présents
4. **Relecteur** (l'étudiant tiré au sort) retrouve la relecture assignée dans sa liste
5. **Relecteur** note et commente → statut passe à « Rendue »
6. **Formateur** revient sur le tableau de bord → moyenne et compteurs mis à jour, graphiques reflètent les vraies données (aucune donnée inventée, F3)
7. **Formateur** ajoute une présence manuelle → compteur mis à jour, tag « ajouté par le formateur » (source `FORMATEUR`)
8. **Formateur** clôture la session → formulaire d'ouverture réapparaît ; nouvelle tentative d'action sur l'ancienne session refusée (`SESSION_CLOTUREE`)
9. **Rechargement de la page** sur l'écran Formateur → la session ouverte est retrouvée automatiquement via `GET /api/sessions/active`, sans action de l'utilisateur

Vérifié séparément : bascule clair/sombre (auto + bouton manuel), persistance du choix de thème et de l'identité étudiant après changement d'écran.

## 4. Ce qui n'est pas couvert

- **`PUT /api/relectures/{id}`** (correction de note, EF9) et **`PUT /api/exercices/{id}`** (remplacement de lien, EF10) : définis dans le contrat, pas encore implémentés côté backend ni frontend — prévus pour v1.0 (stories Should, issues #9 et #6)
- **Aucune authentification** sur les actions formateur (ouvrir/clôturer une session, ajouter une présence) — cohérent avec « pas de mot de passe » (Q1) qui ne concerne que les étudiants, mais reste une absence de contrôle non testée en tant que telle
- **`TentativeCodeTracker`** (RG3) : compteur en mémoire, perdu au redémarrage du serveur — acceptable à l'échelle d'une session de cours, non prévu pour un usage distribué

## 5. Reproduire ces tests

```bash
# Automatisés
cd backend && ./mvnw test

# Manuels
cd backend && ./mvnw spring-boot:run   # terminal 1
cd frontend && npm install && npm run dev   # terminal 2
# puis suivre le parcours de la section 3 sur http://localhost:5173
```
