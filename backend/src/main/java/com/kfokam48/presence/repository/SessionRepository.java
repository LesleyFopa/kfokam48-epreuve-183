package com.kfokam48.presence.repository;

import com.kfokam48.presence.domain.Session;
import com.kfokam48.presence.domain.StatutSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByCode(String code);

    Optional<Session> findFirstByPromotionIdAndStatutOrderByOuvertureAtDesc(Long promotionId, StatutSession statut);
}
