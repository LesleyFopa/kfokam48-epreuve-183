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
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;

@Service
@Transactional
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

    /** EF1, EF8 : retrouve la session actuellement ouverte d'une promotion. */
    public SessionResponse sessionActive(Long promotionId) {
        Session session = sessionRepository
                .findFirstByPromotionIdAndStatutOrderByOuvertureAtDesc(promotionId, StatutSession.OUVERTE)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "AUCUNE_SESSION_OUVERTE",
                        "Aucune session ouverte pour cette promotion."));
        return new SessionResponse(session.getId(), session.getCode(), session.getOuvertureAt(), session.getExpirationAt());
    }

    /** EF8, RG14 : clôture explicite et irréversible d'une session par le formateur. */
    public void cloturer(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "SESSION_INCONNUE", "Session inconnue."));
        if (!session.estOuverte()) {
            throw new ApiException(HttpStatus.CONFLICT, "SESSION_DEJA_CLOTUREE", "Cette session est déjà clôturée.");
        }
        session.setStatut(StatutSession.CLOTUREE);
        session.setClotureAt(Instant.now());
        sessionRepository.save(session);
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
