package com.example.wineapi.dto.manualOperation;

import com.example.wineapi.dto.WineDTO;

import java.util.List;

public record WineRequest(
    List<WineDTO> wines,
    String mealPairing
)
{}
