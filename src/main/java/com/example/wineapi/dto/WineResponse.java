package com.example.wineapi.dto;

public record WineResponse(
        WineDTO recommendedWine,
        String reason
) {
}
