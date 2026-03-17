package com.rra.project.riotrestapi.service;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.LeagueEntryDto;
import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;

import org.springframework.stereotype.Service;

@Service
public class SummonerService {

    private final SummonerClient summonerClient;

    SummonerService(SummonerClient summonerClient) {
        this.summonerClient = summonerClient;
    }

    public AccountDto getAccountDto(ServerID serverId, String gameName, String tagLine) {
        return summonerClient.callForAccountDto(serverId, gameName, tagLine);
    }

    public ProfileResponseDto getProfileResponseDto(ServerID serverId, String gameName, String tagLine) {
        //put together basic data from multiple api calls to send to frontend
        AccountDto account = getAccountDto(serverId, gameName, tagLine);
        String puuid = account.puuid();
        SummonerDto summoner = summonerClient.callForSummonerDto(serverId, puuid);
        LeagueEntryDto[] leagueEntryDtoArr = summonerClient.callForLeagueEntryDtoArr(serverId, puuid);
        return ProfileResponseDto.from(summoner, account, leagueEntryDtoArr);
    }

}
