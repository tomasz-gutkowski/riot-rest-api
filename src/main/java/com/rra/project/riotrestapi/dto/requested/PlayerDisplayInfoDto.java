package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.InfoDto;
import com.rra.project.riotrestapi.dto.fetched.ParticipantDto;
import com.rra.project.riotrestapi.dto.fetched.PerkStyleDto;
import com.rra.project.riotrestapi.dto.requested.common.IdNamePair;
import com.rra.project.riotrestapi.service.datadragon.DataDragonService;

import java.util.List;
import java.util.Objects;

public record PlayerDisplayInfoDto(
        String gameName,
        String tagline,
        String championName,
        int level,
        List<IdNameImage> summonerSpells,
        int kills,
        int deaths,
        int assists,
        int totalDamageDealtToChampions,
        List<IdNamePair> items,
        ModeData modeData
)
{
    public static PlayerDisplayInfoDto from(ParticipantDto player, InfoDto gameInfo, DataDragonService dataDragonService) {
        ModeData modeData;

        if(gameInfo.gameMode().equals("CHERRY")){
            modeData = new ArenaModeData(
                    player.playerSubteamId(),
                    player.subteamPlacement(),
                    List.of(
                            new IdNamePair(player.playerAugment1(), dataDragonService.getAugmentName(player.playerAugment1())),
                            new IdNamePair(player.playerAugment2(), dataDragonService.getAugmentName(player.playerAugment2())),
                            new IdNamePair(player.playerAugment3(), dataDragonService.getAugmentName(player.playerAugment3())),
                            new IdNamePair(player.playerAugment4(), dataDragonService.getAugmentName(player.playerAugment4())),
                            new IdNamePair(player.playerAugment5(), dataDragonService.getAugmentName(player.playerAugment5())),
                            new IdNamePair(player.playerAugment6(), dataDragonService.getAugmentName(player.playerAugment6()))
                    )
            );
        } else {
            List<PerkStyleDto> perks = player.perks().styles();
            int keystoneId = perks.getFirst().selections().getFirst().perk();
            int primaryStyleId = -1;
            int subStyleId = -1;

            for (PerkStyleDto perk : perks) {
                if (perk.description().equals("primaryStyle")) {
                    primaryStyleId = perk.style();
                }
                if (perk.description().equals("subStyle")) {
                    subStyleId = perk.style();
                }
            }

            IdNameImage keystone = new IdNameImage(keystoneId, dataDragonService.getRuneName(keystoneId));
            IdNameImage primaryStyle = new IdNameImage(primaryStyleId, dataDragonService.getRuneName(primaryStyleId));
            IdNameImage subStyle = new IdNameImage(subStyleId, dataDragonService.getRuneName(subStyleId));

            PerksInfo playerPerks = new PerksInfo(keystone, primaryStyle, subStyle);

            String side = player.teamId() == 100 ? "BLUE" : player.teamId() == 200 ? "RED" : null;
            modeData = new DefaultModeData(
                    player.win(),
                    playerPerks,
                    player.getCreepScore(),
                    side,
                    player.teamPosition()
            );
        }
        return new PlayerDisplayInfoDto(
                player.riotIdGameName(),
                player.riotIdTagline(),
                player.championName(),
                player.champLevel(),
                List.of(
                        new IdNameImage(player.summoner1Id(), dataDragonService.getSummonerSpellName(player.summoner1Id())),
                        new IdNameImage(player.summoner2Id(), dataDragonService.getSummonerSpellName(player.summoner2Id()))
                ),
                player.kills(),
                player.deaths(),
                player.assists(),
                player.totalDamageDealtToChampions(),
                List.of(
                        new IdNamePair(player.item0(), dataDragonService.getItemName(player.item0())),
                        new IdNamePair(player.item1(), dataDragonService.getItemName(player.item1())),
                        new IdNamePair(player.item2(), dataDragonService.getItemName(player.item2())),
                        new IdNamePair(player.item3(), dataDragonService.getItemName(player.item3())),
                        new IdNamePair(player.item4(), dataDragonService.getItemName(player.item4())),
                        new IdNamePair(player.item5(), dataDragonService.getItemName(player.item5())),
                        new IdNamePair(player.item6(), dataDragonService.getItemName(player.item6())),
                        new IdNamePair(player.roleBoundItem(), dataDragonService.getItemName(player.roleBoundItem()))
                ),
                modeData
        );
    }

    public record DefaultModeData(
            boolean win,
            PerksInfo perks,
            int creepScore,
            String side,
            String position
    ) implements ModeData {}

    public record ArenaModeData(
            int teamId,
            int teamPlacement,
            List<IdNamePair> augments
    ) implements ModeData {}

    public record PerksInfo(
            IdNameImage keystone,
            IdNameImage primaryStyle,
            IdNameImage subStyle
    ){}

    public record IdNameImage(
            int id,
            DataDragonService.NameImagePair nameImage
    ){}

    public sealed interface ModeData permits DefaultModeData, ArenaModeData{};
}
