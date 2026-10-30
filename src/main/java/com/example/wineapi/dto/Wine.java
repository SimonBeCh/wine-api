package com.example.wineapi.dto;

public record Wine(
        String id,
        String name,
        String producer,
        int vintage,
        String grape,
        String region,
        double glassPrice,
        double bottlePrice
)
{}
