package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.LeagueEntryDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;

import java.util.List;

public record ProfileResponseDto(
        SummonerInfoDto summoner,
        LeagueInfoDto[] leagues
)
{
    public static ProfileResponseDto from(SummonerDto summoner, AccountDto account, LeagueEntryDto[] leagues) {
        return new ProfileResponseDto(SummonerInfoDto.from(summoner, account), LeagueInfoDto.from(leagues));
    }

}
