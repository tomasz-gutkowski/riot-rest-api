package com.rra.project.riotrestapi.dto.requested;


import com.rra.project.riotrestapi.dto.fetched.InfoDto;
import com.rra.project.riotrestapi.dto.fetched.MatchDto;
import com.rra.project.riotrestapi.dto.fetched.MetadataDto;
import com.rra.project.riotrestapi.dto.fetched.ParticipantDto;
import com.rra.project.riotrestapi.dto.requested.common.IdNamePair;
import com.rra.project.riotrestapi.service.datadragon.DataDragonService;


import java.util.ArrayList;
import java.util.List;

public record MatchInfoDto(
        String matchId,
        String gameResult,
        MatchIdInfo gameData,
        PlayerDisplayInfoDto player,
        List<ParticipantDisplayInfoDto> participants,
        long gameEndTimestamp,
        long gameDuration
){
    public static MatchInfoDto from(String ownerPuuid, MatchDto match, DataDragonService dataDragonService){
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

        var ownerInfo = PlayerDisplayInfoDto.from(owner, match.info(), dataDragonService);

        String gameRes;
        if(owner.gameEndedInEarlySurrender() && !owner.gameEndedInSurrender()){
            gameRes = "REMAKE";
        } else{
            gameRes = owner.win() ? "WIN" : "LOSS";
        }

        return new MatchInfoDto(
                metadata.matchId(),
                gameRes,
                new MatchIdInfo(info.queueId(), dataDragonService.getGameModeName(info.queueId())),
                ownerInfo,
                participantsInfo,
                info.gameEndTimestamp(),
                info.gameDuration()
        );
    }

    public record MatchIdInfo(
            int id,
            DataDragonService.MapModeNamePair info
    ){

    }

}