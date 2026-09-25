package com.kfokam48.presence.service;

import com.kfokam48.presence.domain.Etudiant;
import com.kfokam48.presence.domain.Exercice;
import com.kfokam48.presence.domain.Presence;
import com.kfokam48.presence.domain.Relecture;
import com.kfokam48.presence.domain.Session;
import com.kfokam48.presence.domain.StatutExercice;
import com.kfokam48.presence.domain.StatutRelecture;
import com.kfokam48.presence.dto.ExerciceCreateResponse;
import com.kfokam48.presence.dto.ExerciceRequest;
import com.kfokam48.presence.dto.ExerciceVueEtudiant;
import com.kfokam48.presence.dto.RelectureResume;
import com.kfokam48.presence.exception.ApiException;
import com.kfokam48.presence.repository.EtudiantRepository;
import com.kfokam48.presence.repository.ExerciceRepository;
import com.kfokam48.presence.repository.PresenceRepository;
import com.kfokam48.presence.repository.RelectureRepository;
import com.kfokam48.presence.repository.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional
public class ExerciceService {

    private static final Pattern LIEN_VALIDE = Pattern.compile("^https?://\\S+\\.\\S+.*$");
    private static final SecureRandom RANDOM = new SecureRandom();

    /** RG5, étape 3 : chaque exercice est relu par exactement deux relecteurs distincts. */
    private static final int NOMBRE_RELECTEURS = 2;

    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;
    private final SessionRepository sessionRepository;
    private final EtudiantRepository etudiantRepository;
    private final PresenceRepository presenceRepository;

    public ExerciceService(ExerciceRepository exerciceRepository,
                            RelectureRepository relectureRepository,
                            SessionRepository sessionRepository,
                            EtudiantRepository etudiantRepository,
                            PresenceRepository presenceRepository) {
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
        this.sessionRepository = sessionRepository;
        this.etudiantRepository = etudiantRepository;
        this.presenceRepository = presenceRepository;
    }

    /**
     * EF3, RG11, RG16 : dépôt d'un exercice, possible jusqu'à la clôture de la session.
     * EF4, RG4, RG5, RG6 : tente ensuite d'assigner deux relecteurs distincts au hasard
     * parmi les étudiants déjà présents (hypothèse tranchée en §7 du cahier des charges).
     * Si un seul candidat éligible est présent, un seul relecteur est assigné — l'exercice
     * n'attend pas indéfiniment un second relecteur qui n'existe pas encore.
     */
    public ExerciceCreateResponse deposer(ExerciceRequest requete) {
        if (!LIEN_VALIDE.matcher(requete.lien()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "LIEN_INVALIDE", "Le lien de l'exercice n'est pas valide.");
        }

        Session session = sessionRepository.findById(requete.sessionId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "SESSION_INCONNUE", "Session inconnue."));
        if (!session.estOuverte()) {
            throw new ApiException(HttpStatus.CONFLICT, "SESSION_CLOTUREE", "La session est clôturée.");
        }

        Etudiant etudiant = etudiantRepository.findById(requete.etudiantId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "ETUDIANT_INCONNU", "Étudiant inconnu."));

        if (exerciceRepository.findBySessionIdAndEtudiantId(session.getId(), etudiant.getId()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "EXERCICE_DEJA_DEPOSE", "Un exercice a déjà été déposé pour cette session.");
        }

        Exercice exercice = new Exercice();
        exercice.setSession(session);
        exercice.setEtudiant(etudiant);
        exercice.setLien(requete.lien());
        exercice.setStatut(StatutExercice.DEPOSE);
        exercice = exerciceRepository.save(exercice);

        List<Etudiant> relecteurs = tirerRelecteurs(session.getId(), etudiant.getId());
        for (Etudiant relecteur : relecteurs) {
            Relecture relecture = new Relecture();
            relecture.setExercice(exercice);
            relecture.setRelecteur(relecteur);
            relectureRepository.save(relecture);
        }
        if (!relecteurs.isEmpty()) {
            exercice.setStatut(StatutExercice.EN_ATTENTE);
            exercice.setMajAt(Instant.now());
            exercice = exerciceRepository.save(exercice);
        }

        return new ExerciceCreateResponse(exercice.getId(), exercice.getStatut().name());
    }

    /**
     * EF11, EF13, RG7, RG17 : l'étudiant consulte ses exercices, la note retenue
     * (moyenne des relectures rendues, provisoire tant qu'il en manque une) et les
     * commentaires reçus — jamais l'identité d'un relecteur.
     */
    public List<ExerciceVueEtudiant> consulterParEtudiant(Long etudiantId) {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "ETUDIANT_INCONNU", "Étudiant inconnu.");
        }
        return exerciceRepository.findByEtudiantId(etudiantId).stream()
                .map(this::versVueEtudiant)
                .toList();
    }

    private ExerciceVueEtudiant versVueEtudiant(Exercice exercice) {
        List<Relecture> relectures = relectureRepository.findByExerciceId(exercice.getId());

        List<RelectureResume> resumes = relectures.stream()
                .map(r -> new RelectureResume(
                        r.getStatut() == StatutRelecture.RENDUE ? r.getNote() : null,
                        r.getStatut() == StatutRelecture.RENDUE ? r.getCommentaire() : null,
                        r.getStatut().name()))
                .toList();

        List<Integer> notesRendues = relectures.stream()
                .filter(r -> r.getStatut() == StatutRelecture.RENDUE)
                .map(Relecture::getNote)
                .toList();

        Double noteRetenue = notesRendues.isEmpty()
                ? null
                : notesRendues.stream().mapToInt(Integer::intValue).average().orElse(0);
        boolean provisoire = !notesRendues.isEmpty() && notesRendues.size() < relectures.size();

        return new ExerciceVueEtudiant(
                exercice.getId(), exercice.getSession().getId(), exercice.getLien(),
                exercice.getStatut().name(), noteRetenue, provisoire, resumes);
    }

    private List<Etudiant> tirerRelecteurs(Long sessionId, Long deposantId) {
        List<Etudiant> eligibles = new ArrayList<>(presenceRepository.findBySessionId(sessionId).stream()
                .map(Presence::getEtudiant)
                .filter(e -> !e.getId().equals(deposantId))
                .toList());
        Collections.shuffle(eligibles, RANDOM);
        return eligibles.subList(0, Math.min(NOMBRE_RELECTEURS, eligibles.size()));
    }
}
