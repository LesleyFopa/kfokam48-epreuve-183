package com.kfokam48.presence.repository;

import com.kfokam48.presence.domain.Relecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    Optional<Relecture> findByExerciceId(Long exerciceId);

    List<Relecture> findByRelecteurId(Long relecteurId);
}
