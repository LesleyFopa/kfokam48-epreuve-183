# D1 — Cas d'utilisation

Mermaid n'a pas de type de diagramme « cas d'utilisation » natif ; on le représente ici
avec un `flowchart`, acteurs à gauche, cas d'utilisation en forme de stade (arrondis)
regroupés par acteur.

```mermaid
flowchart LR
    Formateur([👤 Formateur])
    Etudiant([👤 Étudiant])
    Relecteur([👤 Relecteur])
    Systeme([⚙️ Système])

    subgraph CAS_FORMATEUR [ ]
        UC1(["Ouvrir une session — EF1"])
        UC2(["Clôturer une session — EF8"])
        UC3(["Ajouter une présence manuelle — EF7"])
        UC4(["Consulter le tableau de bord — EF6"])
    end

    subgraph CAS_ETUDIANT [ ]
        UC5(["S'identifier sans mot de passe — EF12"])
        UC6(["Marquer sa présence — EF2"])
        UC7(["Déposer son exercice — EF3"])
        UC8(["Remplacer le lien de son exercice — EF10"])
        UC9(["Consulter sa note et son commentaire — EF11"])
    end

    subgraph CAS_RELECTEUR [ ]
        UC10(["Noter et commenter un exercice assigné — EF5"])
        UC11(["Corriger une note déjà rendue — EF9"])
    end

    subgraph CAS_SYSTEME [ ]
        UC12(["Générer et faire expirer le code — RG1"])
        UC13(["Assigner deux relecteurs distincts — EF4, RG5, RG6"])
        UC14(["Calculer la moyenne affichée — EF6"])
    end

    Formateur --> UC1
    Formateur --> UC2
    Formateur --> UC3
    Formateur --> UC4

    Etudiant --> UC5
    Etudiant --> UC6
    Etudiant --> UC7
    Etudiant --> UC8
    Etudiant --> UC9

    Relecteur --> UC10
    Relecteur --> UC11

    Systeme --> UC12
    Systeme --> UC13
    Systeme --> UC14

    UC1 -.déclenche.-> UC12
    UC7 -.déclenche.-> UC13
    UC10 -.alimente.-> UC14
```

> **Note** : le Relecteur est un rôle temporaire porté par un Étudiant présent à la
> session (voir `docs/CAHIER_DES_CHARGES.md` §2). Il n'y a pas de compte « relecteur »
> séparé.
