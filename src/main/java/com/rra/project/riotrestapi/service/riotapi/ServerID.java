package com.rra.project.riotrestapi.service.riotapi;


public enum ServerID {
    //AMERICAS
    BR1("https://br1.api.riotgames.com", Region.AMERICAS),
    LA1("https://la1.api.riotgames.com", Region.AMERICAS),
    LA2("https://la2.api.riotgames.com", Region.AMERICAS),
    NA1("https://na1.api.riotgames.com", Region.AMERICAS),
    OC1("https://oc1.api.riotgames.com", Region.AMERICAS),
    //ASIA
    JP1("https://jp1.api.riotgames.com", Region.ASIA),
    KR("https://kr.api.riotgames.com", Region.ASIA),
    SG2("https://sg2.api.riotgames.com", Region.ASIA),
    TW2("https://tw2.api.riotgames.com", Region.ASIA),
    VN2("https://vn2.api.riotgames.com", Region.ASIA),
    //EUROPE
    EUN1("https://eun1.api.riotgames.com", Region.EUROPE),
    EUW1("https://euw1.api.riotgames.com", Region.EUROPE),
    ME1("https://me1.api.riotgames.com", Region.EUROPE),
    RU("https://ru.api.riotgames.com", Region.EUROPE),
    TR1("https://tr1.api.riotgames.com", Region.EUROPE);

    private final String baseUrl;
    private final Region region;

    ServerID(String baseUrl, Region region) {
        this.baseUrl = baseUrl;
        this.region = region;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Region getRegion() {
        return region;
    }
}
