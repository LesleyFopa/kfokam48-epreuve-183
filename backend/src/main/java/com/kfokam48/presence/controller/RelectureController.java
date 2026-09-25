package com.kfokam48.presence.controller;

import com.kfokam48.presence.dto.RelectureRequest;
import com.kfokam48.presence.dto.RelectureVueRelecteur;
import com.kfokam48.presence.service.RelectureService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relectures")
public class RelectureController {

    private final RelectureService relectureService;

    public RelectureController(RelectureService relectureService) {
        this.relectureService = relectureService;
    }

    @GetMapping
    public List<RelectureVueRelecteur> pourRelecteur(@RequestParam Long relecteurId) {
        return relectureService.pourRelecteur(relecteurId);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> noter(@PathVariable Long id, @Valid @RequestBody RelectureRequest requete) {
        relectureService.noter(id, requete);
        return ResponseEntity.ok().build();
    }
}
