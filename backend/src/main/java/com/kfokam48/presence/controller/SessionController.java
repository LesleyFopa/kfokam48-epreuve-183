package com.kfokam48.presence.controller;

import com.kfokam48.presence.dto.SessionCreateRequest;
import com.kfokam48.presence.dto.SessionResponse;
import com.kfokam48.presence.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> ouvrir(@Valid @RequestBody SessionCreateRequest requete) {
        SessionResponse reponse = sessionService.ouvrir(requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
}
