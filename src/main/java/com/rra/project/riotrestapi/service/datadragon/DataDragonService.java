package com.rra.project.riotrestapi.service.datadragon;


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

    private volatile HashMap<Integer, String> itemNames = new HashMap<>();
    private volatile HashMap<Integer, String> runeNames = new HashMap<>();
    private volatile HashMap<Integer, String> summonerSpellNames = new HashMap<>();
    private volatile HashMap<Integer, String> augmentNames = new HashMap<>();
    private volatile HashMap<Integer, MapModeNamePair> gameModes = new HashMap<>();

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
    }


    private void updateCDragonMappings(){
        JsonNode augments = dataDragonClient.fetchAugments();
        updateAugments(augments);
    }

    private void updateItems(JsonNode json){
        HashMap<Integer, String> result = new HashMap<>();

        Set<Map.Entry<String, JsonNode>> data = json.path("data").properties();

        for(var entry : data){
            int k = Integer.parseInt(entry.getKey());
            String v = entry.getValue().path("name").asString();
            result.put(k, v);
        }

        this.itemNames = result;
    }

    private void updateRunes(JsonNode json){
        HashMap<Integer, String> result = new HashMap<>();

        for(var style : json){
            int k1 = style.path("id").intValue();
            String v1 = style.path("name").asString();
            result.put(k1, v1);
            var slots = style.path("slots");
            for(var slot : slots){
                var runes = slot.path("runes");
                for(var rune : runes){
                    int k2 = rune.path("id").intValue();
                    String v2 = rune.path("name").asString();
                    result.put(k2, v2);
                }
            }
        }

        this.runeNames = result;
    }

    private void updateSummonerSpells(JsonNode json){
        HashMap<Integer, String> result = new HashMap<>();

        Set<Map.Entry<String, JsonNode>> data = json.path("data").properties();
        for(var entry : data){
            int k = Integer.parseInt(entry.getValue().path("key").asString());
            String v = entry.getValue().path("name").asString();
            result.put(k, v);
        }
        this.summonerSpellNames = result;
    }

    private void updateGameModes(JsonNode json){
        HashMap<Integer, MapModeNamePair> result = new HashMap<>();

        for(var gameMode : json){
            int k = gameMode.path("queueId").intValue();
            String map =  gameMode.path("map").asString();
            String mode = gameMode.path("description").asString();
            if(mode.endsWith(" games")) mode = mode.substring(0, mode.length()-" games".length());
            var v = new MapModeNamePair(map, mode);
            result.put(k, v);
        }

        this.gameModes = result;
    }

    private void updateAugments(JsonNode json){
        HashMap<Integer, String> result = new HashMap<>();

        JsonNode augments = json.path("augments");
        for(var augment : augments){
            int k = augment.path("id").intValue();
            String v = augment.path("name").asString();
            result.put(k, v);
        }
        this.augmentNames = result;
    }

    public String getItemName(int id){
        return this.itemNames.get(id);
    }

    public String getRuneName(int id){
        return this.runeNames.get(id);
    }

    public String getSummonerSpellName(int id){
        return this.summonerSpellNames.get(id);
    }

    public MapModeNamePair getGameModeName(int id){
        return this.gameModes.get(id);
    }

    public String getAugmentName(int id){ return this.augmentNames.get(id); }



    public record MapModeNamePair(
             String map,
             String modeName
    ){}
}
