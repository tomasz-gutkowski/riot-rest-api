package com.rra.project.riotrestapi.dto.requested;

import com.rra.project.riotrestapi.dto.fetched.InfoDto;
import com.rra.project.riotrestapi.dto.fetched.ParticipantDto;
import com.rra.project.riotrestapi.dto.fetched.PerkStyleDto;
import com.rra.project.riotrestapi.service.datadragon.datatypes.IdNamePair;
import com.rra.project.riotrestapi.service.datadragon.DataDragonService;
import com.rra.project.riotrestapi.service.datadragon.datatypes.AugmentData;
import com.rra.project.riotrestapi.service.datadragon.datatypes.ChampionData;
import com.rra.project.riotrestapi.service.datadragon.datatypes.IdNameImageData;

import java.util.List;

public record PlayerDisplayInfoDto(
        String gameName,
        String tagline,
        ChampionData championData,
        int level,
        List<IdNameImageData> summonerSpells,
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
                            dataDragonService.getAugmentData(player.playerAugment1()),
                            dataDragonService.getAugmentData(player.playerAugment2()),
                            dataDragonService.getAugmentData(player.playerAugment3()),
                            dataDragonService.getAugmentData(player.playerAugment4()),
                            dataDragonService.getAugmentData(player.playerAugment5()),
                            dataDragonService.getAugmentData(player.playerAugment6())
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

            IdNameImageData keystone = dataDragonService.getRuneName(keystoneId);
            IdNameImageData primaryStyle = dataDragonService.getRuneName(primaryStyleId);
            IdNameImageData subStyle = dataDragonService.getRuneName(subStyleId);

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
                dataDragonService.getChampionData(player.championId()),
                player.champLevel(),
                List.of(
                        dataDragonService.getSummonerSpellName(player.summoner1Id()),
                        dataDragonService.getSummonerSpellName(player.summoner2Id())
                ),
                player.kills(),
                player.deaths(),
                player.assists(),
                player.totalDamageDealtToChampions(),
                List.of(
                        dataDragonService.getItemName(player.item0()),
                        dataDragonService.getItemName(player.item1()),
                        dataDragonService.getItemName(player.item2()),
                        dataDragonService.getItemName(player.item3()),
                        dataDragonService.getItemName(player.item4()),
                        dataDragonService.getItemName(player.item5()),
                        dataDragonService.getItemName(player.item6()),
                        dataDragonService.getItemName(player.roleBoundItem() == null ? 0 : player.roleBoundItem())
                        //temp fix for games that took place before season16 (treats roleBoundItem as empty slot)
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
            List<AugmentData> augments
    ) implements ModeData {}

    public record PerksInfo(
            IdNameImageData keystone,
            IdNameImageData primaryStyle,
            IdNameImageData subStyle
    ){}

    public sealed interface ModeData permits DefaultModeData, ArenaModeData {};
}
