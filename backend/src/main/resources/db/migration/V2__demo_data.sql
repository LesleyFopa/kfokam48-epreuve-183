-- Données de démonstration chargées au démarrage (contrainte "Démarrage" du sujet).
-- Volontairement variées : deux sessions (une ouverte, une clôturée), des
-- exercices dans les trois statuts possibles, des notes réparties sur toute
-- l'échelle 0-20, pour que le tableau de bord et les graphiques ne soient
-- pas vides ni monotones à la première ouverture.

INSERT INTO promotion (nom) VALUES ('KF48 - Promotion B2');

INSERT INTO etudiant (nom, promotion_id) VALUES
    ('Aïcha Ndoumbe', 1),
    ('Brice Fotso', 1),
    ('Chloé Mbarga', 1),
    ('David Tchoua', 1),
    ('Emma Biya', 1),
    ('Franck Owona', 1),
    ('Grace Talla', 1),
    ('Hervé Nkeng', 1),
    ('Inès Fouda', 1),
    ('Junior Mballa', 1);

-- Session 1 : ouverte, code encore valable au démarrage.
INSERT INTO session (titre, promotion_id, code, ouverture_at, expiration_at, statut)
VALUES ('Spring Boot — Contrôleurs REST', 1, '4F82', CURRENT_TIMESTAMP, DATEADD('MINUTE', 15, CURRENT_TIMESTAMP), 'OUVERTE');

-- Session 2 : clôturée, pour simuler un historique.
INSERT INTO session (titre, promotion_id, code, ouverture_at, expiration_at, statut, cloture_at)
VALUES ('React — Hooks et état', 1, 'R7T3',
        DATEADD('DAY', -2, CURRENT_TIMESTAMP),
        DATEADD('MINUTE', 15, DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
        'CLOTUREE',
        DATEADD('HOUR', 1, DATEADD('DAY', -2, CURRENT_TIMESTAMP)));

-- Présences session 1 (7 présents sur 10, dont un ajout manuel).
INSERT INTO presence (session_id, etudiant_id, source, cree_at) VALUES
    (1, 1, 'ETUDIANT', CURRENT_TIMESTAMP),
    (1, 2, 'ETUDIANT', CURRENT_TIMESTAMP),
    (1, 3, 'ETUDIANT', CURRENT_TIMESTAMP),
    (1, 4, 'FORMATEUR', CURRENT_TIMESTAMP),
    (1, 5, 'ETUDIANT', CURRENT_TIMESTAMP),
    (1, 7, 'ETUDIANT', CURRENT_TIMESTAMP),
    (1, 8, 'ETUDIANT', CURRENT_TIMESTAMP);

-- Présences session 2 (5 présents).
INSERT INTO presence (session_id, etudiant_id, source, cree_at) VALUES
    (2, 1, 'ETUDIANT', DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
    (2, 2, 'ETUDIANT', DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
    (2, 4, 'ETUDIANT', DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
    (2, 6, 'ETUDIANT', DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
    (2, 9, 'ETUDIANT', DATEADD('DAY', -2, CURRENT_TIMESTAMP));

-- Exercices : les trois statuts possibles, sur les deux sessions. Insérés
-- dans cet ordre précis pour que leurs id auto-générés (1 à 7) correspondent
-- aux références utilisées dans les INSERT INTO relecture ci-dessous.
INSERT INTO exercice (session_id, etudiant_id, lien, statut, cree_at, maj_at) VALUES
    (1, 1, 'https://github.com/aicha/tp-controleurs-rest', 'EN_ATTENTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 2, 'https://github.com/brice/tp-controleurs-rest', 'RELU', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 5, 'https://github.com/emma/tp-controleurs-rest', 'DEPOSE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'https://github.com/aicha/tp-hooks', 'RELU', DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
    (2, 4, 'https://github.com/david/tp-hooks', 'RELU', DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
    (2, 9, 'https://github.com/ines/tp-hooks', 'RELU', DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
    (1, 7, 'https://github.com/grace/tp-controleurs-rest', 'EN_ATTENTE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Relectures : certaines en attente, la plupart rendues avec des notes
-- réparties sur les quatre tranches (0-9, 10-13, 14-16, 17-20). L'exercice 3
-- n'a volontairement aucune relecture : il illustre le cas "pas encore
-- assigné".
INSERT INTO relecture (exercice_id, relecteur_id, statut) VALUES
    (1, 3, 'ASSIGNEE'),
    (7, 8, 'ASSIGNEE');

INSERT INTO relecture (exercice_id, relecteur_id, note, commentaire, statut, rendue_at) VALUES
    (2, 1, 16, 'Bon découpage des couches, il manque la validation des DTO en entrée.', 'RENDUE', CURRENT_TIMESTAMP),
    (4, 6, 12, 'Les hooks sont là mais un useEffect part en boucle infinie.', 'RENDUE', DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
    (5, 9, 20, 'Rien à redire, très propre.', 'RENDUE', DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
    (6, 2, 8, 'Le state est mal découpé, à revoir avant de continuer.', 'RENDUE', DATEADD('DAY', -2, CURRENT_TIMESTAMP));
