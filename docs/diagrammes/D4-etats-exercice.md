# D4 — États-transitions du cycle de vie d'un exercice (bonus)

```mermaid
stateDiagram-v2
    [*] --> Depose : dépôt du lien (EF3)

    Depose --> Depose : remplacement du lien\ntant qu'aucune relecture commencée (RG12)
    Depose --> EnAttenteDeRelecture : relecteur assigné\nau hasard parmi les présents (RG6)

    EnAttenteDeRelecture --> Relu : le relecteur envoie\nnote + commentaire (EF5)

    Relu --> Relu : le relecteur corrige\ntant que la session est ouverte (RG9)

    Depose --> [*] : session clôturée,\naucun relecteur trouvé\n(reste visible \"en attente\" — RG10)
    EnAttenteDeRelecture --> [*] : session clôturée,\nrelecture jamais rendue\n(reste \"en attente\" — RG11)
    Relu --> [*] : session clôturée,\nnote définitive (RG9)
```

> À la clôture de la session (RG14), toute transition sortante de `Relu` est
> bloquée : la note devient définitive. Un exercice resté en `EnAttenteDeRelecture`
> à la clôture reste affiché comme « en attente » dans le tableau du formateur (Q11).
