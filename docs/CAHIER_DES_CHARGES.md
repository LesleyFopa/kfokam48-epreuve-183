# Cahier des charges : Suivi de présence, dépôts et relectures KFOKAM48

Auteur : KF48183  · Frontend choisi : React, parce que la maquette ne compte que 3 écrans simples et que l'écosystème React réduit le risque de blocage sur un point technique dans le temps imparti.

## 1. Contexte et objectif

La direction de la formation KFOKAM48 organise des sessions de cours pendant lesquelles les formateurs doivent : pointer la présence des étudiants, collecter le lien d'un exercice pratique par étudiant, organiser une relecture par les pairs notée, puis consulter une vue d'ensemble par étudiant. Ce suivi est aujourd'hui manuel (feuille de présence, dépôt informel, relecture non tracée), ce qui empêche le formateur d'avoir une vue fiable et à jour. L'application numérise ce cycle complet : ouverture de session avec code de présence, pointage étudiant, dépôt d'exercice, relecture par les pairs assignée automatiquement par le système, et tableau de bord consolidé pour le formateur.

## 2. Acteurs et rôles


| Acteur    | Ce qu'il peut faire                                                                                                                                                                                                 |
| --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Formateur | Ouvrir une session, clôturer une session, ajouter une présence manuellement, consulter le tableau de bord par étudiant                                                                                           |
| Étudiant | Se choisir dans une liste (pas de mot de passe), marquer sa présence, déposer ou remplacer le lien de son exercice, consulter la note et le commentaire reçus sur son exercice                                   |
| Relecteur | Rôle temporaire porté par un étudiant présent à la session, assigné automatiquement par le système (deux relecteurs distincts par exercice depuis l'étape 3) ; note et commente l'exercice qui lui est assigné, peut corriger sa note tant que la session est ouverte |
| Système  | Génère et fait expirer le code de présence, tire au sort les deux relecteurs parmi les étudiants présents, calcule la note retenue (moyenne des deux, provisoire tant qu'un seul a rendu) et la moyenne affichée au formateur |

## 3. Périmètre

**Inclus :**

- Ouverture et clôture de session, génération et validation du code de présence
- Marquage de présence par l'étudiant et ajout manuel par le formateur
- Dépôt et remplacement du lien d'exercice
- Assignation aléatoire de deux relecteurs distincts par exercice, note retenue = moyenne des deux (provisoire tant qu'un seul a rendu), correction avant clôture
- Tableau de bord formateur par étudiant
- Gestion des erreurs conforme au contrat d'API (`api/contrat.yaml`)

**Exclu :**

- Authentification par mot de passe ou compte utilisateur (Q1)
- Gestion administrative des promotions et des étudiants (création, import, édition) — on suppose une liste déjà existante
- Notifications (email, push, SMS)
- Export du tableau de bord (PDF, Excel, CSV)
- Gestion de plusieurs formateurs avec droits différenciés, rôles d'administration
- Soin apporté au rendu visuel : non noté, aucun point pour le CSS

## 4. Exigences fonctionnelles


| Réf | Exigence                                                            | Critère d'acceptation                                                                                                                                                             | Priorité |
| ---- | ------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------- |
| EF1  | Le formateur ouvre une session et obtient un code de présence      | Quand le formateur crée une session (titre + promotion), il reçoit immédiatement un code, une date d'ouverture et une date d'expiration à +15 min                              | Must      |
| EF2  | L'étudiant marque sa présence avec un code                        | Quand un étudiant saisit un code valide et non expiré, sa présence apparaît dans le tableau du formateur avec la source`ETUDIANT`                                              | Must      |
| EF3  | L'étudiant dépose le lien de son exercice                         | Quand un étudiant dépose un lien pour une session où il est présent, l'exercice apparaît au statut « déposé »                                                             | Must      |
| EF4  | Le système assigne deux relecteurs distincts à un exercice déposé *(étape 3)* | Après dépôt, deux relecteurs distincts sont tirés au sort parmi les étudiants présents à la session, tous deux différents du déposant et différents l'un de l'autre               | Must      |
| EF5  | Chaque relecteur note et commente l'exercice qui lui est assigné                   | Quand un relecteur envoie une note entière (0–20) et un commentaire, sa relecture passe à « rendue » ; l'exercice passe à « provisoire » si un seul relecteur a rendu, à « relu » quand les deux ont rendu | Must      |
| EF6  | Le formateur consulte un tableau de bord par étudiant              | Le tableau affiche, par étudiant : présence par session, nombre d'exercices déposés, moyenne des notes reçues, relectures encore dues                                         | Must      |
| EF7  | Le formateur ajoute une présence manuellement                      | Quand le formateur ajoute une présence pour un étudiant absent, elle apparaît dans le tableau avec la source`FORMATEUR`                                                         | Must      |
| EF8  | Le formateur clôture une session                                   | Quand le formateur clôture une session, plus aucune présence, aucun dépôt ni aucune correction de note n'est accepté sur cette session                                        | Must      |
| EF9  | Le relecteur corrige une note déjà envoyée                       | Tant que la session n'est pas clôturée, le relecteur peut renvoyer une note et un commentaire qui remplacent les précédents                                                    | Should    |
| EF10 | L'étudiant remplace le lien de son exercice                        | Tant qu'aucune relecture n'a été commencée sur son exercice, l'étudiant peut soumettre un nouveau lien qui remplace l'ancien                                                   | Should    |
| EF11 | L'étudiant relu consulte sa note et les commentaires reçus         | L'étudiant voit la note retenue et les commentaires de chaque relecteur ayant rendu, sans jamais voir leur identité                                                              | Must      |
| EF12 | L'étudiant s'identifie sans mot de passe                           | L'étudiant sélectionne son nom dans la liste des étudiants de sa promotion avant de marquer sa présence ou déposer un exercice                                                | Must      |
| EF13 | L'étudiant voit clairement quand sa note est provisoire *(étape 3)* | Tant qu'un seul des deux relecteurs a rendu sa relecture, la note affichée est marquée « provisoire » ; la mention disparaît quand le second a rendu ou à la clôture de la session | Must      |

## 5. Exigences non fonctionnelles


| Réf | Exigence                                                                                                        | Comment on la vérifie                                                                                           |
| ---- | --------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| ENF1 | Volumétrie : une promotion compte au plus 60 étudiants, une dizaine de sessions actives en parallèle au plus | Jeux de données de démo dimensionnés en conséquence, pas de pagination nécessaire côté tableau            |
| ENF2 | Usage mobile pour l'écran étudiant                                                                            | Écran étudiant testé et lisible à 375 px de large (téléphone), en usage tactile                            |
| ENF3 | Temps de réponse perçu court sur les actions critiques (marquer présence, déposer exercice)                 | Réponse API sous 300 ms en conditions de démo (base locale, données de démo)                                 |
| ENF4 | Aucune donnée sensible exposée                                                                                | Pas de mot de passe à protéger (Q1) ; l'identité du relecteur n'est jamais renvoyée à l'étudiant relu (Q8) |
| ENF5 | Disponibilité simple                                                                                           | Usage interne en présentiel, pas de contrainte de haute disponibilité ; redémarrage du service acceptable     |

## 6. Règles de gestion


| Réf | Règle                                                                                                                                                                     | Source                                              |
| ---- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------- |
| RG1  | Le code de présence expire 15 minutes après l'ouverture de la session                                                                                                    | Q2                                                  |
| RG2  | Aucune présence ne peut être marquée après expiration du code ou clôture de la session                                                                                | Q2, Q3                                              |
| RG3  | Après 5 erreurs de code consécutives par un même étudiant, blocage de 2 minutes avant un nouvel essai                                                                  | Q4                                                  |
| RG4  | Un étudiant ne peut jamais relire son propre exercice                                                                                                                     | Q5                                                  |
| RG5  | Chaque exercice est relu par exactement deux relecteurs distincts *(étape 3, remplace Q6 — voir §7)*                                                                      | Enveloppe étape 3                                   |
| RG6  | Les deux relecteurs sont tirés au sort par le système parmi les étudiants présents à la session, hors le déposant et hors l'un l'autre                                | Q7, Enveloppe étape 3                               |
| RG7  | L'étudiant relu voit la note et le commentaire, jamais l'identité du relecteur                                                                                           | Q8                                                  |
| RG8  | La note est un entier compris entre 0 et 20 inclus                                                                                                                         | Q9                                                  |
| RG9  | Une note est modifiable par son relecteur tant que la session n'est pas clôturée ; elle devient définitive à la clôture                                               | Q10 / Q15 — tranché, voir §7                     |
| RG10 | Un exercice sans relecture rendue reste au statut « en attente » et doit être visible comme tel dans le tableau du formateur                                            | Q11                                                 |
| RG11 | Le dépôt d'un exercice est possible jusqu'à la clôture de la session par le formateur, même après la fin programmée de la session                                   | Q12                                                 |
| RG12 | Le lien d'un exercice peut être remplacé tant qu'aucune relecture n'a été commencée sur cet exercice                                                                  | Q13                                                 |
| RG13 | Une présence ajoutée manuellement par le formateur porte la source`FORMATEUR` ; une présence saisie par l'étudiant porte la source `ETUDIANT`                          | Q14                                                 |
| RG14 | La clôture d'une session est une action explicite et volontaire du formateur, irréversible, qui verrouille présences, dépôts et corrections de note sur cette session | Trou comblé, voir §7                              |
| RG15 | Un étudiant ne peut marquer sa présence qu'une seule fois par session                                                                                                    | Déduit du contrat (`409 déjà présent`)          |
| RG16 | Un étudiant ne peut déposer qu'un seul exercice par session                                                                                                              | Déduit du contrat (`409 exercice déjà déposé`) |
| RG17 | La note retenue pour un exercice est la moyenne arithmétique des notes rendues par ses relecteurs ; provisoire tant qu'un seul des deux a rendu, définitive quand le second rend la sienne ou à la clôture de la session (RG14) | Enveloppe étape 3 |

## 7. Zones d'ombre, hypothèses et contradictions


| Point                                               | Réponse client (Qx) ou hypothèse                                                                                                                                  | Décision retenue                                                                                                                                                                                                                                                                          | Pourquoi                                                                                                                                                                                                                                                                                                                                   |
| --------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Correction de note après envoi                     | Q10 dit que le relecteur peut corriger sa note tant que la session n'est pas clôturée ; Q15 dit que la note est définitive dès l'envoi — contradiction directe | On retient Q10 : la note reste modifiable tant que la session est ouverte, et devient définitive à la clôture (RG9)                                                                                                                                                                     | Q15 devient vraie à l'échelle de la session entière, au moment de la clôture, plutôt qu'à l'échelle de chaque relecture individuelle. Le formateur garde le dernier mot en clôturant, et le relecteur garde un droit à l'erreur avant ça — aucune des deux réponses n'est ignorée, chacune s'applique à un moment différent |
| Mécanisme de clôture de session absent du contrat | Aucune`Qx` ne décrit comment se clôture une session, alors que Q10 et Q12 s'y réfèrent explicitement                                                            | Ajout d'un endpoint`PATCH /api/sessions/{id}/cloturer` (RG14, EF8), action explicite et volontaire du formateur                                                                                                                                                                            | Q12 dit « jusqu'à ce que je clôture la session », ce qui sous-entend une action du formateur et non un simple timeout ; sans ce mécanisme, RG9 et RG11 seraient inapplicables                                                                                                                                                         |
| Moment du tirage au sort du relecteur               | Aucune`Qx` ne précise quand a lieu l'assignation                                                                                                                   | Le tirage a lieu immédiatement après le dépôt de l'exercice, parmi les étudiants déjà présents à cet instant ; si aucun étudiant éligible n'est présent, l'exercice reste « en attente d'assignation » jusqu'à ce qu'un candidat devienne éligible ou jusqu'à la clôture | Cohérent avec Q7 (« présents à cette session ») ; évite d'introduire un déclencheur manuel que le client n'a pas demandé                                                                                                                                                                                                           |
| Identification étudiant sans mot de passe          | Q1 dit que l'étudiant « choisit son nom dans une liste »                                                                                                         | La liste des étudiants provient de la promotion (`GET /api/promotions/{id}/etudiants`) ; le serveur vérifie que l'`etudiantId` transmis existe bien dans la promotion pour chaque action                                                                                                 | Q1 exclut le mot de passe mais n'exclut pas une vérification minimale d'intégrité ; sans elle, n'importe qui pourrait usurper un`etudiantId` au hasard                                                                                                                                                                                  |
| Un seul relecteur (Q6) vs deux relecteurs (étape 3) | Q6 répondait « un seul » ; l'enveloppe de l'étape 3 revient dessus explicitement : « un seul relecteur ça ne marche pas… chaque exercice est relu par deux pairs différents » | On applique l'instruction la plus récente : RG5/RG6 mises à jour, nouvelle RG17 (moyenne des deux, provisoire si un seul a rendu). Q6 devient obsolète et n'est plus la source de vérité | Le client a testé la v0.1 et change d'avis en connaissance de cause — une instruction explicite et datée prime sur une réponse antérieure, surtout quand le client la justifie lui-même (« quand il ne rend rien, l'étudiant n'a aucune note ») |
| Périmètre à réduire pour absorber ce Must tardif    | L'enveloppe impose : « ce changement est un Must qui arrive tard, donc quelque chose doit sortir du périmètre »                                                  | EF9 (correction de note par le relecteur) et EF10 (remplacement du lien d'exercice) restent **Should**, non implémentées pour cette version — aucun changement par rapport à v0.1, elles ne redescendent pas, elles ne montent simplement pas | Ce sont les deux seules stories Should restantes du backlog ; les garder de côté permet de traiter la double relecture (analyse + migration + backend + frontend) sans la bâcler, plutôt que de livrer les quatre à moitié |

## 8. Contraintes techniques

Imposées par le sujet, rappelées ici pour référence :

- **Backend** : Java 17+, Maven, wrapper `mvnw` commité ; contrat `api/contrat.yaml` respecté à la lettre ; séparation contrôleur / service / repository, DTO uniquement en sortie JSON ; validation des entrées et gestion centralisée des erreurs (`@RestControllerAdvice`) ; schéma versionné par Flyway ou Liquibase, `ddl-auto=update` interdit hors tests ; deux tests significatifs (un unitaire métier, un d'intégration sur un endpoint)
- **Frontend** : React ; trois écrans (formateur, étudiant, relecteur) ; appels API dans une couche dédiée, pas de `fetch` dispersé ; états de chargement et d'erreur gérés ; aucune règle métier dupliquée côté client (la moyenne vient de l'API)
- **Démarrage** : `docker compose up` ou trois commandes maximum documentées et testées depuis un clone vierge, avec données de démonstration chargées au démarrage

## 9. Livrables

- `docs/CAHIER_DES_CHARGES.md` (ce document) et `docs/JOURNAL.md` tenu au fil de l'eau
- `docs/diagrammes/` : D1 cas d'utilisation, D2 classes/modèle de données, D3 séquence « marquer sa présence », D4 (bonus) états-transitions d'un exercice
- Backlog complet en issues GitHub, priorisé Must/Should/Could, chacune renvoyant à une `EFx`/`RGx`
- `api/contrat.yaml` complété et figé avant le premier commit de code
- `/backend` Spring Boot fonctionnel avec migrations versionnées et tests
- `/frontend` React avec les trois écrans
- `README.md` d'installation testé depuis un clone vierge, `CHANGELOG.md` cohérent avec l'historique Git
- Trois commits `[JALON]` (`analyse`, `v0.1`, `v1.0`) dans l'ordre, dans l'historique du dépôt

## 10. Démarche prévue

1. **Analyse** (cette phase) : trancher les zones d'ombre, rédiger le cahier des charges, les diagrammes et le backlog, compléter le contrat d'API, poser `[JALON] analyse`
2. **v0.1** : construire les stories Must uniquement, une branche et une PR par issue, issues fermées par les commits, tests au fur et à mesure, `[JALON] v0.1`
3. **Enveloppe** : ouvrir une issue avant de coder le correctif et l'évolution demandés, versionner la migration correspondante, mettre à jour contrat et documents d'analyse dans un commit dédié
4. **v1.0** : compléter les stories Should/Could restantes selon le temps disponible, rédiger `CHANGELOG.md` et `README.md`, `[JALON] v1.0`
5. **Soumission** : vérification du dépôt en navigation privée, relevé du hash, dépôt de `SOUMISSION.md` sur la plateforme

**Definition of Done** : une issue est terminée quand le code correspondant est mergé via une PR liée à l'issue, que les tests associés (s'il y en a) passent, que la documentation ou les diagrammes concernés sont à jour si l'issue les impacte, et que l'issue est fermée par ce merge.
