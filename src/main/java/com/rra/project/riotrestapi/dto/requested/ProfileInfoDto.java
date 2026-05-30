package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;

public record ProfileInfoDto(
        String puuid,
        String gameName,
        String tagLine,
        int profileIconId,
        long summonerLevel
)
{
    public static ProfileInfoDto from(SummonerDto summoner, AccountDto account) {
        return new ProfileInfoDto(account.puuid(), account.gameName(), account.tagLine(),  summoner.profileIconId(), summoner.summonerLevel());
    }
}