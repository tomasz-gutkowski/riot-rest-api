package com.rra.project.riotrestapi.controller;

import com.rra.project.riotrestapi.exceptions.RestExceptionHandler;
import com.rra.project.riotrestapi.exceptions.code4xx.RateLimitException;
import com.rra.project.riotrestapi.service.ratelimiters.ControllerRateLimiter;
import com.rra.project.riotrestapi.service.riotapi.RiotApiService;
import com.rra.project.riotrestapi.service.riotapi.ServerID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.rra.project.riotrestapi.service.riotapi.ServerID.EUW1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SummonerProfileController.class)
@Import(RestExceptionHandler.class)
class SummonerProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RiotApiService riotApiService;
    @MockitoBean
    private ControllerRateLimiter controllerRateLimiter;
    @MockitoBean
    private CacheManager cacheManager;

    @Nested
    @DisplayName("GET /api/profile/{serverId}/{gameName}/{tagLine}")
    class GetProfileTests {
        private final ServerID serverId = EUW1;
        private final String gameName = "G2 Caps";
        private final String tagLine = "EUW1";

        @Test
        @DisplayName("200 with valid arguments")
        void shouldReturn200WhenValidArguments() throws Exception {
            mockMvc.perform(get("/api/profile/{serverId}/{gameName}/{tagLine}", serverId.name(), gameName, tagLine))
                    .andExpect(status().isOk());

            verify(riotApiService).getProfileResponseDto(serverId, gameName, tagLine );
        }

        @Test
        @DisplayName("400 with invalid server")
        void shouldReturn400WhenInvalidServer() throws Exception {
            String invalidServer = "INVALID_SERVER";

            mockMvc.perform(get("/api/profile/{serverId}/{gameName}/{tagLine}", invalidServer, gameName, tagLine))
                    .andExpect(status().isBadRequest());

            verify(riotApiService, never()).getProfileResponseDto(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("GET /api/matches/{serverId}/{puuid}/{endTime}")
    class GetMatchListTests {
        private final ServerID serverId = ServerID.KR;
        private final String puuid = "puuid-1234";
        private final long endTime = 123456789L;

        @Test
        @DisplayName("200 with valid arguments & default params")
        void shouldReturn200WhenValidArgumentsDefaultParams() throws Exception {
            int defaultStart = 0;
            int defaultCount = 10;
            mockMvc.perform(get("/api/matches/{serverId}/{puuid}/{endTime}", serverId.name(), puuid, endTime))
                    .andDo(print()).andExpect(status().isOk());


            verify(riotApiService).getMatchInfoDtos(serverId, puuid, endTime, defaultStart, defaultCount);
        }

        @Test
        @DisplayName("200 with valid arguments & custom params")
        void shouldReturn200WhenValidArgumentsCustomParams() throws Exception {
            int customStart = 10;
            int customCount = 15;
            mockMvc.perform(get("/api/matches/{serverId}/{puuid}/{endTime}", serverId.name(), puuid, endTime)
                                .param("start", String.valueOf(customStart))
                                .param("count", String.valueOf(customCount)))
                            .andExpect(status().isOk());


            verify(riotApiService).getMatchInfoDtos(serverId, puuid, endTime, customStart, customCount);
        }

        @Test
        @DisplayName("400 with invalid server")
        void shouldReturn400WhenInvalidServer() throws Exception {
            String invalidServer = "INVALID_SERVER";
            mockMvc.perform(get("/api/matches/{serverId}/{puuid}/{endTime}", invalidServer, puuid, endTime))
                    .andExpect(status().isBadRequest());

            verify(riotApiService, never()).getMatchInfoDtos(any(), any(), anyLong(), anyInt(), anyInt());
        }
    }

    @Nested
    @DisplayName("GET /api/match/{serverId}/{matchId}")
    class GetMatchDetailsTests {
        private final ServerID serverId = ServerID.KR;
        private final String matchId = "KR_12345678";

        @Test
        @DisplayName("200 with valid arguments")
        void shouldReturn200WithValidArguments() throws Exception {
            mockMvc.perform(get("/api/match/{serverId}/{matchId}", serverId.name(), matchId))
                    .andExpect(status().isOk());

            verify(riotApiService).getMatchDetailsDto(serverId, matchId);
        }

        @Test
        @DisplayName("400 with invalid server")
        void shouldReturn400WhenInvalidServer() throws Exception {
            String invalidServer = "INVALID_SERVER";
            mockMvc.perform(get("/api/match/{serverId}/{matchId}", invalidServer, matchId))
                    .andExpect(status().isBadRequest());

            verify(riotApiService, never()).getMatchDetailsDto(any(), any());
        }
    }


    @Test
    @DisplayName("Exception Handler")
    void shouldDelegateExceptionToGlobalHandler() throws Exception {
        String exceptionMessage = "Rate Limit Exceeded";
        when(riotApiService.getProfileResponseDto(any(), any(), any()))
                .thenThrow(new RateLimitException(exceptionMessage));

        mockMvc.perform(get("/api/profile/KR/Hide on Bush/KR1"))
                .andExpect(status().isTooManyRequests());
    }
}