package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.ParticipantDto;

public record ParticipantDisplayInfoDto(
        String puuid,
        String gameName,
        String tagLine,
        int championId,
        String position,
        int teamId //100 blue 200 red
) {
    public static ParticipantDisplayInfoDto from(ParticipantDto participant) {

        return new ParticipantDisplayInfoDto(
                participant.puuid(),
                participant.riotIdGameName(),
                participant.riotIdTagline(),
                participant.championId(),
                participant.teamPosition(),
                participant.teamId()
        );
    }
}
