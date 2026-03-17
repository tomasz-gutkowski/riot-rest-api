package com.rra.project.riotrestapi.dto.fetched;

public record LeagueEntryDto(
        String leagueId,
        String puuid,
        String queueType,
        String tier,
        String rank,
        int leaguePoints,
        int wins,
        int losses,
        boolean hotStreak,
        boolean veteran,
        boolean freshBlood,
        boolean inactive,
        MiniSeriesDto miniSeries
)
{}