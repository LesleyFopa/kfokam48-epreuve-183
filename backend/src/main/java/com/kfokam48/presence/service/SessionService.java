package com.kfokam48.presence.service;

import com.kfokam48.presence.domain.Promotion;
import com.kfokam48.presence.domain.Session;
import com.kfokam48.presence.domain.StatutSession;
import com.kfokam48.presence.dto.SessionCreateRequest;
import com.kfokam48.presence.dto.SessionResponse;
import com.kfokam48.presence.exception.ApiException;
import com.kfokam48.presence.repository.PromotionRepository;
import com.kfokam48.presence.repository.SessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Service
public class SessionService {

    private static final String ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int LONGUEUR_CODE = 4;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SessionRepository sessionRepository;
    private final PromotionRepository promotionRepository;
    private final int dureeValiditeMinutes;

    public SessionService(SessionRepository sessionRepository,
                           PromotionRepository promotionRepository,
                           @Value("${kfokam48.presence.duree-validite-code-minutes:15}") int dureeValiditeMinutes) {
        this.sessionRepository = sessionRepository;
        this.promotionRepository = promotionRepository;
        this.dureeValiditeMinutes = dureeValiditeMinutes;
    }

    /** EF1, RG1 : le formateur ouvre une session et obtient un code de présence valable 15 minutes. */
    public SessionResponse ouvrir(SessionCreateRequest requete) {
        Promotion promotion = promotionRepository.findById(requete.promotionId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "PROMOTION_INCONNUE",
                        "Aucune promotion avec cet identifiant."));

        Session session = new Session();
        session.setTitre(requete.titre());
        session.setPromotion(promotion);
        session.setCode(genererCodeUnique());
        Instant maintenant = Instant.now();
        session.setOuvertureAt(maintenant);
        session.setExpirationAt(maintenant.plus(Duration.ofMinutes(dureeValiditeMinutes)));
        session.setStatut(StatutSession.OUVERTE);

        session = sessionRepository.save(session);
        return new SessionResponse(session.getId(), session.getCode(), session.getOuvertureAt(), session.getExpirationAt());
    }

    private String genererCodeUnique() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(LONGUEUR_CODE);
            for (int i = 0; i < LONGUEUR_CODE; i++) {
                sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
            }
            code = sb.toString();
        } while (sessionRepository.findByCode(code).isPresent());
        return code;
    }
}
