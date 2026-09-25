# Changelog

Format libre, organisé par jalon. Les références `EFx`/`RGx` renvoient à [`docs/CAHIER_DES_CHARGES.md`](docs/CAHIER_DES_CHARGES.md).

## [JALON] v1.0

Deux stories Should traitées en plus de la version finale — plus rien en attente dans le backlog.

### Ajouté
- **EF9** — le relecteur corrige une note déjà rendue tant que la session est ouverte (`PUT /api/relectures/{id}`)
- **EF10** — l'étudiant remplace le lien de son exercice tant qu'aucune relecture n'a été rendue (`PUT /api/exercices/{id}`)

## Enveloppe (étape 3)

Un bug signalé par le client, corrigé avec une issue et un test qui échoue avant le correctif ; un changement de besoin qui a fait évoluer l'analyse, le contrat, la base et les deux écrans concernés.

### Corrigé
- **Condition de course sur le marquage de présence** (RG15) — le contrôle "pas déjà présent" puis l'insertion n'étaient pas atomiques. Sous accès concurrent réel, une `DataIntegrityViolationException` brute pouvait fuiter au lieu d'un `409 DEJA_PRESENT` propre. S'appuie désormais sur la contrainte unique en base comme garde-fou. Reproduit et vérifié par `PresenceConcurrencyTest`

### Changé — double relecture (remplace Q6)
- **RG5, RG6** — chaque exercice est désormais relu par **deux** relecteurs distincts (au lieu d'un seul), tirés au sort parmi les étudiants présents
- **RG17** (nouvelle) — la note retenue est la moyenne des relectures rendues ; **provisoire** tant qu'une seule des deux est rendue, définitive quand la seconde est rendue ou à la clôture de la session
- **EF13** (nouvelle) — l'étudiant voit clairement quand sa note est provisoire
- Migration `V3` : contrainte d'unicité déplacée de `(exercice_id)` à `(exercice_id, relecteur_id)`, nouveau statut `PROVISOIRE`
- `GET /api/exercices` (contrat v1.2) : `noteRetenue`, `provisoire`, et un tableau `relectures[]` remplacent l'ancien couple `note`/`commentaire` unique
- Écran étudiant : note retenue, mention "en attente du second avis", commentaires des deux relecteurs sans jamais leur identité

### Ajouté (correctifs de contrat découverts pendant l'implémentation)
- `GET /api/sessions/active` — le formateur retrouve sa session ouverte sans dépendre du `localStorage` de son navigateur
- `GET /api/relectures?relecteurId=` — un relecteur découvre les relectures qui lui sont assignées

## [JALON] v0.1

Première version fonctionnelle : toutes les stories Must du backlog.

### Ajouté
- Backend Spring Boot : les 10 endpoints Must (EF1-EF8, EF11, EF12), migrations Flyway (schéma conforme au diagramme D2), gestion d'erreurs centralisée, données de démonstration chargées au démarrage
- Frontend React : 3 écrans (formateur, étudiant, relecteur), couche API dédiée, thème clair/sombre (auto + bouton, persistant)
- 6 tests automatisés : RG3 (blocage après 5 codes erronés), RG4/RG8 (auto-relecture, note invalide), intégration `POST /api/sessions`
- `POST /api/sessions/{id}/presences` — présence manuelle par le formateur, oublié à l'analyse
- Bandeau global "serveur injoignable" plutôt qu'un échec silencieux par carte

### Corrigé
- `LazyInitializationException` sur une association JPA accédée hors session Hibernate — services passés en `@Transactional`
- Spécificité CSS du mode sombre : le choix manuel pouvait être silencieusement écrasé par la préférence système

## [JALON] analyse

Aucun code. Cahier des charges (10 sections, EF/RG numérotées), 4 diagrammes Mermaid (D1-D4, D4 en bonus), contrat d'API complété et figé, 12 issues de backlog.
