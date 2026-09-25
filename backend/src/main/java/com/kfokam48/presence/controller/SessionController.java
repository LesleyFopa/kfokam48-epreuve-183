package com.kfokam48.presence.controller;

import com.kfokam48.presence.dto.PresenceManuelleRequest;
import com.kfokam48.presence.dto.PresenceResponse;
import com.kfokam48.presence.dto.SessionCreateRequest;
import com.kfokam48.presence.dto.SessionResponse;
import com.kfokam48.presence.service.PresenceService;
import com.kfokam48.presence.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final PresenceService presenceService;

    public SessionController(SessionService sessionService, PresenceService presenceService) {
        this.sessionService = sessionService;
        this.presenceService = presenceService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> ouvrir(@Valid @RequestBody SessionCreateRequest requete) {
        SessionResponse reponse = sessionService.ouvrir(requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    @GetMapping("/active")
    public SessionResponse sessionActive(@RequestParam Long promotionId) {
        return sessionService.sessionActive(promotionId);
    }

    @PostMapping("/{id}/presences")
    public ResponseEntity<PresenceResponse> ajouterPresenceManuelle(
            @PathVariable Long id, @Valid @RequestBody PresenceManuelleRequest requete) {
        PresenceResponse reponse = presenceService.ajouterManuellement(id, requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    @PatchMapping("/{id}/cloturer")
    public ResponseEntity<Void> cloturer(@PathVariable Long id) {
        sessionService.cloturer(id);
        return ResponseEntity.noContent().build();
    }
}
