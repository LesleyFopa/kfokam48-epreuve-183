package com.kfokam48.presence.service;

import com.kfokam48.presence.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RG3 : après 5 codes erronés d'un même étudiant, blocage de 2 minutes.
 * Compteur en mémoire — suffisant pour l'échelle d'une session de cours,
 * pas destiné à survivre un redémarrage du serveur.
 */
@Component
public class TentativeCodeTracker {

    private final int maxTentatives;
    private final Duration dureeBlocage;
    private final Map<Long, Etat> etats = new ConcurrentHashMap<>();

    public TentativeCodeTracker(
            @Value("${kfokam48.presence.max-tentatives-code:5}") int maxTentatives,
            @Value("${kfokam48.presence.duree-blocage-minutes:2}") int dureeBlocageMinutes) {
        this.maxTentatives = maxTentatives;
        this.dureeBlocage = Duration.ofMinutes(dureeBlocageMinutes);
    }

    public void verifierNonBloque(Long etudiantId) {
        Etat etat = etats.get(etudiantId);
        if (etat != null && etat.blockedUntil != null && Instant.now().isBefore(etat.blockedUntil)) {
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "TROP_DE_TENTATIVES",
                    "Trop d'essais, réessaie dans quelques minutes.");
        }
    }

    public void enregistrerEchec(Long etudiantId) {
        Etat etat = etats.computeIfAbsent(etudiantId, k -> new Etat());
        int compte = etat.compteur.incrementAndGet();
        if (compte >= maxTentatives) {
            etat.blockedUntil = Instant.now().plus(dureeBlocage);
            etat.compteur.set(0);
        }
    }

    public void reinitialiser(Long etudiantId) {
        Etat etat = etats.get(etudiantId);
        if (etat != null) {
            etat.compteur.set(0);
        }
    }

    private static final class Etat {
        private final AtomicInteger compteur = new AtomicInteger(0);
        private volatile Instant blockedUntil;
    }
}
