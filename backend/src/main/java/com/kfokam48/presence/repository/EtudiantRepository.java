package com.kfokam48.presence.repository;

import com.kfokam48.presence.domain.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    List<Etudiant> findByPromotionId(Long promotionId);
}
