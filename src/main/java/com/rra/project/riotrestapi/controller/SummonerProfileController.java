package com.rra.project.riotrestapi.controller;




import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.service.ServerID;
import com.rra.project.riotrestapi.service.SummonerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SummonerProfileController {

    private final SummonerService summonerService;

    SummonerProfileController(SummonerService summonerService) {
        this.summonerService = summonerService;
    }

    @GetMapping("/summoner/{serverId}/{gameName}-{tagLine}")
    public ResponseEntity<ProfileResponseDto> displaySummonerProfile(@PathVariable ServerID serverId,
                                                                     @PathVariable String gameName,
                                                                     @PathVariable String tagLine,
                                                                     @RequestParam(defaultValue = "0") int start,
                                                                     @RequestParam(defaultValue = "20") int count) {
        //request Riot API for user's data
        return ResponseEntity.ok(summonerService.getProfileResponseDto(serverId, gameName, tagLine, start, count));
    }



}