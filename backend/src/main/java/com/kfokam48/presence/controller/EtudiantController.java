package com.kfokam48.presence.controller;

import com.kfokam48.presence.dto.EtudiantDto;
import com.kfokam48.presence.service.EtudiantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class EtudiantController {

    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping("/{promotionId}/etudiants")
    public List<EtudiantDto> lister(@PathVariable Long promotionId) {
        return etudiantService.listerParPromotion(promotionId);
    }
}
