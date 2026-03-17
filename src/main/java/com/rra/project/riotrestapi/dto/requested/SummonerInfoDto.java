package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;

public record SummonerInfoDto(
        String gameName,
        String tagLine,
        int profileIconId,
        long summonerLevel
)
{
    public static SummonerInfoDto from(SummonerDto summoner, AccountDto account) {
        return new SummonerInfoDto(account.gameName(), account.tagLine(),  summoner.profileIconId(), summoner.summonerLevel());
    }
}