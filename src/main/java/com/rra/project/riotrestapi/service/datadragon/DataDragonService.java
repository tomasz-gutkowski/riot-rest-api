package com.rra.project.riotrestapi.service.datadragon;


import com.rra.project.riotrestapi.service.datadragon.datatypes.*;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class DataDragonService {

    private final DataDragonClient dataDragonClient;

    private volatile HashMap<Integer, IdNamePair> itemNames = new HashMap<>();
    private volatile HashMap<Integer, IdNameImageData> runeNames = new HashMap<>();
    private volatile HashMap<Integer, IdNameImageData> summonerSpellNames = new HashMap<>();
    private volatile HashMap<Integer, AugmentData> augmentData = new HashMap<>();
    private volatile HashMap<Integer, GameModeData> gameModes = new HashMap<>();
    private volatile HashMap<Integer, ChampionData> championData = new HashMap<>();

    public DataDragonService(DataDragonClient dataDragonClient) {
        this.dataDragonClient = dataDragonClient;
    }

    @PostConstruct // after dependency injection
    @Scheduled(cron = "0 0 6 * * *") // every day at 6:00
    public void refreshDDragonVersion(){
        boolean isNewDDragonVersion = dataDragonClient.checkAndUpdateVersion();
        boolean isNewCDragonVersion = dataDragonClient.isNewCommunityDragonVersion();
        if(isNewDDragonVersion){
            updateDDragonMappings();
        }
        if(isNewCDragonVersion){
            updateCDragonMappings();
        }
    }

    private void updateDDragonMappings(){
        JsonNode items = dataDragonClient.fetchItems();
        updateItems(items);

        JsonNode runes = dataDragonClient.fetchRunes();
        updateRunes(runes);

        JsonNode summonerSpells = dataDragonClient.fetchSummonerSpells();
        updateSummonerSpells(summonerSpells);

        JsonNode gameModes = dataDragonClient.fetchGameModes();
        updateGameModes(gameModes);

        JsonNode champions = dataDragonClient.fetchChampions();
        updateChampions(champions);
    }


    private void updateCDragonMappings(){
        JsonNode augments = dataDragonClient.fetchAugments();
        updateAugments(augments);
    }

    private void updateItems(JsonNode json){
        HashMap<Integer, IdNamePair> result = new HashMap<>();

        Set<Map.Entry<String, JsonNode>> data = json.path("data").properties();

        for(var entry : data){
            int k = Integer.parseInt(entry.getKey());
            String v = entry.getValue().path("name").asString();
            result.put(k, new IdNamePair(k,v));
        }

        this.itemNames = result;
    }

    private void updateRunes(JsonNode json){
        HashMap<Integer, IdNameImageData> result = new HashMap<>();

        for(var style : json){
            int k1 = style.path("id").intValue();
            String v11 = style.path("name").asString();
            String v12 = style.path("icon").asString();
            result.put(k1, new IdNameImageData(k1, v11, v12));
            var slots = style.path("slots");
            for(var slot : slots){
                var runes = slot.path("runes");
                for(var rune : runes){
                    int k2 = rune.path("id").intValue();
                    String v21 = rune.path("name").asString();
                    String v22 = rune.path("icon").asString();
                    result.put(k2, new IdNameImageData(k2 ,v21, v22));
                }
            }
        }

        this.runeNames = result;
    }

    private void updateSummonerSpells(JsonNode json){
        HashMap<Integer, IdNameImageData> result = new HashMap<>();

        Set<Map.Entry<String, JsonNode>> data = json.path("data").properties();
        for(var entry : data){
            int k = Integer.parseInt(entry.getValue().path("key").asString());
            String v1 = entry.getValue().path("name").asString();
            String v2 = entry.getValue().path("image").path("full").asString();
            result.put(k, new IdNameImageData(k, v1, v2));
        }
        this.summonerSpellNames = result;
    }

    private void updateGameModes(JsonNode json){
        HashMap<Integer, GameModeData> result = new HashMap<>();

        for(var gameMode : json){
            int k = gameMode.path("queueId").intValue();
            String map =  gameMode.path("map").asString();
            String mode = gameMode.path("description").asString();
            if(mode.endsWith(" games")) mode = mode.substring(0, mode.length()-" games".length());
            var v = new GameModeData(k, map, mode);
            result.put(k, v);
        }

        //Arena 3x8,Custom and Ranked 5s are currently missing from static.dev json, leaving it hardcoded for now
        result.computeIfAbsent(710, (k) -> new GameModeData(710, "Summoners Rift", "Ranked 5s"));
        result.computeIfAbsent(1750, (k) -> new GameModeData(1750,"Rings of Wrath", "Arena 3v3"));
        result.computeIfAbsent(3130, (k) -> new GameModeData(3130,"", "Custom"));

        this.gameModes = result;
    }



    private void updateAugments(JsonNode json){
        HashMap<Integer, AugmentData> result = new HashMap<>();

        JsonNode augments = json.path("augments");
        for(var augment : augments){
            int k = augment.path("id").intValue();
            String v1 = augment.path("name").asString();
            String v2 = augment.path("iconLarge").asString();
            String v3 = augment.path("iconSmall").asString();
            result.put(k, new AugmentData(k, v1, v2, v3));
        }
        this.augmentData = result;
    }

    public void updateChampions(JsonNode json){
        HashMap<Integer, ChampionData> result = new HashMap<>();

        JsonNode data = json.path("data");
        for(var champion : data){
            int k = Integer.parseInt(champion.path("key").asString());
            String v1 = champion.path("id").asString();
            String v2 = champion.path("name").asString();
            result.put(k, new ChampionData(k, v1, v2));
        }
        this.championData = result;
    }



    public IdNamePair getItemName(int id){
        return this.itemNames.containsKey(id) ? this.itemNames.get(id) : new IdNamePair(-1, "unknown");
    }

    public IdNameImageData getRuneName(int id){return this.runeNames.containsKey(id) ? this.runeNames.get(id) : new IdNameImageData(-1, "unknown", "unknown");}

    public IdNameImageData getSummonerSpellName(int id){return this.summonerSpellNames.containsKey(id) ? this.summonerSpellNames.get(id) : new IdNameImageData(-1, "unknown", "unknown");}

    public GameModeData getGameModeName(int id){
        return this.gameModes.containsKey(id) ? this.gameModes.get(id) : new GameModeData(-1, "unknown", "unknown");
    }

    public AugmentData getAugmentData(int id){ return this.augmentData.containsKey(id)  ? this.augmentData.get(id) : new AugmentData(-1, "unknown", "unknown", "unknown"); }

    public ChampionData getChampionData(int id){ return this.championData.containsKey(id) ? this.championData.get(id) : new ChampionData(-1, "unknown", "unknown");}

    public String getLatestVersion(){
        return dataDragonClient.getCurrentVersion();
    }

}
