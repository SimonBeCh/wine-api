package com.example.wineapi.controller;

import com.example.wineapi.dto.WineRequest;
import com.example.wineapi.dto.WineResponse;
import com.example.wineapi.service.WineService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WineController {

    private final WineService service;

    public WineController(WineService service) {
        this.service = service;
    }

    @PostMapping("/wine/suggest")
    public WineResponse wineSuggestion(@RequestBody WineRequest wineRequest) {
        return service.suggestion(wineRequest);
    }
}
