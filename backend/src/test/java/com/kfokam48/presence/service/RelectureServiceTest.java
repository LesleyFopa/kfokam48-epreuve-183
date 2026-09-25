package com.kfokam48.presence.service;

import com.kfokam48.presence.domain.Etudiant;
import com.kfokam48.presence.domain.Exercice;
import com.kfokam48.presence.domain.Relecture;
import com.kfokam48.presence.domain.StatutRelecture;
import com.kfokam48.presence.dto.RelectureRequest;
import com.kfokam48.presence.exception.ApiException;
import com.kfokam48.presence.repository.ExerciceRepository;
import com.kfokam48.presence.repository.RelectureRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Tests unitaires sur deux règles métier réelles de RelectureService, avec
 * repositories mockés (aucun contexte Spring, aucune base) :
 * RG8 (note entière 0-20) et RG4 (interdiction de se relire soi-même).
 */
@ExtendWith(MockitoExtension.class)
class RelectureServiceTest {

    @Mock
    private RelectureRepository relectureRepository;

    @Mock
    private ExerciceRepository exerciceRepository;



    @Test
    void refuseUneNoteSuperieureA20() {
        RelectureService relectureService = new RelectureService(relectureRepository, exerciceRepository);
        Relecture relecture = new Relecture();
        relecture.setId(1L);
        when(relectureRepository.findById(1L)).thenReturn(Optional.of(relecture));

        assertThatThrownBy(() -> relectureService.noter(1L, new RelectureRequest(25, "trop haut")))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getCode()).isEqualTo("NOTE_INVALIDE"));
    }

    @Test
    void refuseUneNoteNegative() {
        RelectureService relectureService = new RelectureService(relectureRepository, exerciceRepository);
        Relecture relecture = new Relecture();
        relecture.setId(2L);
        when(relectureRepository.findById(2L)).thenReturn(Optional.of(relecture));

        assertThatThrownBy(() -> relectureService.noter(2L, new RelectureRequest(-1, "négatif")))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getCode()).isEqualTo("NOTE_INVALIDE"));
    }

    @Test
    void refuseQuUnEtudiantSeReliseLuiMeme() {
        RelectureService relectureService = new RelectureService(relectureRepository, exerciceRepository);

        Etudiant memeEtudiant = new Etudiant();
        memeEtudiant.setId(7L);

        Exercice exercice = new Exercice();
        exercice.setId(10L);
        exercice.setEtudiant(memeEtudiant);

        Relecture relecture = new Relecture();
        relecture.setId(3L);
        relecture.setExercice(exercice);
        relecture.setRelecteur(memeEtudiant);
        relecture.setStatut(StatutRelecture.ASSIGNEE);

        when(relectureRepository.findById(3L)).thenReturn(Optional.of(relecture));

        assertThatThrownBy(() -> relectureService.noter(3L, new RelectureRequest(15, "je me note moi-même")))
                .isInstanceOf(ApiException.class)
                .satisfies(ex -> assertThat(((ApiException) ex).getCode()).isEqualTo("AUTO_RELECTURE"));
    }

    @Test
    void accepteUneNoteValideDUnAutreEtudiant() {
        RelectureService relectureService = new RelectureService(relectureRepository, exerciceRepository);

        Etudiant deposant = new Etudiant();
        deposant.setId(1L);
        Etudiant relecteur = new Etudiant();
        relecteur.setId(2L);

        Exercice exercice = new Exercice();
        exercice.setId(10L);
        exercice.setEtudiant(deposant);

        Relecture relecture = new Relecture();
        relecture.setId(4L);
        relecture.setExercice(exercice);
        relecture.setRelecteur(relecteur);
        relecture.setStatut(StatutRelecture.ASSIGNEE);

        when(relectureRepository.findById(4L)).thenReturn(Optional.of(relecture));
        when(relectureRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(exerciceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        relectureService.noter(4L, new RelectureRequest(17, "Très bien."));

        assertThat(relecture.getStatut()).isEqualTo(StatutRelecture.RENDUE);
        assertThat(relecture.getNote()).isEqualTo(17);
    }
}
