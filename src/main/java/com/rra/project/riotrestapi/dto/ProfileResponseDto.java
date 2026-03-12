package com.rra.project.riotrestapi.dto;

public record ProfileResponseDto(
        SummonerInfoDto summoner,
        LeagueInfoDto[] leagues
) {

    public record SummonerInfoDto(
            String gameName,
            String tagLine,
            int profileIconId,
            long summonerLevel
    )
    {
        public static SummonerInfoDto from(SummonerDto summoner, AccountDto account) {
            return new SummonerInfoDto(account.gameName(), account.tagLine(),  summoner.profileIconId(), summoner.summonerLevel());
        }
    }

    public record LeagueInfoDto(
            String queueType,
            String tier,
            String rank,
            int leaguePoints,
            int wins,
            int losses
    ) {
        public static LeagueInfoDto[] from(LeagueEntryDto[] leagues) {
            LeagueInfoDto[] leaguesDto = new LeagueInfoDto[leagues.length];
            int i = 0;
            for (LeagueEntryDto league : leagues) {
                leaguesDto[i] = new LeagueInfoDto(
                        league.queueType(), league.tier(), league.rank(), league.leaguePoints(), league.wins(), league.losses());
                i++;
            }
            return leaguesDto;
        }
    }

    public static ProfileResponseDto from(SummonerDto summoner,AccountDto account, LeagueEntryDto[] leagues) {
        return new ProfileResponseDto(SummonerInfoDto.from(summoner, account), LeagueInfoDto.from(leagues));
    }
}
