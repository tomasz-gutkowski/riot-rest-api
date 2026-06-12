package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.ParticipantDto;
import com.rra.project.riotrestapi.service.datadragon.DataDragonService;

public record ParticipantDisplayInfoDto(
        String puuid,
        String gameName,
        String tagLine,
        PlayerDisplayInfoDto.ChampionData championData,
        String position,
        int teamId, //100 blue 200 red
        int placement
) {
    public static ParticipantDisplayInfoDto from(ParticipantDto participant, DataDragonService dataDragonService) {
        int tId;
        int place;
        if(participant.playerSubteamId() == 0) {
            tId = participant.teamId();
            place = 0;
        } else{
            tId = participant.playerSubteamId();
            place = participant.subteamPlacement();
        }

        return new ParticipantDisplayInfoDto(
                participant.puuid(),
                participant.riotIdGameName(),
                participant.riotIdTagline(),
                new PlayerDisplayInfoDto.ChampionData(participant.championId(), dataDragonService.getChampionName(participant.championId())),
                participant.teamPosition(),
                tId,
                place
        );
    }

}
