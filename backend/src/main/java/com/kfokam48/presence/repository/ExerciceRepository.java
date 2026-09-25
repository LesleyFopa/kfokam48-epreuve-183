package com.kfokam48.presence.repository;

import com.kfokam48.presence.domain.Exercice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    Optional<Exercice> findBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    List<Exercice> findByEtudiantId(Long etudiantId);

    List<Exercice> findByEtudiantIdIn(List<Long> etudiantIds);
}
