package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.ParticipantDto;
import com.rra.project.riotrestapi.dto.fetched.PerkStyleDto;
import com.rra.project.riotrestapi.dto.fetched.PerksDto;
import com.rra.project.riotrestapi.service.datadragon.DataDragonService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record PlayerDisplayInfoDto(
        String gameName,
        String tagline,
        String championName,
        int level,
        PerksInfo perks,
        List<IdNamePair> summonerSpells,
        int kills,
        int deaths,
        int assists,
        int creepScore,
        int totalDamageDealtToChampions,
        boolean win,
        int teamId,
        List<IdNamePair> items
)
{
    public static PlayerDisplayInfoDto from(ParticipantDto player, DataDragonService dataDragonService) {
        List<PerkStyleDto> perks = player.perks().styles();
        int keystoneId = perks.getFirst().selections().getFirst().perk(); //could be cleaner but works for now
        int primaryStyleId = -1;
        int subStyleId = -1;

        for(PerkStyleDto perk : perks ) {
            if(perk.description().equals("primaryStyle")) {
                primaryStyleId = perk.style();
            }
            if(perk.description().equals("subStyle")) {
                subStyleId = perk.style();
            }
        }

        IdNamePair keystone = new IdNamePair(keystoneId, dataDragonService.getRuneName(keystoneId));
        IdNamePair primaryStyle = new IdNamePair(primaryStyleId, dataDragonService.getRuneName(primaryStyleId));
        IdNamePair subStyle = new IdNamePair(subStyleId, dataDragonService.getRuneName(subStyleId));

        PerksInfo p = new PerksInfo(keystone, primaryStyle, subStyle);
        return new PlayerDisplayInfoDto(
                player.riotIdGameName(),
                player.riotIdTagline(),
                player.championName(),
                player.champLevel(),
                p,
                List.of(
                        new IdNamePair(player.summoner1Id(), dataDragonService.getSummonerSpellName(player.summoner1Id())),
                        new IdNamePair(player.summoner2Id(), dataDragonService.getSummonerSpellName(player.summoner2Id()))
                ),
                player.kills(),
                player.deaths(),
                player.assists(),
                player.getCreepScore(),
                player.totalDamageDealtToChampions(),
                player.win(),
                player.teamId(),
                List.of(
                        new IdNamePair(player.item0(), dataDragonService.getItemName(player.item0())),
                        new IdNamePair(player.item1(), dataDragonService.getItemName(player.item1())),
                        new IdNamePair(player.item2(), dataDragonService.getItemName(player.item2())),
                        new IdNamePair(player.item3(), dataDragonService.getItemName(player.item3())),
                        new IdNamePair(player.item4(), dataDragonService.getItemName(player.item4())),
                        new IdNamePair(player.item5(), dataDragonService.getItemName(player.item5())),
                        new IdNamePair(player.item6(), dataDragonService.getItemName(player.item6())))
        );
    }

    public record PerksInfo(
            IdNamePair keystone,
            IdNamePair primaryStyle,
            IdNamePair subStyle
    ){}

    public record IdNamePair(
            int id,
            String name
    ){}
}
