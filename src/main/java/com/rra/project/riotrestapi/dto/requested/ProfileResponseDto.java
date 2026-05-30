package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.LeagueEntryDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;

public record ProfileResponseDto(
        ProfileInfoDto player,
        LeagueInfoDto[] leagues
)
{
    public static ProfileResponseDto from(SummonerDto summoner, AccountDto account, LeagueEntryDto[] leagues) {
        return new ProfileResponseDto(ProfileInfoDto.from(summoner, account), LeagueInfoDto.from(leagues));
    }

}
