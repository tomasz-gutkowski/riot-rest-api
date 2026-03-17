package com.rra.project.riotrestapi.dto.requested;


import com.rra.project.riotrestapi.dto.fetched.InfoDto;
import com.rra.project.riotrestapi.dto.fetched.MatchDto;
import com.rra.project.riotrestapi.dto.fetched.MetadataDto;
import com.rra.project.riotrestapi.dto.fetched.ParticipantDto;


import java.util.ArrayList;
import java.util.List;

public record MatchInfoDto(
        String matchId,
        String endOfGameResult,
        String gameMode,
        PlayerDisplayInfoDto player,
        List<ParticipantDisplayInfoDto> participants,
        long gameCreation,
        long gameDuration
){
    public static MatchInfoDto from(String ownerPuuid, MatchDto match){
        MetadataDto metadata = match.metadata();
        InfoDto info = match.info();
        List<ParticipantDto> participants = info.participants();
        List<ParticipantDisplayInfoDto> participantsInfo = new ArrayList<>();

        ParticipantDto owner = null;
        for(ParticipantDto participant : participants){
            participantsInfo.add(ParticipantDisplayInfoDto.from(participant));

            if(participant.puuid().equals(ownerPuuid)){
                owner = participant;
            }
        }
        assert owner != null;

        var ownerInfo = PlayerDisplayInfoDto.from(owner);

        return new MatchInfoDto(
                metadata.matchId(),
                info.endOfGameResult(),
                info.gameMode(),
                ownerInfo,
                participantsInfo,
                info.gameCreation(),
                info.gameDuration()
        );
    }
}