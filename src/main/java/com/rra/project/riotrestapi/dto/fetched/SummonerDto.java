package com.rra.project.riotrestapi.dto.fetched;

public record SummonerDto(
        int profileIconId,
        long revisionDate,
        String puuid,
        long summonerLevel
)
{}
