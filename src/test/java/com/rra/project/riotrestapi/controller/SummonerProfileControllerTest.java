package com.rra.project.riotrestapi.controller;

import com.rra.project.riotrestapi.dto.TestDtoFactory;
import com.rra.project.riotrestapi.exceptions.code4xx.ClientException;
import com.rra.project.riotrestapi.exceptions.code4xx.ResourceNotFoundException;
import com.rra.project.riotrestapi.service.ServerID;
import com.rra.project.riotrestapi.service.SummonerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import static org.mockito.Mockito.when;

@WebMvcTest(SummonerProfileController.class)
class SummonerProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    SummonerService summonerService;


    @Test
    void shouldReturn404WhenSummonerNotFound() throws Exception {
        when(summonerService.getProfileResponseDto(any(), any(), any()))
                .thenThrow(new ResourceNotFoundException("Cannot find summoner"));

        mockMvc.perform(get("/summoner/{serverId}/{gameName}-{tagLine}", TestDtoFactory.serverId, TestDtoFactory.gameName, TestDtoFactory.tagLine))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnSummonerProfileDataForSingleQueue() throws Exception {
        when(summonerService.getProfileResponseDto(eq(TestDtoFactory.serverId), eq(TestDtoFactory.gameName), eq(TestDtoFactory.tagLine)))
                .thenReturn(TestDtoFactory.singleQueueRankedProfile());

        mockMvc.perform(get("/summoner/{serverId}/{gameName}-{tagLine}", TestDtoFactory.serverId, TestDtoFactory.gameName, TestDtoFactory.tagLine))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summoner.gameName").value(TestDtoFactory.gameName))
                .andExpect(jsonPath("$.summoner.tagLine").value(TestDtoFactory.tagLine))
                .andExpect(jsonPath("$.leagues[0]").exists())
                .andExpect(jsonPath("$.leagues[0]").isNotEmpty());
    }

    @Test
    void shouldReturnSummonerProfileDataForMultipleQueues() throws Exception {
        when(summonerService.getProfileResponseDto(eq(TestDtoFactory.serverId), eq(TestDtoFactory.gameName), eq(TestDtoFactory.tagLine)))
                .thenReturn(TestDtoFactory.multipleQueueRankedProfile());

        mockMvc.perform(get("/summoner/{serverId}/{gameName}-{tagLine}",  TestDtoFactory.serverId, TestDtoFactory.gameName, TestDtoFactory.tagLine))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summoner.gameName").value(TestDtoFactory.gameName))
                .andExpect(jsonPath("$.summoner.tagLine").value(TestDtoFactory.tagLine))
                .andExpect(jsonPath("$.leagues[0]").exists())
                .andExpect(jsonPath("$.leagues[0]").isNotEmpty())
                .andExpect(jsonPath("$.leagues[1]").exists())
                .andExpect(jsonPath("$.leagues[1]").isNotEmpty());
    }

    @Test
    void shouldReturnSummonerProfileDataForUnranked() throws Exception {
        when(summonerService.getProfileResponseDto(eq(TestDtoFactory.serverId), eq(TestDtoFactory.gameName), eq(TestDtoFactory.tagLine)))
                .thenReturn(TestDtoFactory.unrankedQueueRankedProfile());

        mockMvc.perform(get("/summoner/{serverId}/{gameName}-{tagLine}",  TestDtoFactory.serverId, TestDtoFactory.gameName, TestDtoFactory.tagLine))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summoner.gameName").value(TestDtoFactory.gameName))
                .andExpect(jsonPath("$.summoner.tagLine").value(TestDtoFactory.tagLine))
                .andExpect(jsonPath("$.leagues").isEmpty());
    }

    @Test
    void shouldReturnSummonersMatchListWithDefaultParams() throws Exception {
        int defaultStart = 0;
        int defaultCount = 20;
        when(summonerService.getMatchInfoDtos(eq(TestDtoFactory.serverId), eq(TestDtoFactory.gameName), eq(TestDtoFactory.tagLine), eq(defaultStart), eq(defaultCount)))
                .thenReturn(TestDtoFactory.matchInfoListStartingFromOfSize(defaultStart, defaultCount));

        mockMvc.perform(get("/api/matches/{serverId}/{gameName}-{tagLine}",  TestDtoFactory.serverId, TestDtoFactory.gameName, TestDtoFactory.tagLine)
                        .param("start", String.valueOf(defaultStart))
                        .param("count", String.valueOf(defaultCount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(defaultCount))
                .andExpect(jsonPath("$[0].matchId").value("GAME"+defaultStart))
                .andExpect(jsonPath("$["+(defaultCount-1)+"].matchId").value("GAME"+(defaultStart+defaultCount-1)));
    }

    @Test
    void shouldReturnSummonersMatchListWithCustomParams() throws Exception {
        int defaultStart = 15;
        int defaultCount = 30;
        when(summonerService.getMatchInfoDtos(eq(TestDtoFactory.serverId), eq(TestDtoFactory.gameName), eq(TestDtoFactory.tagLine), eq(defaultStart), eq(defaultCount)))
                .thenReturn(TestDtoFactory.matchInfoListStartingFromOfSize(defaultStart, defaultCount));

        mockMvc.perform(get("/api/matches/{serverId}/{gameName}-{tagLine}",  TestDtoFactory.serverId, TestDtoFactory.gameName, TestDtoFactory.tagLine)
                        .param("start", String.valueOf(defaultStart))
                        .param("count", String.valueOf(defaultCount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(defaultCount))
                .andExpect(jsonPath("$[0].matchId").value("GAME"+defaultStart))
                .andExpect(jsonPath("$["+(defaultCount-1)+"].matchId").value("GAME"+(defaultStart+defaultCount-1)));
    }

}