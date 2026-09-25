# D3 — Séquence : marquer sa présence

Cas nominal et trois cas d'erreur, alignés avec les codes HTTP de `POST /api/presences`
dans `api/contrat.yaml` (400, 409, 410). Le cas des 5 tentatives erronées (RG3) est
représenté séparément car il s'accumule sur plusieurs appels.

```mermaid
sequenceDiagram
    participant E as Étudiant
    participant F as Front
    participant API as PresenceController
    participant S as PresenceService
    participant BD as Base de données

    E->>F: saisit le code
    F->>API: POST /api/presences { code, etudiantId }
    API->>S: enregistrer(code, etudiantId)
    S->>BD: rechercher la session par code

    alt code inconnu
        BD-->>S: aucune session trouvée
        S-->>API: CodeInconnuException
        API-->>F: 400 { code: "CODE_INCONNU" }
        F-->>E: affiche l'erreur
    else code expiré (RG1)
        BD-->>S: session trouvée, expiration_at < maintenant
        S-->>API: CodeExpireException
        API-->>F: 410 { code: "CODE_EXPIRE" }
        F-->>E: affiche l'erreur
    else déjà présent (RG15)
        BD-->>S: présence existante pour (session, etudiant)
        S-->>API: DejaPresentException
        API-->>F: 409 { code: "DEJA_PRESENT" }
        F-->>E: affiche l'erreur
    else cas nominal
        BD-->>S: session valide, aucune présence existante
        S->>BD: insérer présence (source = ETUDIANT)
        BD-->>S: présence créée
        S-->>API: Presence
        API-->>F: 201 { id, sessionId, etudiantId, source }
        F-->>E: confirme la présence
    end
```

## Cas complémentaire — blocage après 5 erreurs (RG3)

```mermaid
sequenceDiagram
    participant E as Étudiant
    participant F as Front
    participant API as PresenceController
    participant S as PresenceService

    loop jusqu'à 5 tentatives échouées
        E->>F: saisit un code invalide
        F->>API: POST /api/presences
        API->>S: enregistrer(code, etudiantId)
        S-->>API: CodeInconnuException (compteur incrémenté)
        API-->>F: 400 { code: "CODE_INCONNU" }
    end

    E->>F: saisit un 6e code
    F->>API: POST /api/presences
    API->>S: enregistrer(code, etudiantId)
    S-->>API: TropDeTentativesException (RG3)
    API-->>F: 429 { code: "TROP_DE_TENTATIVES" }
    F-->>E: affiche « réessaie dans 2 minutes »
```
