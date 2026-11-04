package com.example.wineapi.dto;

import java.math.BigDecimal;
import java.util.List;

public record WineDTO(
        String name,
        List<String> grapes,
        BigDecimal price,
        String tasteNotes,
        String vintage,
        String producent,
        String region
)
{}
