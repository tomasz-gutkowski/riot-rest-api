package com.rra.project.riotrestapi.dto.fetched;

public record MiniSeriesDto(
        int losses,
        String progress,
        int target,
        int wins
) {}