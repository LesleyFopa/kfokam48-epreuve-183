package com.kfokam48.presence.repository;

import com.kfokam48.presence.domain.Relecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    /** Étape 3 : 0 à 2 relectures par exercice (RG5), plus une seule comme avant v0.1. */
    List<Relecture> findByExerciceId(Long exerciceId);

    List<Relecture> findByRelecteurId(Long relecteurId);
}
