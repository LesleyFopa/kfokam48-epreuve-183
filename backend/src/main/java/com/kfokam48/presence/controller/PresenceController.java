package com.kfokam48.presence.controller;

import com.kfokam48.presence.dto.PresenceRequest;
import com.kfokam48.presence.dto.PresenceResponse;
import com.kfokam48.presence.service.PresenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/presences")
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @PostMapping
    public ResponseEntity<PresenceResponse> marquer(@Valid @RequestBody PresenceRequest requete) {
        PresenceResponse reponse = presenceService.marquerAvecCode(requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
}
