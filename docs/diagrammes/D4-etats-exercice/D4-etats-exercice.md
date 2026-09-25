# D4 — États-transitions du cycle de vie d'un exercice (bonus)

> **Mis à jour à l'étape 3** : ajout de l'état `Provisoire` entre l'attente et le
> relu, conséquence du passage à deux relecteurs distincts (RG5, RG17).

```mermaid
stateDiagram-v2
    [*] --> Depose : dépôt du lien (EF3)

    Depose --> Depose : remplacement du lien\ntant qu'aucune relecture commencée (RG12)
    Depose --> EnAttenteDeRelecture : deux relecteurs assignés\nau hasard parmi les présents (RG5, RG6)

    EnAttenteDeRelecture --> Provisoire : un premier relecteur envoie\nnote + commentaire (EF5)
    Provisoire --> Relu : le second relecteur envoie\nnote + commentaire (EF5, RG17)

    Provisoire --> Provisoire : un relecteur corrige\ntant que la session est ouverte (RG9)
    Relu --> Relu : un relecteur corrige\ntant que la session est ouverte (RG9)

    Depose --> [*] : session clôturée,\naucun relecteur trouvé\n(reste visible \"en attente\" — RG10)
    EnAttenteDeRelecture --> [*] : session clôturée,\naucune relecture rendue\n(reste \"en attente\" — RG11)
    Provisoire --> [*] : session clôturée,\nnote provisoire devient définitive (RG17)
    Relu --> [*] : session clôturée,\nnote définitive (RG9)
```

> À la clôture de la session (RG14), toute transition sortante de `Provisoire` ou
> `Relu` est bloquée : la note (moyenne des relectures rendues, RG17) devient
> définitive telle quelle. Un exercice resté en `EnAttenteDeRelecture` à la clôture
> reste affiché comme « en attente » dans le tableau du formateur (Q11).
