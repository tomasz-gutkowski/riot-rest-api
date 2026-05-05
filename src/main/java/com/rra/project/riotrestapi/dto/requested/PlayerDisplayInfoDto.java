package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.ParticipantDto;
import com.rra.project.riotrestapi.dto.fetched.PerkStyleDto;
import com.rra.project.riotrestapi.dto.fetched.PerksDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record PlayerDisplayInfoDto(
        String riotIdGameName,
        String riotIdTagline,
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
        int totalDamageDealtToChampions,
        boolean win,
        List<Integer> itemIds
)
{
    public static PlayerDisplayInfoDto from(ParticipantDto player) {
        List<PerkStyleDto> perks = player.perks().styles();
        int keystoneId = perks.getFirst().selections().getFirst().perk(); //could be cleaner but works for now
        int subStyle = 0;

        for(PerkStyleDto perk : perks ) {
            if(perk.description().equals("subStyle")) {
                subStyle = perk.style();
                break;
            }
        }
        return new PlayerDisplayInfoDto(
                player.riotIdGameName(),
                player.riotIdTagline(),
                player.championId(),
                player.champLevel(),
                keystoneId,
                subStyle,
                player.summoner1Id(),
                player.summoner2Id(),
                player.kills(),
                player.deaths(),
                player.assists(),
                player.getCreepScore(),
                player.totalDamageDealtToChampions(),
                player.win(),
                List.of(player.item0(), player.item1(), player.item2(), player.item3(), player.item4(), player.item5(), player.item6())
        );
    }
}
