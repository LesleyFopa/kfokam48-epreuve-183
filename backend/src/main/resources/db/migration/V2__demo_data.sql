-- Données de démonstration chargées au démarrage (contrainte "Démarrage" du sujet).

INSERT INTO promotion (nom) VALUES ('KF48 - Promotion B2');

INSERT INTO etudiant (nom, promotion_id) VALUES
    ('Aïcha Ndoumbe', 1),
    ('Brice Fotso', 1),
    ('Chloé Mbarga', 1),
    ('David Tchoua', 1),
    ('Emma Biya', 1),
    ('Franck Owona', 1);

INSERT INTO session (titre, promotion_id, code, ouverture_at, expiration_at, statut)
VALUES ('Spring Boot — Contrôleurs REST', 1, '4F82', CURRENT_TIMESTAMP, DATEADD('MINUTE', 15, CURRENT_TIMESTAMP), 'OUVERTE');

INSERT INTO presence (session_id, etudiant_id, source, cree_at) VALUES
    (1, 1, 'ETUDIANT', CURRENT_TIMESTAMP),
    (1, 2, 'ETUDIANT', CURRENT_TIMESTAMP),
    (1, 3, 'ETUDIANT', CURRENT_TIMESTAMP),
    (1, 4, 'FORMATEUR', CURRENT_TIMESTAMP);

INSERT INTO exercice (session_id, etudiant_id, lien, statut, cree_at, maj_at) VALUES
    (1, 1, 'https://github.com/aicha/tp-controleurs-rest', 'EN_ATTENTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 2, 'https://github.com/brice/tp-controleurs-rest', 'RELU', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO relecture (exercice_id, relecteur_id, statut, rendue_at) VALUES
    (1, 3, 'ASSIGNEE', NULL);

INSERT INTO relecture (exercice_id, relecteur_id, note, commentaire, statut, rendue_at) VALUES
    (2, 1, 16, 'Bon découpage des couches, il manque la validation des DTO en entrée.', 'RENDUE', CURRENT_TIMESTAMP);
