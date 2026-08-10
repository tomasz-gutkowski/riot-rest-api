package com.rra.project.riotrestapi.service.riotapi;

import com.rra.project.riotrestapi.dto.fetched.AccountDto;
import com.rra.project.riotrestapi.dto.fetched.LeagueEntryDto;
import com.rra.project.riotrestapi.dto.fetched.MatchDto;
import com.rra.project.riotrestapi.dto.fetched.SummonerDto;
import com.rra.project.riotrestapi.dto.requested.ProfileResponseDto;
import com.rra.project.riotrestapi.exceptions.code4xx.BadRequestException;
import com.rra.project.riotrestapi.exceptions.code4xx.RateLimitException;
import com.rra.project.riotrestapi.exceptions.code4xx.ResourceNotFoundException;
import com.rra.project.riotrestapi.service.datadragon.DataDragonService;
import com.rra.project.riotrestapi.support.TestDtoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiotApiServiceTest {

    @Mock
    private RiotApiClient riotApiClient;

    @Mock
    private DataDragonService dataDragonService;

    @InjectMocks
    private RiotApiService riotApiService;

    @Nested
    @DisplayName("getProfileResponseDto")
    class GetProfileResponseDtoTest {
        @Test
        @DisplayName("Returns valid response DTO")
        void shouldReturnProfileResponseDto() {
            ServerID serverID = ServerID.KR;
            String gameName = "gameName";
            String tagLine = "tagLine";

            AccountDto account = TestDtoFactory.createAccountDto("puuid-1234");
            String puuid = account.puuid();
            SummonerDto summoner = TestDtoFactory.createSummonerDto(puuid);
            LeagueEntryDto[] leagueEntryDtoArr = TestDtoFactory.createLeagueEntryDtoArr(puuid);

            when(riotApiClient.callForAccountDto(serverID, gameName, tagLine)).thenReturn(account);

            when(riotApiClient.callForSummonerDto(serverID, puuid)).thenReturn(summoner);
            when(riotApiClient.callForLeagueEntryDtoArr(serverID, puuid)).thenReturn(leagueEntryDtoArr);

            ProfileResponseDto result = riotApiService.getProfileResponseDto(serverID, gameName, tagLine);

            assertEquals(account.gameName(), result.player().gameName());
            assertEquals(summoner.summonerLevel(), result.player().summonerLevel());
            assertEquals(leagueEntryDtoArr.length, result.leagues().length);

            verify(riotApiClient).callForAccountDto(serverID, gameName, tagLine);
            verify(riotApiClient).callForSummonerDto(serverID, puuid);
            verify(riotApiClient).callForLeagueEntryDtoArr(serverID, puuid);
        }

        @Test
        @DisplayName("Stops further method calls after an exception on 1st method")
        void shouldStopFurtherMethodCallsAfterAnExceptionOnFirstMethod() {
            ServerID serverID = ServerID.KR;
            String gameName = "gameName";
            String tagLine = "tagLine";

            when(riotApiClient.callForAccountDto(serverID, gameName, tagLine))
                    .thenThrow(new ResourceNotFoundException("Resource not found"));

            assertThrows(ResourceNotFoundException.class, () -> riotApiService.getProfileResponseDto(serverID, gameName, tagLine));

            verify(riotApiClient).callForAccountDto(serverID, gameName, tagLine);
            verify(riotApiClient, never()).callForSummonerDto(any(), any());
            verify(riotApiClient, never()).callForLeagueEntryDtoArr(any(), any());
        }

        @Test
        @DisplayName("Stops further method calls after an exception on 2nd method")
        void shouldStopFurtherMethodCallsAfterAnExceptionOnSecondMethod() {
            ServerID serverID = ServerID.KR;
            String gameName = "gameName";
            String tagLine = "tagLine";

            AccountDto account = TestDtoFactory.createAccountDto("puuid-1234");

            when(riotApiClient.callForAccountDto(serverID, gameName, tagLine))
                    .thenReturn(account);
            when(riotApiClient.callForSummonerDto(serverID, account.puuid()))
                    .thenThrow(new ResourceNotFoundException("Resource not found"));

            assertThrows(ResourceNotFoundException.class, () -> riotApiService.getProfileResponseDto(serverID, gameName, tagLine));

            verify(riotApiClient).callForAccountDto(serverID, gameName, tagLine);
            verify(riotApiClient).callForSummonerDto(serverID, account.puuid());
            verify(riotApiClient, never()).callForLeagueEntryDtoArr(any(), any());
        }

        @Test
        @DisplayName("Stops further method calls after an exception on 3rd method")
        void shouldStopFurtherMethodCallsAfterAnExceptionOnThirdMethod() {
            ServerID serverID = ServerID.KR;
            String gameName = "gameName";
            String tagLine = "tagLine";

            AccountDto account = TestDtoFactory.createAccountDto("puuid-1234");
            SummonerDto summoner = TestDtoFactory.createSummonerDto(account.puuid());

            when(riotApiClient.callForAccountDto(serverID, gameName, tagLine))
                    .thenReturn(account);
            when(riotApiClient.callForSummonerDto(serverID, account.puuid()))
                    .thenReturn(summoner);
            when(riotApiClient.callForLeagueEntryDtoArr(serverID, account.puuid()))
                    .thenThrow(new ResourceNotFoundException("Resource not found"));

            assertThrows(ResourceNotFoundException.class, () -> riotApiService.getProfileResponseDto(serverID, gameName, tagLine));

            verify(riotApiClient).callForAccountDto(serverID, gameName, tagLine);
            verify(riotApiClient).callForSummonerDto(serverID, account.puuid());
            verify(riotApiClient).callForLeagueEntryDtoArr(serverID, account.puuid());
        }
    }

    @Nested
    @DisplayName("getMatchDtos")
    class GetMatchDtosTest {

        @Test
        @DisplayName("Returns valid list of MatchDto with correct order")
        void shouldReturnMatchDtoList(){
            ServerID serverID = ServerID.KR;
            String puuid = "puuid-1234";
            long endTime = System.currentTimeMillis();
            int start = 0;
            int count = 10;

            List<String> matches = TestDtoFactory.createMatchDtoArr();

            when(riotApiClient.callForMatchesList(serverID, puuid, endTime, start, count))
                    .thenReturn(matches);
            when(riotApiClient.callForMatchDto(eq(serverID), any()))
                    .thenAnswer(invocation -> {
                            String matchId =  invocation.getArgument(1);
                            return TestDtoFactory.createMatchDto(matchId);
                    });

            List<MatchDto> result = riotApiService.getMatchDtos(serverID, puuid, endTime, start, count);


            List<String> resultIds = result.stream().map(m -> m.metadata().matchId()).toList();

            assertEquals(matches, resultIds);

            verify(riotApiClient).callForMatchesList(serverID, puuid, endTime, start, count);
            verify(riotApiClient, times(matches.size())).callForMatchDto(eq(serverID), any());
        }

        @Test
        @DisplayName("Returns an empty list when no matches found")
        void shouldReturnEmptyListWhenNoMatchFound(){
            ServerID serverID = ServerID.KR;
            String puuid = "puuid-1234";
            long endTime = System.currentTimeMillis();
            int start = 0;
            int count = 10;

            List<String> matches = List.of();

            when(riotApiClient.callForMatchesList(serverID, puuid, endTime, start, count))
                    .thenReturn(matches);

            List<MatchDto> result = riotApiService.getMatchDtos(serverID, puuid, endTime, start, count);

            assertTrue(result.isEmpty());

            verify(riotApiClient).callForMatchesList(serverID, puuid, endTime, start, count);
            verify(riotApiClient, never()).callForMatchDto(eq(serverID), any());
        }

        @Test
        @DisplayName("Aborts method callForMatchesList exception")
        void shouldAbortMethodOnCallForMatchesListException(){
            ServerID serverID = ServerID.KR;
            String puuid = "puuid-1234";
            long endTime = System.currentTimeMillis();
            int start = 0;
            int count = 10;

            when(riotApiClient.callForMatchesList(serverID, puuid, endTime, start, count))
                    .thenThrow(new BadRequestException("Bad request"));

            assertThrows(BadRequestException.class, () -> riotApiService.getMatchDtos(serverID, puuid, endTime, start, count));

            verify(riotApiClient).callForMatchesList(serverID, puuid, endTime, start, count);
            verify(riotApiClient, never()).callForMatchDto(eq(serverID), any());
        }

        @Test
        @DisplayName("Aborts method if any callForMatchDto fails")
        void shouldAbortMethodOnAnyCallForMatchDtoException(){
            ServerID serverID = ServerID.KR;
            String puuid = "puuid-1234";
            long endTime = System.currentTimeMillis();
            int start = 0;
            int count = 10;

            List<String> matches = TestDtoFactory.createMatchDtoArr();

            when(riotApiClient.callForMatchesList(serverID, puuid, endTime, start, count))
                    .thenReturn(matches);
            when(riotApiClient.callForMatchDto(eq(serverID), argThat(id -> !id.equals(matches.getFirst()))))
                    .thenThrow(new RateLimitException("Rate limit exceeded"));
            when(riotApiClient.callForMatchDto(eq(serverID), eq(matches.getFirst())))
                    .thenReturn(TestDtoFactory.createMatchDto(matches.getFirst()));

            assertThrows(RateLimitException.class, () -> riotApiService.getMatchDtos(serverID, puuid, endTime, start, count));

            verify(riotApiClient).callForMatchesList(serverID, puuid, endTime, start, count);
            verify(riotApiClient).callForMatchDto(eq(serverID), eq(matches.getFirst()));
            verify(riotApiClient, times(1)).callForMatchDto(eq(serverID), argThat(id -> !id.equals(matches.getFirst())));
        }
    }
}