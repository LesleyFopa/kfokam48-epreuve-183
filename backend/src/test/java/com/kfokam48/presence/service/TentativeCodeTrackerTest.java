package com.kfokam48.presence.service;

import com.kfokam48.presence.exception.ApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test unitaire sur une règle métier réelle : RG3, "après 5 codes erronés
 * d'un même étudiant, blocage de 2 minutes" (Q4 de CLIENT.md).
 */
class TentativeCodeTrackerTest {

    private final TentativeCodeTracker tracker = new TentativeCodeTracker(5, 2);

    @Test
    void nBloquePasAvantCinqEchecs() {
        Long etudiantId = 42L;

        for (int i = 0; i < 4; i++) {
            tracker.verifierNonBloque(etudiantId);
            tracker.enregistrerEchec(etudiantId);
        }

        assertThatCode(() -> tracker.verifierNonBloque(etudiantId)).doesNotThrowAnyException();
    }

    @Test
    void bloqueApresCinqEchecsConsecutifs() {
        Long etudiantId = 43L;

        for (int i = 0; i < 5; i++) {
            tracker.verifierNonBloque(etudiantId);
            tracker.enregistrerEchec(etudiantId);
        }

        assertThatThrownBy(() -> tracker.verifierNonBloque(etudiantId))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getCode()).isEqualTo("TROP_DE_TENTATIVES"));
    }

    @Test
    void reinitialiserRemetLeCompteurAZero() {
        Long etudiantId = 44L;

        for (int i = 0; i < 4; i++) {
            tracker.enregistrerEchec(etudiantId);
        }
        tracker.reinitialiser(etudiantId);
        tracker.enregistrerEchec(etudiantId);

        assertThatCode(() -> tracker.verifierNonBloque(etudiantId)).doesNotThrowAnyException();
    }
}
