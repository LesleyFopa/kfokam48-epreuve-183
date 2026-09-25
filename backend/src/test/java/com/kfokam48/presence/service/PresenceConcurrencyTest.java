package com.kfokam48.presence.service;

import com.kfokam48.presence.domain.Etudiant;
import com.kfokam48.presence.domain.Promotion;
import com.kfokam48.presence.domain.Session;
import com.kfokam48.presence.domain.StatutSession;
import com.kfokam48.presence.dto.PresenceRequest;
import com.kfokam48.presence.exception.ApiException;
import com.kfokam48.presence.repository.EtudiantRepository;
import com.kfokam48.presence.repository.PresenceRepository;
import com.kfokam48.presence.repository.PromotionRepository;
import com.kfokam48.presence.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reproduit le bug remonté par le client (issue #26, étape 3).
 *
 * Le client décrit deux étudiants différents dont l'un "disparaît" — un
 * test à deux threads sur deux étudiants distincts (voir historique de ce
 * fichier) ne reproduit rien : le contrôle d'unicité est scopé par étudiant,
 * deux étudiants différents n'entrent jamais en conflit entre eux. La
 * situation réellement non atomique dans {@link PresenceService#marquerAvecCode}
 * est la vérification "pas déjà présent" suivie de l'insertion, pour UN MÊME
 * étudiant qui soumet deux fois quasi simultanément (double-clic, requête
 * relancée sur un réseau lent) — un scénario qu'un client non technique décrit
 * facilement comme "un des deux n'est pas passé".
 */
@SpringBootTest
class PresenceConcurrencyTest {

    @Autowired
    private PresenceService presenceService;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    @Test
    void deuxSoumissionsSimultaneesDuMemeEtudiantNeDoiventJamaisLeverUneErreurNonGeree() throws Exception {
        Promotion promotion = new Promotion();
        promotion.setNom("Promotion test concurrence");
        promotion = promotionRepository.save(promotion);

        Etudiant etudiant = new Etudiant();
        etudiant.setNom("Étudiant concurrence");
        etudiant.setPromotion(promotion);
        etudiant = etudiantRepository.save(etudiant);

        Session session = new Session();
        session.setTitre("Session test concurrence");
        session.setPromotion(promotion);
        session.setCode("CONC2");
        session.setOuvertureAt(Instant.now());
        session.setExpirationAt(Instant.now().plusSeconds(900));
        session.setStatut(StatutSession.OUVERTE);
        session = sessionRepository.save(session);

        Long sessionId = session.getId();
        Long etudiantId = etudiant.getId();
        String code = session.getCode();

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch pret = new CountDownLatch(2);
        CountDownLatch top = new CountDownLatch(1);

        Callable<Object> soumission = () -> {
            pret.countDown();
            top.await();
            try {
                return presenceService.marquerAvecCode(new PresenceRequest(code, etudiantId));
            } catch (ApiException attendue) {
                // Un 409 DEJA_PRESENT propre est le seul échec acceptable.
                return attendue;
            }
        };

        Future<Object> resultat1 = pool.submit(soumission);
        Future<Object> resultat2 = pool.submit(soumission);

        pret.await();
        top.countDown();

        // Les deux appels doivent se terminer par un résultat métier propre
        // (succès ou ApiException DEJA_PRESENT) — jamais par une exception
        // technique non gérée (ex. violation de contrainte SQL brute).
        assertResultatPropre(resultat1);
        assertResultatPropre(resultat2);
        pool.shutdown();

        List<?> presences = presenceRepository.findBySessionId(sessionId);
        assertThat(presences).hasSize(1);
    }

    private void assertResultatPropre(Future<Object> resultat) throws InterruptedException {
        try {
            resultat.get();
        } catch (ExecutionException e) {
            throw new AssertionError(
                    "Une soumission concurrente a levé une exception technique non gérée au lieu d'un 409 DEJA_PRESENT propre : "
                            + e.getCause(), e.getCause());
        }
    }
}
