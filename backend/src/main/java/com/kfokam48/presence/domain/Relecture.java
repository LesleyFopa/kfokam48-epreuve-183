package com.kfokam48.presence.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Relecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercice_id", nullable = false, unique = true)
    private Exercice exercice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relecteur_id", nullable = false)
    private Etudiant relecteur;

    private Integer note;

    private String commentaire;

    @Enumerated(EnumType.STRING)
    private StatutRelecture statut = StatutRelecture.ASSIGNEE;

    private Instant rendueAt;
}
