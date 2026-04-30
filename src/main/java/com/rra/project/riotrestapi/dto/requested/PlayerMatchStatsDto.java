package com.rra.project.riotrestapi.dto.requested;

import java.util.List;

public record PlayerMatchStatsDto(
        int championId,
        int level,
        int keystoneId,
        int perkSecondaryTreeId,
        int summonerSpell1Id,
        int summonerSpell2Id,
        int kills,
        int deaths,
        int assists,
        int creepScore,
        boolean win,
        List<Integer> itemIds
) {

}
