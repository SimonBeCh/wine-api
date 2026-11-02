package com.example.wineapi.dto;

import java.util.List;

public record WineRequest(
    List<WineDTO> wines,
    String mealPairing
)
{}
