package com.rra.project.riotrestapi.service;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.LeagueEntryDto;
import com.rra.project.riotrestapi.dto.fetched.MatchDto;
import com.rra.project.riotrestapi.dto.requested.MatchDetailsDto;
import com.rra.project.riotrestapi.dto.requested.MatchInfoDto;
import com.rra.project.riotrestapi.dto.requested.PlayerMatchStatsDto;
import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SummonerService {

    private final RiotApiClient riotApiClient;

    SummonerService(RiotApiClient riotApiClient) {
        this.riotApiClient = riotApiClient;
    }

    public AccountDto getAccountDto(ServerID serverId, String gameName, String tagLine) {
        return riotApiClient.callForAccountDto(serverId, gameName, tagLine);
    }

    public ProfileResponseDto getProfileResponseDto(ServerID serverId, String gameName, String tagLine) {
        AccountDto account = getAccountDto(serverId, gameName, tagLine);
        String puuid = account.puuid();
        SummonerDto summoner = riotApiClient.callForSummonerDto(serverId, puuid);
        LeagueEntryDto[] leagueEntryDtoArr = riotApiClient.callForLeagueEntryDtoArr(serverId, puuid);

        return ProfileResponseDto.from(summoner, account, leagueEntryDtoArr);
    }

    public List<MatchDto> getMatchDtos(ServerID serverId, String puuid, int start, int count) {
        List<String> matches = riotApiClient.callForMatchesList(serverId, puuid, start, count);
        List<MatchDto> matchDtos = new ArrayList<>();
        for (String matchId : matches) {
            matchDtos.add(riotApiClient.callForMatchDto(serverId, matchId));
        }
        return matchDtos;
    }

    public List<MatchInfoDto> getMatchInfoDtos(ServerID serverId, String name, String tagLine, int start, int count) {
        String puuid = riotApiClient.callForAccountDto(serverId, name, tagLine).puuid();
        List<MatchDto> matchDtos = getMatchDtos(serverId, puuid, start, count);
        List<MatchInfoDto> matchInfoDtos = new ArrayList<>();

        matchDtos.forEach(matchDto -> {
            matchInfoDtos.add(MatchInfoDto.from(puuid, matchDto));
        });

        return matchInfoDtos;
    }

    public MatchDetailsDto getMatchDetailsDto(String matchId) {
        return null;
    }

    public PlayerMatchStatsDto getPlayerMatchStatsDto() {
        return null;
    }
}
