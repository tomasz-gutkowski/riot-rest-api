package com.rra.project.riotrestapi.dto;

import com.rra.project.riotrestapi.dto.fetched.MatchDto;
import com.rra.project.riotrestapi.dto.fetched.MetadataDto;
import com.rra.project.riotrestapi.dto.requested.LeagueInfoDto;
import com.rra.project.riotrestapi.dto.requested.MatchInfoDto;
import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.dto.requested.SummonerInfoDto;
import com.rra.project.riotrestapi.service.riotapi.ServerID;

import java.util.ArrayList;
import java.util.List;

public class TestDtoFactory {

    public static final String gameName = "Faker";
    public static final String tagLine = "KR1";
    public static final ServerID serverId = ServerID.KR;

    public static ProfileResponseDto singleQueueRankedProfile() {
        return new ProfileResponseDto(summonerInfo(), singleQueueLeagueInfo());
    }

    public static ProfileResponseDto multipleQueueRankedProfile() {
        return new ProfileResponseDto(summonerInfo(), multipleQueueLeagueInfo());
    }

    public static ProfileResponseDto unrankedQueueRankedProfile() {
        return new ProfileResponseDto(summonerInfo(), unrankedQueueLeagueInfo());
    }

    public static SummonerInfoDto summonerInfo(){
        return new SummonerInfoDto(gameName, tagLine, 0, 1000);
    }

    public static LeagueInfoDto[] singleQueueLeagueInfo(){
        return new LeagueInfoDto[] {new LeagueInfoDto("RANKED_SOLO_5x5", "CHALLENGER","I", 100, 100, 50)};
    }

    public static LeagueInfoDto[] multipleQueueLeagueInfo(){
        return new LeagueInfoDto[] {new LeagueInfoDto("RANKED_SOLO_5x5", "CHALLENGER","I", 100, 100, 50),
                                    new LeagueInfoDto("RANKED_FLEX_SR", "IRON", "IV", 0, 50, 100)};
    }

    public static LeagueInfoDto[] unrankedQueueLeagueInfo(){
        return new LeagueInfoDto[0];
    }

    public static List<MatchInfoDto> matchInfoListStartingFromOfSize(int start, int count){
       List<MatchInfoDto> matchInfoDtos = new ArrayList<>();
       List<MatchDto> matchDtos = matchListStartingFromOfSize(start, count);
       for(MatchDto match : matchDtos){
            matchInfoDtos.add(matchDtoToMatchInfoDto(match));
       }
       return matchInfoDtos;
    }

    public static List<MatchDto> matchListStartingFromOfSize(int start, int count){
        List<MatchDto> matchDtos = new ArrayList<>();
        for(int i = 0; i < count; i++){
            matchDtos.add(new MatchDto(metaDataWithIdNumber(start+i), null));
        }
        return matchDtos;
    }

    public static MetadataDto metaDataWithIdNumber(int num){
        return new MetadataDto(null, "GAME"+num, null);
    }

    public static MatchInfoDto matchDtoToMatchInfoDto(MatchDto matchDto){
        return new MatchInfoDto(matchDto.metadata().matchId(), null, null, null,null, 0, 0);
    }
}
