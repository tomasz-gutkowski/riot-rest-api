package com.rra.project.riotrestapi.service;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.LeagueEntryDto;
import com.rra.project.riotrestapi.dto.fetched.MatchDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class RiotApiClient {

    @Value("${riot.api.key}")
    private String apiKey;

    public RestClient getRegionClient(ServerID serverId) {
        return RestClient.builder()
                .baseUrl(serverId.getRegion().getBaseUrl())
                .defaultHeader("X-Riot-Token", apiKey)
                .build();
    }

    public RestClient getServerClient(ServerID serverId) {
        return RestClient.builder()
                .baseUrl(serverId.getBaseUrl())
                .defaultHeader("X-Riot-Token", apiKey)
                .build();
    }

    public AccountDto callForAccountDto(ServerID serverId, String gameName, String tagLine) {
        return getRegionClient(serverId).get()
                .uri("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine)
                .retrieve()
                .body(AccountDto.class);
    }

    public SummonerDto callForSummonerDto(ServerID serverId, String puuid) {
        return getServerClient(serverId).get()
                .uri("/lol/summoner/v4/summoners/by-puuid/{puuid}", puuid)
                .retrieve()
                .body(SummonerDto.class);
    }

    public LeagueEntryDto[] callForLeagueEntryDtoArr(ServerID serverId, String puuid) {
        return getServerClient(serverId).get()
                .uri("/lol/league/v4/entries/by-puuid/{puuid}", puuid)
                .retrieve()
                .body(LeagueEntryDto[].class);
    }

    public List<String> callForMatchesList(ServerID serverId, String puuid, int start, int count) {
        return getRegionClient(serverId).get()
                .uri("/lol/match/v5/matches/by-puuid/{puuid}/ids?start={start}&count={count}", puuid, start, count)
                .retrieve()
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    public MatchDto callForMatchDto(ServerID serverId, String matchId) {
        return getRegionClient(serverId).get()
                .uri("/lol/match/v5/matches/{matchId}", matchId)
                .retrieve()
                .body(MatchDto.class);
    }
}
