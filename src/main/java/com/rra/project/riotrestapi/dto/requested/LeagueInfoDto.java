package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.LeagueEntryDto;

public record LeagueInfoDto(
        String queueType,
        String tier,
        String rank,
        int leaguePoints,
        int wins,
        int losses
) {
    public static LeagueInfoDto[] from(LeagueEntryDto[] leagues) {
        LeagueInfoDto[] leaguesDto = new LeagueInfoDto[leagues.length];
        int i = 0;
        for (LeagueEntryDto league : leagues) {
            leaguesDto[i] = new LeagueInfoDto(
                    league.queueType(), league.tier(), league.rank(), league.leaguePoints(), league.wins(), league.losses());
            i++;
        }
        return leaguesDto;
    }
}