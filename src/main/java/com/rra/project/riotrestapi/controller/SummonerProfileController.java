package com.rra.project.riotrestapi.controller;




import com.rra.project.riotrestapi.dto.requested.MatchDetailsDto;
import com.rra.project.riotrestapi.dto.requested.MatchInfoDto;
import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.service.riotapi.ServerID;
import com.rra.project.riotrestapi.service.riotapi.RiotApiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class SummonerProfileController {

    private final RiotApiService riotApiService;

    SummonerProfileController(RiotApiService riotApiService) {
        this.riotApiService = riotApiService;
    }

    //request for basic profile data
    @GetMapping("/profile/{serverId}/{gameName}/{tagLine}")
    public ProfileResponseDto displaySummonerProfile(@PathVariable ServerID serverId,
                                                                     @PathVariable String gameName,
                                                                     @PathVariable String tagLine) {

        return riotApiService.getProfileResponseDto(serverId, gameName, tagLine);
    }

    //request for list of match data
    @GetMapping("/matches/{serverId}/{puuid}")
    public List<MatchInfoDto> displayMatchList(@PathVariable ServerID serverId,
                                                             @PathVariable String puuid,
                                                             @RequestParam(defaultValue = "0") int start,
                                                             @RequestParam(defaultValue = "20") int count){
        return riotApiService.getMatchInfoDtos(serverId, puuid, start, count);
    }

    //request for specific match data
    @GetMapping("/match/{serverId}/{matchId}")
    public MatchDetailsDto displayMatchDetails(@PathVariable ServerID serverId,
                                                @PathVariable String matchId) {
        return riotApiService.getMatchDetailsDto(serverId,matchId);
    }

}