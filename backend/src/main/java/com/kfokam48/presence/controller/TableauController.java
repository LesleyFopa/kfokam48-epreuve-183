package com.kfokam48.presence.controller;

import com.kfokam48.presence.dto.TableauLigne;
import com.kfokam48.presence.service.TableauService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tableau")
public class TableauController {

    private final TableauService tableauService;

    public TableauController(TableauService tableauService) {
        this.tableauService = tableauService;
    }

    @GetMapping
    public List<TableauLigne> tableau(@RequestParam Long promotionId) {
        return tableauService.pourPromotion(promotionId);
    }
}
