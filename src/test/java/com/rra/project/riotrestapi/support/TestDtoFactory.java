package com.rra.project.riotrestapi.support;

import com.rra.project.riotrestapi.dto.fetched.*;
import org.assertj.core.util.Arrays;

import java.util.List;

public class TestDtoFactory {
    public static AccountDto createAccountDto(String puuid) {
        final String gameName = "Hide on Bush";
        final String tagLine = "KR1";

        return new AccountDto(puuid, gameName, tagLine);
    }
    public static SummonerDto createSummonerDto(String puuid) {
        final int profileIconId = 1;
        final long revisionDate = 123456789L;
        final long summonerLevel = 1000L;

        return new SummonerDto(profileIconId, revisionDate, puuid, summonerLevel);
    }

    public static LeagueEntryDto[] createLeagueEntryDtoArr(String puuid) {
        return Arrays.array(createLeagueEntryDto(puuid, "RANKED_SOLO_5x5"), createLeagueEntryDto(puuid, "RANKED_PREMADE_5x5"), createLeagueEntryDto(puuid,"JADE_5X5"));
    }

    private static LeagueEntryDto createLeagueEntryDto(String puuid, String queueType) {
        final String tier = "CHALLENGER";
        final String rank = "1";
        final int leaguePoints = 1000;
        final int wins = 100;
        final int losses = 50;
        final boolean hotStreak = true;
        final boolean veteran = true;
        final boolean freshBlood = false;
        final boolean inactive = false;
        final MiniSeriesDto miniSeriesDto = null;

        return new LeagueEntryDto(puuid, queueType, tier, rank, leaguePoints, wins, losses, hotStreak, veteran, freshBlood, inactive, miniSeriesDto);
    }

    public static List<String> createMatchDtoArr() {
        return List.of("KR_M001","KR_M002","KR_M003","KR_M004","KR_M005","KR_M006","KR_M007","KR_M008","KR_M009");
    }

    public static MatchDto createMatchDto(String matchId) {
        MetadataDto metaData = new MetadataDto("10.1", matchId, List.of("puuid-0001","puuid-0002","puuid-0003","puuid-0004","puuid-0005"));
        return new MatchDto(metaData, null);
    }
}
