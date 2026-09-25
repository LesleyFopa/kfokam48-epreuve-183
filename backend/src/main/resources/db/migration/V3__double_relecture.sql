-- Étape 3, changement de besoin : chaque exercice est désormais relu par
-- deux relecteurs distincts (RG5) au lieu d'un seul (Q6, obsolète — voir
-- docs/CAHIER_DES_CHARGES.md §7). La note retenue est la moyenne des
-- relectures rendues, provisoire tant qu'une seule des deux est rendue
-- (RG17) — calculée à la lecture, aucune colonne dédiée nécessaire.
--
-- Ne modifie jamais V1/V2. Compatible avec les données déjà en base :
-- chaque exercice existant a au plus une ligne relecture, ce qui reste
-- valide sous la nouvelle contrainte (au plus deux relecteurs distincts).

ALTER TABLE relecture DROP CONSTRAINT uk_relecture_exercice;

ALTER TABLE relecture
    ADD CONSTRAINT uk_relecture_exercice_relecteur UNIQUE (exercice_id, relecteur_id);

ALTER TABLE exercice DROP CONSTRAINT chk_exercice_statut;

ALTER TABLE exercice
    ADD CONSTRAINT chk_exercice_statut CHECK (statut IN ('DEPOSE', 'EN_ATTENTE', 'PROVISOIRE', 'RELU'));
