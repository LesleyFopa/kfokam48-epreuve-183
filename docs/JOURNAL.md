# Journal de bord — KF48-183

> Une entrée **par étape**, écrite **au moment où tu la termines**, pas à la fin de la journée.
> Trois lignes suffisent. Un journal rédigé d'un bloc juste avant de soumettre se repère
> immédiatement dans l'historique Git et ne compte pas.

Chaque entrée répond aux trois mêmes questions :

- **Fait** — ce que tu viens de terminer
- **Bloqué** — ce qui t'a coûté du temps, et combien
- **IA** — ce que tu lui as demandé, et **comment tu as vérifié sa réponse**

---

## Étape 1 — Analyse et conception

**Fait :** cahier des charges (12 exigences fonctionnelles EF1-EF12, 16 règles de gestion RG1-RG16), 3 diagrammes Mermaid (D1 cas d'utilisation, D2 modèle de données, D3 séquence présence) + D4 bonus états-transitions, contrat d'API complété (5 opérations imposées + 5 ajoutées : clôture session, correction note, remplacement lien, liste étudiants, consultation exercices), 12 issues créées (10 Must, 2 Should), commit `[JALON] analyse` poussé.

**Bloqué :** temps passé à repérer la contradiction Q10/Q15 (le relecteur peut corriger sa note vs. la note est définitive dès l'envoi) et le trou du contrat (aucun endpoint pour clôturer une session, alors que Q10 et Q12 s'y réfèrent). Tranché en faveur de Q10 : la note reste modifiable tant que la session est ouverte, devient définitive à la clôture — les deux réponses du client s'appliquent alors, chacune à un moment différent. Détail en section 7 du cahier des charges.

**IA :** utilisée pour structurer le cahier des charges, rédiger les diagrammes Mermaid et générer le contenu des 12 issues à partir des EF déjà rédigées. Vérifié chaque diagramme en le faisant correspondre ligne à ligne au contrat d'API (D3) et aux entités prévues (D2) ; vérifié chaque issue en confirmant qu'elle renvoie à une EFx/RGx existante et qu'elle décrit un résultat utilisateur, pas une tâche technique.

---

## Étape 2 — Première version

**Fait :** backend Spring Boot complet (10 endpoints Must, migrations Flyway conformes à D2, gestion d'erreurs centralisée, 6 tests dont 1 unitaire RG3 et 1 intégration), frontend React (3 écrans, couche API dédiée, thème clair/sombre auto + manuel). 9 PR mergées, 10 issues Must fermées. Testé de bout en bout dans le navigateur : session → présence → dépôt → assignation automatique → relecture → tableau à jour → clôture.

**Bloqué :** un `LazyInitializationException` sur `relecture.getExercice().getEtudiant()` hors session Hibernate (~15 min) — corrigé en ajoutant `@Transactional` aux services. Deux trous découverts en cours de route, absents du contrat figé en Phase 1 : aucun moyen pour le formateur d'ajouter une présence sans code (EF7), et aucun moyen pour un relecteur de découvrir l'id de sa relecture assignée. Les deux comblés par un endpoint dédié, en PR séparée, avant de continuer.

**IA :** génération du squelette Spring Boot (dépendances, structure de packages) et des DTO/contrôleurs à partir du contrat déjà figé — vérifié en testant chaque endpoint manuellement (`curl`) contre les codes HTTP et le format d'erreur exacts du contrat avant de committer. Pour le frontend, l'IA a proposé une palette et une mise en page inspirées d'une image de référence que j'ai fournie ; vérifié en testant le parcours complet dans le navigateur (formateur → étudiant → relecteur) et en confirmant que les graphiques du tableau de bord n'affichent que des données réellement renvoyées par l'API, sans moyenne recalculée côté client (F3).

---

## Étape 3 — Enveloppe

**Fait (partie 1, bug) :** issue #26 ouverte avant tout code, décrivant le signalement client et sa reproduction. Écrit `PresenceConcurrencyTest` (deux vrais threads) pour le prouver. Corrigé la condition de course dans `PresenceService` (contrôle "déjà présent" non atomique avec l'insertion) en s'appuyant sur la contrainte unique en base plutôt que sur le contrôle applicatif seul. Test rejoué 5 fois après correction : 5/5. PR séparée mergée, issue fermée par le commit.

**Bloqué :** ~20 min sur la traduction du signalement client. Un premier test avec deux étudiants *différents* (description littérale du client) ne reproduisait rien — le contrôle d'unicité est scopé par étudiant, aucun conflit possible entre deux personnes distinctes. Le vrai scénario non atomique est une double soumission du *même* étudiant ; un client non technique décrit facilement les deux situations de la même façon ("un des deux n'est pas passé").

**IA :** proposé plusieurs hypothèses de cause (pool de connexions, verrouillage H2, état partagé) avant la bonne. Vérifié en écrivant le test AVANT de choisir : le test avec deux étudiants différents passait (pas de bug), celui avec le même étudiant échouait de façon reproductible (5/5) — c'est ce résultat empirique qui a tranché, pas une supposition.

**Fait (partie 2, changement de besoin) :** analyse mise à jour dans un commit dédié (EF4/EF5/EF11 + nouvelle EF13, RG5/RG6 + nouvelle RG17, section 7, D2, D4, D1) avant tout code. Contrat `api/contrat.yaml` v1.2 (GET /api/exercices enrichi). Migration `V3` ajoutée, `V1`/`V2` intactes, vérifiée sur une base déjà remplie. 2 issues (#31, #32) ouvertes avant l'implémentation. Backend : deux relecteurs distincts assignés au dépôt, statut PROVISOIRE/RELU recalculé à chaque relecture rendue, note retenue = moyenne. Frontend : écran étudiant affiche la note retenue, le badge provisoire, les commentaires des deux relecteurs sans jamais leur identité. Testé de bout en bout dans le navigateur (dépôt → 2 relecteurs → 1re relecture provisoire → 2e relecture → moyenne exacte 15 pour 13+17). Suite complète : 11/11 tests, build frontend OK.

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :** EF9 (le relecteur corrige une note déjà envoyée) et EF10 (l'étudiant remplace le lien de son exercice) restent Should, non implémentées. C'étaient déjà les deux seules stories Should en attente depuis v0.1 ; les garder de côté a permis de traiter la double relecture (analyse + migration + backend + frontend) sans la bâcler, plutôt que de livrer les deux évolutions à moitié dans le temps restant.

---

## Étape 4 — Version finale

**Fait :** EF9 (le relecteur corrige une note, PUT /api/relectures/{id}) et EF10 (l'étudiant remplace le lien, PUT /api/exercices/{id}) implémentées backend+frontend, testées de bout en bout — les deux dernières issues du backlog fermées, plus rien en attente. `CHANGELOG.md` créé, organisé par jalon. `README.md` revérifié en clonant réellement le dépôt dans un dossier à part : `./mvnw spring-boot:run` puis `npm install && npm run dev` fonctionnent tels quels, données de démo chargées (10 étudiants, 2 sessions), aucune config supplémentaire nécessaire. `[JALON] v1.0` poussé.

**Bloqué :** rien de notable — les deux endpoints suivaient exactement le contrat déjà écrit en Phase 1, l'essentiel du temps est parti dans la vérification (tester "Corriger" sur la bonne relecture : j'ai d'abord corrigé par erreur la relecture d'un autre exercice en confondant les id, repéré en comparant la réponse à ce qui était attendu).

**IA :** aucune proposition de raccourci pris sans vérification à cette étape — chaque endpoint testé manuellement (curl puis navigateur) avant commit, le clone vierge testé littéralement en clonant le dépôt GitHub dans un répertoire séparé plutôt qu'en supposant que le README était à jour.

---

## Étape 5 — Soumission

**Fait :** dépôt vérifié public et accessible en visiteur anonyme (`curl` sans authentification sur le dépôt et sur le commit final, 200 dans les deux cas). Hash final relevé (`29d0fe1...`, 40 caractères). `SOUMISSION.md` rédigé — section Épreuve Git retirée (supprimée du sujet cette session, confirmé par le prof), reste du fichier rempli.

**Ce que je referais autrement avec une journée de plus :** rien côté produit — le backlog est vide, Must et Should livrés. J'ajouterais une authentification minimale sur les actions formateur (ouvrir/clôturer une session), repérée en cours de route comme absence de contrôle mais hors périmètre du sujet (Q1 ne l'exige que pour les étudiants).
