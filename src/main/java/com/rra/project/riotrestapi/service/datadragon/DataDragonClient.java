package com.rra.project.riotrestapi.service.datadragon;

import com.rra.project.riotrestapi.exceptions.code4xx.*;
import com.rra.project.riotrestapi.exceptions.code5xx.BadGatewayException;
import com.rra.project.riotrestapi.exceptions.code5xx.GatewayTimeoutException;
import com.rra.project.riotrestapi.exceptions.code5xx.InternalServerErrorException;
import com.rra.project.riotrestapi.exceptions.code5xx.ServiceUnavailableException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;

@Component
public class DataDragonClient {

    private static final String VERSION_URI = "/api/versions.json";

    private final RestClient dataDragonClient;
    private final RestClient staticDeveloperClient;
    private final RestClient communityDragonClient;

    private volatile String currentVersion;
    private String augmentsEtag;

    public DataDragonClient() {
        this.dataDragonClient = RestClient.builder().
                baseUrl("https://ddragon.leagueoflegends.com").
                build();

        this.staticDeveloperClient = RestClient.builder()
                .baseUrl("https://static.developer.riotgames.com")
                .build();

        this.communityDragonClient = RestClient.builder().
                baseUrl("https://raw.communitydragon.org").
                build();
    }

    public String getCurrentVersion(){
        return currentVersion;
    }

    public boolean checkAndUpdateVersion(){
        ArrayList<String> versions = request(VERSION_URI, dataDragonClient).body(new ParameterizedTypeReference<ArrayList<String>>(){});
        if(versions != null && !versions.isEmpty()){
            String newVersion = versions.getFirst();
            if(currentVersion != null && currentVersion.equals(newVersion)){
                return false;
            }
            else{
                this.currentVersion = versions.getFirst();
                return true;
            }
        }
        return false;
    }

    public boolean isNewCommunityDragonVersion(){
        RestClient.RequestHeadersSpec<?> request = communityDragonClient.get()
                .uri("/latest/cdragon/arena/en_us.json");

        if (augmentsEtag != null) {
            request.header("If-None-Match", augmentsEtag);
        }

        ResponseEntity<JsonNode> response = request.retrieve().toEntity(JsonNode.class);

        return response.getStatusCode().value() != 304;
    }

    public JsonNode fetchItems(){
        String uri = "/cdn/"+currentVersion+"/data/en_US/item.json";
        return request(uri, dataDragonClient).body(JsonNode.class);
    }

    public JsonNode fetchRunes(){
        String uri = "/cdn/"+currentVersion+"/data/en_US/runesReforged.json";
        return request(uri, dataDragonClient).body(JsonNode.class);
    }

    public JsonNode fetchSummonerSpells(){
        String uri = "/cdn/"+currentVersion+"/data/en_US/summoner.json";
        return request(uri, dataDragonClient).body(JsonNode.class);
    }

    public JsonNode fetchGameModes(){
        String uri = "/docs/lol/queues.json";
        return request(uri, staticDeveloperClient).body(JsonNode.class);
    }

    public JsonNode fetchAugments(){
        String uri = "/latest/cdragon/arena/en_us.json";
        return request(uri, communityDragonClient).body(JsonNode.class);
    }

    public JsonNode fetchChampions(){
        String uri = "/cdn/"+currentVersion+"/data/en_US/champion.json";
        return request(uri, dataDragonClient).body(JsonNode.class);
    }

    public RestClient.ResponseSpec request(String uri, RestClient client){
        return client.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) ->{
                    switch(response.getStatusCode().value()){
                        case 400 -> throw new BadRequestException("Bad request");
                        case 401 -> throw new UnauthorizedException("Unauthorized");
                        case 403 -> throw new ForbiddenException("Forbidden");
                        case 404 -> throw new ResourceNotFoundException("Resource not found");
                        case 405 -> throw new MethodNotAllowedException("Method not allowed");
                        case 415 -> throw new UnsupportedMediaTypeException("Unsupported media type");
                        case 429 -> throw new RateLimitException("Rate limit exceeded");
                        default  -> throw new BadRequestException("Client error: " + response.getStatusCode());
                    }
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) ->{
                    switch(response.getStatusCode().value()){
                        case 500 -> throw new InternalServerErrorException("Internal server error");
                        case 502 -> throw new BadGatewayException("Bad gateway");
                        case 503 -> throw new ServiceUnavailableException("Service unavailable");
                        case 504 -> throw new GatewayTimeoutException("Gateway timeout");
                        default  -> throw new InternalServerErrorException("Server error: " + response.getStatusCode());
                    }
                });
    }
}
