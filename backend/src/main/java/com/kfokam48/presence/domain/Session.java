package com.kfokam48.presence.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    private String code;

    private Instant ouvertureAt;

    private Instant expirationAt;

    @Enumerated(EnumType.STRING)
    private StatutSession statut = StatutSession.OUVERTE;

    private Instant clotureAt;

    public boolean estOuverte() {
        return statut == StatutSession.OUVERTE;
    }

    public boolean codeExpire(Instant maintenant) {
        return maintenant.isAfter(expirationAt);
    }
}
