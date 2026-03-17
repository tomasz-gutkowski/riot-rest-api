package com.rra.project.riotrestapi.controller;




import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.service.ServerID;
import com.rra.project.riotrestapi.service.SummonerService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/summoner")
public class SummonerController {

    private final SummonerService summonerService;

    SummonerController(SummonerService summonerService) {
        this.summonerService = summonerService;
    }

    @GetMapping("/{serverId}/{gameName}-{tagLine}")
    public ProfileResponseDto displaySummonerProfile(@PathVariable ServerID serverId,
                                                     @PathVariable String gameName,
                                                     @PathVariable String tagLine,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        //request Riot API for user's data
        return summonerService.getProfileResponseDto(serverId, gameName, tagLine);
    }
}