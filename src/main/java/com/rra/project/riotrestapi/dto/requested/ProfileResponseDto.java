package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.LeagueEntryDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;

public record ProfileResponseDto(
        SummonerInfoDto summoner,
        LeagueInfoDto[] leagues,
        MatchInfoDto[] matches
)
{
    public static ProfileResponseDto from(SummonerDto summoner,AccountDto account, LeagueEntryDto[] leagues) {
        return new ProfileResponseDto(SummonerInfoDto.from(summoner, account), LeagueInfoDto.from(leagues), null);
    }

}
