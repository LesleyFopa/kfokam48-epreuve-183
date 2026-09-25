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

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 3 — Enveloppe

**Fait :**

**Bloqué :**

**IA :**

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :**

---

## Étape 4 — Version finale

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 5 — Épreuve Git

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 6 — Soumission

**Fait :**

**Ce que je referais autrement avec une journée de plus :**
