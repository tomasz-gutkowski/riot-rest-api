package com.rra.project.riotrestapi.dto;

public record SummonerDto(
        int profileIconId,
        long revisionDate,
        String puuid,
        long summonerLevel
)
{}
