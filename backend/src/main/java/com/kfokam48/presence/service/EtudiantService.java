package com.kfokam48.presence.service;

import com.kfokam48.presence.dto.EtudiantDto;
import com.kfokam48.presence.exception.ApiException;
import com.kfokam48.presence.repository.EtudiantRepository;
import com.kfokam48.presence.repository.PromotionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final PromotionRepository promotionRepository;

    public EtudiantService(EtudiantRepository etudiantRepository, PromotionRepository promotionRepository) {
        this.etudiantRepository = etudiantRepository;
        this.promotionRepository = promotionRepository;
    }

    /** EF12 : liste des étudiants d'une promotion, pour identification sans mot de passe (Q1). */
    public List<EtudiantDto> listerParPromotion(Long promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "PROMOTION_INCONNUE", "Aucune promotion avec cet identifiant.");
        }
        return etudiantRepository.findByPromotionId(promotionId).stream()
                .map(e -> new EtudiantDto(e.getId(), e.getNom()))
                .toList();
    }
}
