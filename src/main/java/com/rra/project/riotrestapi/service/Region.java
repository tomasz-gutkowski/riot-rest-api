package com.rra.project.riotrestapi.service;

public enum Region {
    AMERICAS("https://americas.api.riotgames.com"),
    ASIA("https://asia.api.riotgames.com"),
    EUROPE("https://europe.api.riotgames.com");

    private final String baseUrl;

    Region(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    public String getBaseUrl() {
        return baseUrl;
    }
}
