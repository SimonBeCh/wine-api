package com.example.wineapi.dto.imageOperation;

import com.example.wineapi.dto.WineDTO;

import java.util.List;

public record ImageWineResponse(
        List<WineDTO> recommendedWines,
        String reason
)
{}
