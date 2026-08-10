package com.rra.project.riotrestapi.service.riotapi;

import com.rra.project.riotrestapi.dto.fetched.*;
import com.rra.project.riotrestapi.dto.requested.*;

import com.rra.project.riotrestapi.service.datadragon.DataDragonService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiotApiService {

    private final RiotApiClient riotApiClient;
    private final DataDragonService dataDragonService;

    RiotApiService(RiotApiClient riotApiClient, DataDragonService dataDragonService) {
        this.riotApiClient = riotApiClient;
        this.dataDragonService = dataDragonService;
    }

    public ProfileResponseDto getProfileResponseDto(ServerID serverId, String gameName, String tagLine) {
        AccountDto account = riotApiClient.callForAccountDto(serverId, gameName, tagLine);
        String puuid = account.puuid();
        SummonerDto summoner = riotApiClient.callForSummonerDto(serverId, puuid);
        LeagueEntryDto[] leagueEntryDtoArr = riotApiClient.callForLeagueEntryDtoArr(serverId, puuid);

        return ProfileResponseDto.from(summoner, account, leagueEntryDtoArr);
    }

    public List<MatchDto> getMatchDtos(ServerID serverId, String puuid, long endTime, int start, int count) {
        List<String> matches = riotApiClient.callForMatchesList(serverId, puuid, endTime, start, count);
        List<MatchDto> matchDtos = new ArrayList<>();
        for (String matchId : matches) {
            matchDtos.add(riotApiClient.callForMatchDto(serverId, matchId));
        }
        return matchDtos;
    }

    public List<MatchInfoDto> getMatchInfoDtos(ServerID serverId, String puuid, long endTime, int start, int count) {
        List<MatchDto> matchDtos = getMatchDtos(serverId, puuid, endTime, start, count);
        List<MatchInfoDto> matchInfoDtos = new ArrayList<>();

        matchDtos.forEach(matchDto -> {
            matchInfoDtos.add(MatchInfoDto.from(puuid, matchDto, dataDragonService));
        });

        return matchInfoDtos;
    }

    public MatchDetailsDto getMatchDetailsDto(ServerID serverId, String matchId) {
        ArrayList<PlayerDisplayInfoDto> playerDisplayInfoDtos = new ArrayList<>();
        MatchDto match = riotApiClient.callForMatchDto(serverId, matchId);
        for(ParticipantDto p : match.info().participants()){
            playerDisplayInfoDtos.add(PlayerDisplayInfoDto.from(p, match.info(), this.dataDragonService));
        }
        return new MatchDetailsDto(playerDisplayInfoDtos);
    }

    public String getDatadragonLatest(){
        return dataDragonService.getLatestVersion();
    }
}
