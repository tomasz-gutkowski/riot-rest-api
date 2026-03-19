package com.rra.project.riotrestapi.service;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.LeagueEntryDto;
import com.rra.project.riotrestapi.dto.fetched.MatchDto;
import com.rra.project.riotrestapi.dto.requested.MatchInfoDto;
import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SummonerService {

    private final SummonerClient summonerClient;

    SummonerService(SummonerClient summonerClient) {
        this.summonerClient = summonerClient;
    }

    public AccountDto getAccountDto(ServerID serverId, String gameName, String tagLine) {
        return summonerClient.callForAccountDto(serverId, gameName, tagLine);
    }

    public ProfileResponseDto getProfileResponseDto(ServerID serverId, String gameName, String tagLine, int page, int size) {
        //put together basic data from multiple api calls to send to frontend
        AccountDto account = getAccountDto(serverId, gameName, tagLine);
        String puuid = account.puuid();
        SummonerDto summoner = summonerClient.callForSummonerDto(serverId, puuid);
        LeagueEntryDto[] leagueEntryDtoArr = summonerClient.callForLeagueEntryDtoArr(serverId, puuid);
        List<MatchDto> matchDtos = getMatchDtos(serverId, puuid, page, size);


        return ProfileResponseDto.from(summoner, account, leagueEntryDtoArr, convertMatchDtoToMatchInfoDto(matchDtos, puuid));
    }

    public List<MatchDto> getMatchDtos(ServerID serverId, String puuid, int page, int size) {
        List<String> matches = summonerClient.callForMatchesList(serverId, puuid, page, size);
        List<MatchDto> matchDtos = new ArrayList<>();
        for (String matchId : matches) {
            matchDtos.add(summonerClient.callForMatchDto(serverId, matchId));
        }
        return matchDtos;
    }

    public List<MatchInfoDto> convertMatchDtoToMatchInfoDto(List<MatchDto> matchDtos, String puuid) {
        List<MatchInfoDto> matchInfoDtos = new ArrayList<>();
        matchDtos.forEach(matchDto -> {
            matchInfoDtos.add(MatchInfoDto.from(puuid, matchDto));
        });
        return matchInfoDtos;
    }
}
