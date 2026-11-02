package com.example.wineapi.dto;

import java.util.List;

public record Wine(
        String id,
        String name,
        String producer,
        int vintage,
        List<String> grapes,
        String region,
        double glassPrice,
        double bottlePrice
)
{}
