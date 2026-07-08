package com.rra.project.riotrestapi.service.datadragon;


import com.rra.project.riotrestapi.service.datadragon.datatypes.AugmentData;
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
    private volatile HashMap<Integer, NameImagePair> runeNames = new HashMap<>();
    private volatile HashMap<Integer, NameImagePair> summonerSpellNames = new HashMap<>();
    private volatile HashMap<Integer, AugmentData> augmentData = new HashMap<>();
    private volatile HashMap<Integer, MapModeNamePair> gameModes = new HashMap<>();
    private volatile HashMap<Integer, ChampIdNamePair> championNames = new HashMap<>();

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
        HashMap<Integer, NameImagePair> result = new HashMap<>();

        for(var style : json){
            int k1 = style.path("id").intValue();
            String v01 = style.path("name").asString();
            String v02 = style.path("icon").asString();
            result.put(k1, new NameImagePair(v01, v02));
            var slots = style.path("slots");
            for(var slot : slots){
                var runes = slot.path("runes");
                for(var rune : runes){
                    int k2 = rune.path("id").intValue();
                    String v11 = rune.path("name").asString();
                    String v12 = rune.path("icon").asString();
                    result.put(k2, new NameImagePair(v11, v12));
                }
            }
        }

        this.runeNames = result;
    }

    private void updateSummonerSpells(JsonNode json){
        HashMap<Integer, NameImagePair> result = new HashMap<>();

        Set<Map.Entry<String, JsonNode>> data = json.path("data").properties();
        for(var entry : data){
            int k = Integer.parseInt(entry.getValue().path("key").asString());
            String v1 = entry.getValue().path("name").asString();
            String v2 = entry.getValue().path("image").path("full").asString();
            result.put(k, new NameImagePair(v1, v2));
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
        //Arena 3x8 and Custom are currently missing from static.dev json, leaving it hardcoded for now
        result.computeIfAbsent(1750, (k) -> new MapModeNamePair("Rings of Wrath", "Arena 3v3"));
        result.computeIfAbsent(3130, (k) -> new MapModeNamePair("", "Custom"));
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
        HashMap<Integer, ChampIdNamePair> result = new HashMap<>();

        JsonNode data = json.path("data");
        for(var champion : data){
            int k = Integer.parseInt(champion.path("key").asString());
            String v1 = champion.path("id").asString();
            String v2 = champion.path("name").asString();
            result.put(k, new ChampIdNamePair(v1, v2));
        }
        this.championNames = result;
    }



    public String getItemName(int id){
        return this.itemNames.get(id);
    }

    public NameImagePair getRuneName(int id){
        return this.runeNames.get(id);
    }

    public NameImagePair getSummonerSpellName(int id){
        return this.summonerSpellNames.get(id);
    }

    public MapModeNamePair getGameModeName(int id){
        return this.gameModes.get(id);
    }

    public AugmentData getAugmentData(int id){ return this.augmentData.get(id) != null ? this.augmentData.get(id) : new AugmentData(0, "empty", "noIcon", "noIcon"); }

    public ChampIdNamePair getChampionName(int id){ return this.championNames.get(id); }

    public String getLatestVersion(){
        return dataDragonClient.getCurrentVersion();
    }



    public record MapModeNamePair(
             String map,
             String modeName
    ){}

    public record NameImagePair(
            String name,
            String image
    ){}

    public record ChampIdNamePair(
            String id,
            String name
    ){}
}
