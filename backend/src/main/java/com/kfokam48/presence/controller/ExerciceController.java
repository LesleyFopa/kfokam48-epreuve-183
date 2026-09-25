package com.kfokam48.presence.controller;

import com.kfokam48.presence.dto.ExerciceCreateResponse;
import com.kfokam48.presence.dto.ExerciceRequest;
import com.kfokam48.presence.dto.ExerciceVueEtudiant;
import com.kfokam48.presence.service.ExerciceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/exercices")
public class ExerciceController {

    private final ExerciceService exerciceService;

    public ExerciceController(ExerciceService exerciceService) {
        this.exerciceService = exerciceService;
    }

    @PostMapping
    public ResponseEntity<ExerciceCreateResponse> deposer(@Valid @RequestBody ExerciceRequest requete) {
        ExerciceCreateResponse reponse = exerciceService.deposer(requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }

    @GetMapping
    public List<ExerciceVueEtudiant> consulter(@RequestParam Long etudiantId) {
        return exerciceService.consulterParEtudiant(etudiantId);
    }
}
