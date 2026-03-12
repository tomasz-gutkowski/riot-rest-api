package com.rra.project.riotrestapi.dto;

public record MiniSeriesDto(
        int losses,
        String progress,
        int target,
        int wins
) {}