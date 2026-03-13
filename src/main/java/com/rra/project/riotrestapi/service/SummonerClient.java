package com.rra.project.riotrestapi.service;

import com.rra.project.riotrestapi.dto.AccountDto;
import com.rra.project.riotrestapi.dto.LeagueEntryDto;
import com.rra.project.riotrestapi.dto.SummonerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SummonerClient {

    @Value("${riot.api.key}")
    private String apiKey;

    public RestClient getRegionClient(ServerID serverId) {
        System.out.println(apiKey);
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


}
