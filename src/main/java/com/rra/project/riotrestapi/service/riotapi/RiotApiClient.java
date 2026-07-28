package com.rra.project.riotrestapi.service.riotapi;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.LeagueEntryDto;
import com.rra.project.riotrestapi.dto.fetched.MatchDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;
import com.rra.project.riotrestapi.exceptions.code4xx.*;
import com.rra.project.riotrestapi.exceptions.code5xx.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;

@Component
public class RiotApiClient {

    private final Bandwidth perSecondLimit = Bandwidth.builder()
            .capacity(20)
            .refillIntervally(20, Duration.ofSeconds(1))
            .build();

    private final Bandwidth per2MinuteLimit = Bandwidth.builder()
            .capacity(100)
            .refillIntervally(100, Duration.ofMinutes(2))
            .build();

    private final Bucket bucket = Bucket.builder()
            .addLimit(per2MinuteLimit)
            .addLimit(perSecondLimit)
            .build();


    @Value("${riot.api.key}")
    private String apiKey;

    private final Map<ServerID, RestClient> regionClients = new ConcurrentHashMap<ServerID, RestClient>();
    private final Map<ServerID, RestClient> serverClients = new ConcurrentHashMap<ServerID, RestClient>();

    public RestClient getRegionClient(ServerID serverId) {
        return regionClients.computeIfAbsent(serverId,
                (sid) -> RestClient.builder()
                        .baseUrl(sid.getRegion().getBaseUrl())
                        .defaultHeader("X-Riot-Token", apiKey)
                        .build());
    }

    public RestClient getServerClient(ServerID serverId) {
        return serverClients.computeIfAbsent(serverId,
                (sid) -> RestClient.builder()
                        .baseUrl(sid.getBaseUrl())
                        .defaultHeader("X-Riot-Token", apiKey)
                        .build());
    }

    public AccountDto callForAccountDto(ServerID serverId, String gameName, String tagLine) {
        // "/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}", gameName, tagLine
        String uri = String.format("/riot/account/v1/accounts/by-riot-id/%s/%s", gameName, tagLine);
        RestClient regionClient = getRegionClient(serverId);

        return callRiotApi(regionClient, uri)
                .body(AccountDto.class);
    }

    public SummonerDto callForSummonerDto(ServerID serverId, String puuid) {
        // "/lol/summoner/v4/summoners/by-puuid/{puuid}", puuid
        String uri = String.format("/lol/summoner/v4/summoners/by-puuid/%s", puuid);
        RestClient serverClient = getServerClient(serverId);

        return callRiotApi(serverClient, uri)
                .body(SummonerDto.class);
    }

    public LeagueEntryDto[] callForLeagueEntryDtoArr(ServerID serverId, String puuid) {
        // "/lol/league/v4/entries/by-puuid/{puuid}", puuid
        String uri = String.format("/lol/league/v4/entries/by-puuid/%s", puuid);
        RestClient serverClient = getServerClient(serverId);

        return callRiotApi(serverClient, uri)
                .body(LeagueEntryDto[].class);
    }

    public List<String> callForMatchesList(ServerID serverId, String puuid, long endTime, int start, int count) {
        // "/lol/match/v5/matches/by-puuid/{puuid}/ids?endTime={timestamp}&start={start}&count={count}", puuid, endTime, start, count
        String uri = String.format("/lol/match/v5/matches/by-puuid/%s/ids?endTime=%d&start=%d&count=%d", puuid, endTime, start, count);
        RestClient regionClient = getRegionClient(serverId);

        return callRiotApi(regionClient, uri)
                .body(new ParameterizedTypeReference<List<String>>() {});
    }

    @Cacheable("matches")
    public MatchDto callForMatchDto(ServerID serverId, String matchId) {
        // "lol/match/v5/matches/{matchId}", matchId
        String uri = String.format("/lol/match/v5/matches/%s", matchId);
        RestClient regionClient = getRegionClient(serverId);

        return callRiotApi(regionClient, uri)
                .body(MatchDto.class);
    }

    public RestClient.ResponseSpec callRiotApi(RestClient client, String uri){
        if(!bucket.tryConsume(1)) throw new RateLimitException("Global rate limit exceeded");

        return client.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->{
                    switch(response.getStatusCode().value()){
                        case 400 -> throw new BadRequestException("Bad request");
                        case 401 -> throw new UnauthorizedException("Unauthorized");
                        case 403 -> throw new ForbiddenException("Forbidden");
                        case 404 -> throw new ResourceNotFoundException("Resource not found");
                        case 405 -> throw new MethodNotAllowedException("Method not allowed");
                        case 415 -> throw new UnsupportedMediaTypeException("Unsupported media type");
                        case 429 -> throw new RateLimitException("Riot API rate limit exceeded");
                        default  -> throw new BadRequestException("Client error: " + response.getStatusCode());
                    }
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) ->{
                    switch(response.getStatusCode().value()){
                        case 500 -> throw new InternalServerErrorException("Internal server error");
                        case 502 -> throw new BadGatewayException("Bad gateway");
                        case 503 -> throw new ServiceUnavailableException("Service unavailable");
                        case 504 -> throw new GatewayTimeoutException("Gateway timeout");
                        default  -> throw new InternalServerErrorException("Server error: " + response.getStatusCode());
                    }
                });
    }
}
