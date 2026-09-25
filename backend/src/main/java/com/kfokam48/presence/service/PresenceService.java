package com.kfokam48.presence.service;

import com.kfokam48.presence.domain.Etudiant;
import com.kfokam48.presence.domain.Presence;
import com.kfokam48.presence.domain.Session;
import com.kfokam48.presence.domain.SourcePresence;
import com.kfokam48.presence.dto.PresenceManuelleRequest;
import com.kfokam48.presence.dto.PresenceRequest;
import com.kfokam48.presence.dto.PresenceResponse;
import com.kfokam48.presence.exception.ApiException;
import com.kfokam48.presence.repository.EtudiantRepository;
import com.kfokam48.presence.repository.PresenceRepository;
import com.kfokam48.presence.repository.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final SessionRepository sessionRepository;
    private final EtudiantRepository etudiantRepository;
    private final TentativeCodeTracker tentativeCodeTracker;

    public PresenceService(PresenceRepository presenceRepository,
                            SessionRepository sessionRepository,
                            EtudiantRepository etudiantRepository,
                            TentativeCodeTracker tentativeCodeTracker) {
        this.presenceRepository = presenceRepository;
        this.sessionRepository = sessionRepository;
        this.etudiantRepository = etudiantRepository;
        this.tentativeCodeTracker = tentativeCodeTracker;
    }

    /** EF2, RG1, RG2, RG3, RG15 : un étudiant marque sa présence avec le code de session. */
    public PresenceResponse marquerAvecCode(PresenceRequest requete) {
        tentativeCodeTracker.verifierNonBloque(requete.etudiantId());

        Session session = sessionRepository.findByCode(requete.code()).orElse(null);
        if (session == null) {
            tentativeCodeTracker.enregistrerEchec(requete.etudiantId());
            throw new ApiException(HttpStatus.BAD_REQUEST, "CODE_INCONNU", "Ce code de présence n'existe pas.");
        }

        if (!session.estOuverte() || session.codeExpire(Instant.now())) {
            throw new ApiException(HttpStatus.GONE, "CODE_EXPIRE", "Le code de présence a expiré.");
        }

        Etudiant etudiant = etudiantRepository.findById(requete.etudiantId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "ETUDIANT_INCONNU", "Étudiant inconnu."));

        if (presenceRepository.findBySessionIdAndEtudiantId(session.getId(), etudiant.getId()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "DEJA_PRESENT", "Présence déjà enregistrée pour cette session.");
        }

        Presence presence = new Presence();
        presence.setSession(session);
        presence.setEtudiant(etudiant);
        presence.setSource(SourcePresence.ETUDIANT);
        presence = presenceRepository.save(presence);

        tentativeCodeTracker.reinitialiser(requete.etudiantId());

        return versReponse(presence);
    }

    /** EF7, RG13, RG14 : le formateur ajoute une présence manuellement, sans code. */
    public PresenceResponse ajouterManuellement(Long sessionId, PresenceManuelleRequest requete) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "SESSION_INCONNUE", "Session inconnue."));

        if (!session.estOuverte()) {
            throw new ApiException(HttpStatus.CONFLICT, "SESSION_CLOTUREE", "La session est clôturée.");
        }

        Etudiant etudiant = etudiantRepository.findById(requete.etudiantId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "ETUDIANT_INCONNU", "Étudiant inconnu."));

        if (presenceRepository.findBySessionIdAndEtudiantId(session.getId(), etudiant.getId()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "DEJA_PRESENT", "Présence déjà enregistrée pour cette session.");
        }

        Presence presence = new Presence();
        presence.setSession(session);
        presence.setEtudiant(etudiant);
        presence.setSource(SourcePresence.FORMATEUR);
        presence = presenceRepository.save(presence);

        return versReponse(presence);
    }

    private PresenceResponse versReponse(Presence presence) {
        return new PresenceResponse(
                presence.getId(),
                presence.getSession().getId(),
                presence.getEtudiant().getId(),
                presence.getSource().name());
    }
}
