package com.example.wineapi.dto;

import java.util.List;

public record WineResponse(
        List<WineDTO> recommendedWines,
        String reason
) {
}
