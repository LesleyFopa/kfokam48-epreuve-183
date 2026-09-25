package com.kfokam48.presence.service;

import com.kfokam48.presence.domain.Etudiant;
import com.kfokam48.presence.domain.Exercice;
import com.kfokam48.presence.domain.Relecture;
import com.kfokam48.presence.domain.StatutRelecture;
import com.kfokam48.presence.dto.TableauLigne;
import com.kfokam48.presence.exception.ApiException;
import com.kfokam48.presence.repository.EtudiantRepository;
import com.kfokam48.presence.repository.ExerciceRepository;
import com.kfokam48.presence.repository.PresenceRepository;
import com.kfokam48.presence.repository.PromotionRepository;
import com.kfokam48.presence.repository.RelectureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class TableauService {

    private final PromotionRepository promotionRepository;
    private final EtudiantRepository etudiantRepository;
    private final PresenceRepository presenceRepository;
    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;

    public TableauService(PromotionRepository promotionRepository,
                           EtudiantRepository etudiantRepository,
                           PresenceRepository presenceRepository,
                           ExerciceRepository exerciceRepository,
                           RelectureRepository relectureRepository) {
        this.promotionRepository = promotionRepository;
        this.etudiantRepository = etudiantRepository;
        this.presenceRepository = presenceRepository;
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
    }

    /** EF6, RG10, Q16 : vue formateur, une ligne par étudiant de la promotion. */
    public List<TableauLigne> pourPromotion(Long promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "PROMOTION_INCONNUE", "Aucune promotion avec cet identifiant.");
        }

        List<Etudiant> etudiants = etudiantRepository.findByPromotionId(promotionId);
        return etudiants.stream().map(this::ligne).toList();
    }

    private TableauLigne ligne(Etudiant etudiant) {
        long presences = presenceRepository.findByEtudiantId(etudiant.getId()).size();

        List<Exercice> exercices = exerciceRepository.findByEtudiantId(etudiant.getId());
        long exercicesDeposes = exercices.size();

        // RG17, étape 3 : la note retenue d'un exercice est la moyenne de ses
        // relectures rendues (1 ou 2) ; la moyenne du tableau agrège ces notes
        // retenues, jamais les relectures individuelles directement.
        List<Double> notesRetenues = exercices.stream()
                .map(ex -> {
                    List<Integer> rendues = relectureRepository.findByExerciceId(ex.getId()).stream()
                            .filter(r -> r.getStatut() == StatutRelecture.RENDUE)
                            .map(Relecture::getNote)
                            .toList();
                    return rendues.isEmpty() ? null : rendues.stream().mapToInt(Integer::intValue).average().orElse(0);
                })
                .filter(Objects::nonNull)
                .toList();
        Double moyenne = notesRetenues.isEmpty()
                ? null
                : notesRetenues.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        long relecturesEnAttente = relectureRepository.findByRelecteurId(etudiant.getId()).stream()
                .filter(r -> r.getStatut() != StatutRelecture.RENDUE)
                .count();

        return new TableauLigne(etudiant.getId(), etudiant.getNom(), presences, exercicesDeposes, moyenne, relecturesEnAttente);
    }
}
