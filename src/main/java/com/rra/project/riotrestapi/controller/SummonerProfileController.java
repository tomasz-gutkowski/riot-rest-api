package com.rra.project.riotrestapi.controller;




import com.rra.project.riotrestapi.dto.requested.MatchDetailsDto;
import com.rra.project.riotrestapi.dto.requested.MatchInfoDto;
import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.exceptions.code4xx.RateLimitException;
import com.rra.project.riotrestapi.service.riotapi.ServerID;
import com.rra.project.riotrestapi.service.riotapi.RiotApiService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${cors.allowed-origin}")
public class SummonerProfileController {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private final RiotApiService riotApiService;

    SummonerProfileController(RiotApiService riotApiService) {
        this.riotApiService = riotApiService;
    }

    //request for basic profile data
    @GetMapping("/profile/{serverId}/{gameName}/{tagLine}")
    public ProfileResponseDto getSummonerProfile(@PathVariable ServerID serverId,
                                                                    @PathVariable String gameName,
                                                                    @PathVariable String tagLine,
                                                                    HttpServletRequest request) {
        consumeTokens(request, 30);

        return riotApiService.getProfileResponseDto(serverId, gameName, tagLine);
    }

    //request for list of match data
    @GetMapping("/matches/{serverId}/{puuid}/{endTime}")
    public List<MatchInfoDto> getMatchList(@PathVariable ServerID serverId,
                                                            @PathVariable String puuid,
                                                            @PathVariable long endTime,
                                                            @RequestParam(defaultValue = "0") int start,
                                                            @RequestParam(defaultValue = "10") int count,
                                                            HttpServletRequest request){
        int tokenCost = 10*(count+1);
        consumeTokens(request, tokenCost);
        return riotApiService.getMatchInfoDtos(serverId, puuid, endTime, start, count);
    }

    //request for specific match data
    @GetMapping("/match/{serverId}/{matchId}")
    public MatchDetailsDto getMatchDetails(@PathVariable ServerID serverId,
                                                            @PathVariable String matchId,
                                                            HttpServletRequest request) {
        consumeTokens(request, 5);
        return riotApiService.getMatchDetailsDto(serverId,matchId);
    }

    @GetMapping("/ddragon/latest")
    public String getLatestDdragon(HttpServletRequest request) {
        consumeTokens(request, 2);
        return riotApiService.getDatadragonLatest();
    }

    private Bucket buildBucket(){
        return Bucket.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(360)
                        .refillGreedy(220, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    private void consumeTokens(HttpServletRequest request, int tokens) {
        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, key -> buildBucket());
        if(!bucket.tryConsume(tokens)) throw new RateLimitException("Rate limit exceeded, try again later");
    }

}