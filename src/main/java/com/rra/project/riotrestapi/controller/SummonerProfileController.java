package com.rra.project.riotrestapi.controller;




import com.rra.project.riotrestapi.dto.requested.MatchDetailsDto;
import com.rra.project.riotrestapi.dto.requested.MatchInfoDto;
import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.service.ratelimiters.ControllerRateLimiter;
import com.rra.project.riotrestapi.service.riotapi.ServerID;
import com.rra.project.riotrestapi.service.riotapi.RiotApiService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${cors.allowed-origin}")
public class SummonerProfileController {

    private final RiotApiService riotApiService;
    private final ControllerRateLimiter rateLimiter;

    SummonerProfileController(RiotApiService riotApiService, ControllerRateLimiter rateLimiter) {
        this.riotApiService = riotApiService;
        this.rateLimiter = rateLimiter;
    }

    //request for basic profile data
    @GetMapping("/profile/{serverId}/{gameName}/{tagLine}")
    public ProfileResponseDto getSummonerProfile(@PathVariable ServerID serverId,
                                                                    @PathVariable String gameName,
                                                                    @PathVariable String tagLine,
                                                                    HttpServletRequest request) {
        rateLimiter.consumeTokens(request, 30);

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
        rateLimiter.consumeTokens(request, tokenCost);
        return riotApiService.getMatchInfoDtos(serverId, puuid, endTime, start, count);
    }

    //request for specific match data
    @GetMapping("/match/{serverId}/{matchId}")
    public MatchDetailsDto getMatchDetails(@PathVariable ServerID serverId,
                                                            @PathVariable String matchId,
                                                            HttpServletRequest request) {
        rateLimiter.consumeTokens(request, 5);
        return riotApiService.getMatchDetailsDto(serverId,matchId);
    }

    @GetMapping("/ddragon/latest")
    public String getLatestDdragon(HttpServletRequest request) {
        rateLimiter.consumeTokens(request, 2);
        return riotApiService.getDatadragonLatest();
    }


}