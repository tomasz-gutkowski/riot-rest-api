package com.rra.project.riotrestapi.controller;




import com.rra.project.riotrestapi.dto.requested.MatchInfoDto;
import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.service.ServerID;
import com.rra.project.riotrestapi.service.SummonerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SummonerProfileController {

    private final SummonerService summonerService;

    SummonerProfileController(SummonerService summonerService) {
        this.summonerService = summonerService;
    }

    //request for basic profile data
    @GetMapping("/summoner/{serverId}/{gameName}-{tagLine}")
    public ProfileResponseDto displaySummonerProfile(@PathVariable ServerID serverId,
                                                                     @PathVariable String gameName,
                                                                     @PathVariable String tagLine) {

        return summonerService.getProfileResponseDto(serverId, gameName, tagLine);
    }

    //request for list of match data
    @GetMapping("/api/matches/{serverId}/{gameName}-{tagLine}")
    public List<MatchInfoDto> displayMatchList(@PathVariable ServerID serverId,
                                                             @PathVariable String gameName,
                                                             @PathVariable String tagLine,
                                                             @RequestParam(defaultValue = "0") int start,
                                                             @RequestParam(defaultValue = "20") int count){
        return summonerService.getMatchInfoDtos(serverId, gameName, tagLine, start, count);
    }


}