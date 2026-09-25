package com.kfokam48.presence.service;

import com.kfokam48.presence.domain.Exercice;
import com.kfokam48.presence.domain.Relecture;
import com.kfokam48.presence.domain.StatutExercice;
import com.kfokam48.presence.domain.StatutRelecture;
import com.kfokam48.presence.dto.RelectureRequest;
import com.kfokam48.presence.dto.RelectureVueRelecteur;
import com.kfokam48.presence.exception.ApiException;
import com.kfokam48.presence.repository.ExerciceRepository;
import com.kfokam48.presence.repository.RelectureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class RelectureService {

    private final RelectureRepository relectureRepository;
    private final ExerciceRepository exerciceRepository;

    public RelectureService(RelectureRepository relectureRepository, ExerciceRepository exerciceRepository) {
        this.relectureRepository = relectureRepository;
        this.exerciceRepository = exerciceRepository;
    }

    /** EF5, RG4, RG7, RG8 : le relecteur note et commente l'exercice qui lui est assigné. */
    public void noter(Long relectureId, RelectureRequest requete) {
        Relecture relecture = relectureRepository.findById(relectureId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "RELECTURE_INCONNUE", "Relecture inconnue."));

        if (requete.note() == null || requete.note() < 0 || requete.note() > 20) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "NOTE_INVALIDE", "La note doit être un entier compris entre 0 et 20.");
        }

        Exercice exercice = relecture.getExercice();
        if (relecture.getRelecteur().getId().equals(exercice.getEtudiant().getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "AUTO_RELECTURE", "Impossible de relire son propre exercice.");
        }

        if (relecture.getStatut() == StatutRelecture.RENDUE) {
            throw new ApiException(HttpStatus.CONFLICT, "RELECTURE_DEJA_RENDUE", "Cette relecture a déjà été rendue.");
        }

        relecture.setNote(requete.note());
        relecture.setCommentaire(requete.commentaire());
        relecture.setStatut(StatutRelecture.RENDUE);
        relecture.setRendueAt(Instant.now());
        relectureRepository.save(relecture);

        exercice.setStatut(statutApresRelecture(exercice));
        exercice.setMajAt(Instant.now());
        exerciceRepository.save(exercice);
    }

    /** EF9, RG9 : le relecteur corrige une note déjà rendue, tant que la session est ouverte. */
    public void corriger(Long relectureId, RelectureRequest requete) {
        Relecture relecture = relectureRepository.findById(relectureId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "RELECTURE_INCONNUE", "Relecture inconnue."));

        if (requete.note() == null || requete.note() < 0 || requete.note() > 20) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "NOTE_INVALIDE", "La note doit être un entier compris entre 0 et 20.");
        }

        Exercice exercice = relecture.getExercice();
        if (!exercice.getSession().estOuverte()) {
            throw new ApiException(HttpStatus.CONFLICT, "SESSION_CLOTUREE", "La session est clôturée, la note est définitive.");
        }

        relecture.setNote(requete.note());
        relecture.setCommentaire(requete.commentaire());
        relecture.setStatut(StatutRelecture.RENDUE);
        relecture.setRendueAt(Instant.now());
        relectureRepository.save(relecture);

        exercice.setStatut(statutApresRelecture(exercice));
        exercice.setMajAt(Instant.now());
        exerciceRepository.save(exercice);
    }

    /**
     * RG17, étape 3 : RELU quand toutes les relectures assignées à l'exercice
     * sont rendues (une seule s'il n'y en avait qu'une par manque de candidats,
     * deux dans le cas normal) ; PROVISOIRE tant qu'il en manque une.
     */
    private StatutExercice statutApresRelecture(Exercice exercice) {
        List<Relecture> toutes = relectureRepository.findByExerciceId(exercice.getId());
        long rendues = toutes.stream().filter(r -> r.getStatut() == StatutRelecture.RENDUE).count();
        return rendues >= toutes.size() ? StatutExercice.RELU : StatutExercice.PROVISOIRE;
    }

    /** EF5 : un relecteur découvre les relectures qui lui sont assignées. */
    public List<RelectureVueRelecteur> pourRelecteur(Long relecteurId) {
        return relectureRepository.findByRelecteurId(relecteurId).stream()
                .map(r -> new RelectureVueRelecteur(
                        r.getId(), r.getExercice().getId(), r.getExercice().getLien(), r.getStatut().name()))
                .toList();
    }
}
