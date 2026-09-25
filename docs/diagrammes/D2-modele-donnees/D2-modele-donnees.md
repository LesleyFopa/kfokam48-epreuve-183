# D2 — Modèle de données

Ce diagramme doit correspondre exactement aux migrations Flyway/Liquibase du backend
(`/backend/.../db/migration`). Toute divergence entre les deux sera une faute en
Phase 2.

```mermaid
erDiagram
    PROMOTION ||--o{ ETUDIANT : "compte"
    PROMOTION ||--o{ SESSION : "concerne"
    SESSION ||--o{ PRESENCE : "enregistre"
    SESSION ||--o{ EXERCICE : "reçoit"
    ETUDIANT ||--o{ PRESENCE : "marque"
    ETUDIANT ||--o{ EXERCICE : "dépose"
    ETUDIANT ||--o{ RELECTURE : "effectue en tant que relecteur"
    EXERCICE ||--o| RELECTURE : "est relu par"

    PROMOTION {
        bigint id PK
        varchar nom
    }

    ETUDIANT {
        bigint id PK
        varchar nom
        bigint promotion_id FK
    }

    SESSION {
        bigint id PK
        varchar titre
        bigint promotion_id FK
        varchar code UK "unique par session active"
        timestamp ouverture_at
        timestamp expiration_at "ouverture_at + 15 min, RG1"
        varchar statut "OUVERTE | CLOTUREE, RG14"
        timestamp cloture_at "nullable"
    }

    PRESENCE {
        bigint id PK
        bigint session_id FK
        bigint etudiant_id FK
        varchar source "ETUDIANT | FORMATEUR, RG13"
        timestamp cree_at
    }

    EXERCICE {
        bigint id PK
        bigint session_id FK
        bigint etudiant_id FK
        varchar lien
        varchar statut "DEPOSE | EN_ATTENTE | RELU"
        timestamp cree_at
        timestamp maj_at
    }

    RELECTURE {
        bigint id PK
        bigint exercice_id FK "unique, RG5 un seul relecteur"
        bigint relecteur_id FK "references etudiant, RG4 jamais soi-même"
        int note "0 à 20 entier, RG8, nullable tant que non rendue"
        varchar commentaire "nullable tant que non rendue"
        varchar statut "ASSIGNEE | RENDUE"
        timestamp rendue_at "nullable"
    }
```

**Contraintes d'unicité notables (RG15, RG16, RG5) :**
- `PRESENCE (session_id, etudiant_id)` — unique : un étudiant ne marque sa présence qu'une fois par session
- `EXERCICE (session_id, etudiant_id)` — unique : un étudiant ne dépose qu'un exercice par session
- `RELECTURE (exercice_id)` — unique : un seul relecteur par exercice
