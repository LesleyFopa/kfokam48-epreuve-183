package com.kfokam48.presence.service;

import com.kfokam48.presence.domain.Exercice;
import com.kfokam48.presence.domain.Relecture;
import com.kfokam48.presence.domain.StatutExercice;
import com.kfokam48.presence.domain.StatutRelecture;
import com.kfokam48.presence.dto.RelectureRequest;
import com.kfokam48.presence.exception.ApiException;
import com.kfokam48.presence.repository.ExerciceRepository;
import com.kfokam48.presence.repository.RelectureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

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

        exercice.setStatut(StatutExercice.RELU);
        exercice.setMajAt(Instant.now());
        exerciceRepository.save(exercice);
    }
}
