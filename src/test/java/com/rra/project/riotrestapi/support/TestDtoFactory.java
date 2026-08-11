package com.rra.project.riotrestapi.support;

import com.rra.project.riotrestapi.dto.fetched.*;
import org.assertj.core.util.Arrays;

import java.util.ArrayList;
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

    public static List<String> createMatchesArr() {
        return List.of("KR_M001","KR_M002","KR_M003","KR_M004","KR_M005","KR_M006","KR_M007","KR_M008","KR_M009");
    }

    public static MatchDto createMatchDto(String matchId) {
        List<ParticipantDto> participants = new ArrayList<>();
        String puuid = "puuid-";
        for(int i = 0; i < 10 ; i++){
            participants.add(createParticipantDto(puuid + i));
        }
        MetadataDto metaData = new MetadataDto("10.1", matchId, List.of("puuid-0001","puuid-0002","puuid-0003","puuid-0004","puuid-0005"));
        InfoDto info = new InfoDto(null, 0,0, 0,0, null,null, 0, null, null, 0, participants, null, 0, null, null);
        return new MatchDto(metaData, info);
    }

    static ParticipantDto createParticipantDto(String puuid) {
        return new ParticipantDto(
                0,      // allInPings
                0,      // assistMePings
                0,      // assists
                0,      // baronKills
                0,      // bountyLevel
                0,      // champExperience
                0,      // champLevel
                0,      // championId
                "",     // championName
                0,      // commandPings
                0,      // championTransform
                0,      // consumablesPurchased
                0,      // damageDealtToBuildings
                0,      // damageDealtToObjectives
                0,      // damageDealtToTurrets
                0,      // damageSelfMitigated
                0,      // deaths
                0,      // detectorWardsPlaced
                0,      // doubleKills
                0,      // dragonKills
                false,  // eligibleForProgression
                0,      // enemyMissingPings
                0,      // enemyVisionPings
                false,  // firstBloodAssist
                false,  // firstBloodKill
                false,  // firstTowerAssist
                false,  // firstTowerKill
                false,  // gameEndedInEarlySurrender
                false,  // gameEndedInSurrender
                0,      // holdPings
                0,      // getBackPings
                0,      // goldEarned
                0,      // goldSpent
                "",     // individualPosition
                0,      // inhibitorKills
                0,      // inhibitorTakedowns
                0,      // inhibitorsLost
                0,      // item0
                0,      // item1
                0,      // item2
                0,      // item3
                0,      // item4
                0,      // item5
                0,      // item6
                0,      // roleBoundItem
                0,      // itemsPurchased
                0,      // killingSprees
                0,      // kills
                "",     // lane
                0,      // largestCriticalStrike
                0,      // largestKillingSpree
                0,      // largestMultiKill
                0,      // longestTimeSpentLiving
                0,      // magicDamageDealt
                0,      // magicDamageDealtToChampions
                0,      // magicDamageTaken
                null,   // missions (MissionsDto)
                0,      // neutralMinionsKilled
                0,      // needVisionPings
                0,      // nexusKills
                0,      // nexusTakedowns
                0,      // nexusLost
                0,      // objectivesStolen
                0,      // objectivesStolenAssists
                0,      // onMyWayPings
                0,      // participantId
                0,      // playerScore0
                0,      // playerScore1
                0,      // playerScore2
                0,      // playerScore3
                0,      // playerScore4
                0,      // playerScore5
                0,      // playerScore6
                0,      // playerScore7
                0,      // playerScore8
                0,      // playerScore9
                0,      // playerScore10
                0,      // playerScore11
                0,      // pentaKills
                null,   // perks (PerksDto)
                0,      // physicalDamageDealt
                0,      // physicalDamageDealtToChampions
                0,      // physicalDamageTaken
                0,      // placement
                0,      // playerAugment1
                0,      // playerAugment2
                0,      // playerAugment3
                0,      // playerAugment4
                0,      // playerAugment5
                0,      // playerAugment6
                0,      // playerSubteamId
                0,      // pushPings
                0,      // profileIcon
                puuid,  // puuid
                0,      // quadraKills
                "",     // riotIdGameName
                "",     // riotIdTagline
                "",     // role
                0,      // sightWardsBoughtInGame
                0,      // spell1Casts
                0,      // spell2Casts
                0,      // spell3Casts
                0,      // spell4Casts
                0,      // subteamPlacement
                0,      // summoner1Casts
                0,      // summoner1Id
                0,      // summoner2Casts
                0,      // summoner2Id
                "",     // summonerId
                0,      // summonerLevel
                "",     // summonerName
                false,  // teamEarlySurrendered
                0,      // teamId
                "",     // teamPosition
                0,      // timeCCingOthers
                0,      // timePlayed
                0,      // totalAllyJungleMinionsKilled
                0,      // totalDamageDealt
                0,      // totalDamageDealtToChampions
                0,      // totalDamageShieldedOnTeammates
                0,      // totalDamageTaken
                0,      // totalEnemyJungleMinionsKilled
                0,      // totalHeal
                0,      // totalHealsOnTeammates
                0,      // totalMinionsKilled
                0,      // totalTimeCCDealt
                0,      // totalTimeSpentDead
                0,      // totalUnitsHealed
                0,      // tripleKills
                0,      // trueDamageDealt
                0,      // trueDamageDealtToChampions
                0,      // trueDamageTaken
                0,      // turretKills
                0,      // turretTakedowns
                0,      // turretsLost
                0,      // unrealKills
                0,      // visionScore
                0,      // visionClearedPings
                0,      // visionWardsBoughtInGame
                0,      // wardsKilled
                0,      // wardsPlaced
                false   // win
        );
    }
}
