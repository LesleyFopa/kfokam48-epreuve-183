package com.kfokam48.presence.service;

import com.kfokam48.presence.domain.Etudiant;
import com.kfokam48.presence.domain.Exercice;
import com.kfokam48.presence.domain.Presence;
import com.kfokam48.presence.domain.Relecture;
import com.kfokam48.presence.domain.Session;
import com.kfokam48.presence.domain.StatutExercice;
import com.kfokam48.presence.dto.ExerciceCreateResponse;
import com.kfokam48.presence.dto.ExerciceRequest;
import com.kfokam48.presence.exception.ApiException;
import com.kfokam48.presence.repository.EtudiantRepository;
import com.kfokam48.presence.repository.ExerciceRepository;
import com.kfokam48.presence.repository.PresenceRepository;
import com.kfokam48.presence.repository.RelectureRepository;
import com.kfokam48.presence.repository.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ExerciceService {

    private static final Pattern LIEN_VALIDE = Pattern.compile("^https?://\\S+\\.\\S+.*$");
    private static final SecureRandom RANDOM = new SecureRandom();

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
     * EF4, RG4, RG5, RG6 : tente ensuite d'assigner un relecteur au hasard parmi les
     * étudiants déjà présents (hypothèse tranchée en §7 du cahier des charges).
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

        Etudiant relecteur = tirerRelecteur(session.getId(), etudiant.getId());
        if (relecteur != null) {
            Relecture relecture = new Relecture();
            relecture.setExercice(exercice);
            relecture.setRelecteur(relecteur);
            relectureRepository.save(relecture);

            exercice.setStatut(StatutExercice.EN_ATTENTE);
            exercice.setMajAt(Instant.now());
            exercice = exerciceRepository.save(exercice);
        }

        return new ExerciceCreateResponse(exercice.getId(), exercice.getStatut().name());
    }

    private Etudiant tirerRelecteur(Long sessionId, Long deposantId) {
        List<Etudiant> eligibles = presenceRepository.findBySessionId(sessionId).stream()
                .map(Presence::getEtudiant)
                .filter(e -> !e.getId().equals(deposantId))
                .toList();
        if (eligibles.isEmpty()) {
            return null;
        }
        return eligibles.get(RANDOM.nextInt(eligibles.size()));
    }
}
