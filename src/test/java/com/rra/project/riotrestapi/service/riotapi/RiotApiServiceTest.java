package com.rra.project.riotrestapi.service.riotapi;

import com.rra.project.riotrestapi.dto.fetched.*;
import com.rra.project.riotrestapi.dto.requested.*;
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
import org.mockito.MockedStatic;
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
        private final ServerID serverID = ServerID.KR;
        private final String gameName = "gameName";
        private final String tagLine = "tagLine";

        @Test
        @DisplayName("Returns valid response DTO")
        void shouldReturnProfileResponseDto() {
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
        private final ServerID serverID = ServerID.KR;
        private final String puuid = "puuid-1234";
        private final long endTime = System.currentTimeMillis();
        private final int start = 0;
        private final int count = 10;

        @Test
        @DisplayName("Returns valid list of MatchDto with correct order")
        void shouldReturnMatchDtoList(){
            List<String> matches = TestDtoFactory.createMatchesArr();

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
            when(riotApiClient.callForMatchesList(serverID, puuid, endTime, start, count))
                    .thenThrow(new BadRequestException("Bad request"));

            assertThrows(BadRequestException.class, () -> riotApiService.getMatchDtos(serverID, puuid, endTime, start, count));

            verify(riotApiClient).callForMatchesList(serverID, puuid, endTime, start, count);
            verify(riotApiClient, never()).callForMatchDto(eq(serverID), any());
        }

        @Test
        @DisplayName("Aborts method if any callForMatchDto fails")
        void shouldAbortMethodOnAnyCallForMatchDtoException(){
            List<String> matches = TestDtoFactory.createMatchesArr();

            when(riotApiClient.callForMatchesList(serverID, puuid, endTime, start, count))
                    .thenReturn(matches);
            when(riotApiClient.callForMatchDto(eq(serverID), argThat(id -> !id.equals(matches.getFirst()))))
                    .thenThrow(new RateLimitException("Rate limit exceeded"));
            when(riotApiClient.callForMatchDto(eq(serverID), eq(matches.getFirst())))
                    .thenReturn(TestDtoFactory.createMatchDto(matches.getFirst()));

            assertThrows(RateLimitException.class, () -> riotApiService.getMatchDtos(serverID, puuid, endTime, start, count));

            verify(riotApiClient).callForMatchesList(serverID, puuid, endTime, start, count);
            verify(riotApiClient).callForMatchDto(eq(serverID), eq(matches.getFirst()));
            verify(riotApiClient).callForMatchDto(eq(serverID), eq(matches.get(1)));
            verify(riotApiClient, never()).callForMatchDto(eq(serverID), eq(matches.get(2)));
        }
    }

    @Nested
    @DisplayName("getMatchInfoDtos")
    class GetMatchInfoDtosTest{
        private final ServerID serverID = ServerID.KR;
        private final String puuid = "puuid-1234";
        private final long endTime = System.currentTimeMillis();
        private final int start = 0;
        private final int count = 10;

        @Test
        @DisplayName("Returns valid list of MatchInfoDto with correct data")
        void shouldReturnMatchInfoDtoList(){


            List<String> matches = TestDtoFactory.createMatchesArr();
            MatchDto matchFirst = TestDtoFactory.createMatchDto(matches.getFirst());
            MatchDto matchLast = TestDtoFactory.createMatchDto(matches.getLast());

            when(riotApiClient.callForMatchesList(serverID, puuid, endTime, start, count))
                    .thenReturn(matches);
            when(riotApiClient.callForMatchDto(eq(serverID), any()))
                    .thenAnswer(invocation -> {
                        String matchId =  invocation.getArgument(1);
                        return TestDtoFactory.createMatchDto(matchId);
                    });
            when(riotApiClient.callForMatchDto(serverID, matches.getFirst()))
                    .thenReturn(matchFirst);
            when(riotApiClient.callForMatchDto(serverID, matches.getLast()))
                    .thenReturn(matchLast);

            try(MockedStatic<MatchInfoDto> mocked =  mockStatic(MatchInfoDto.class)) {
                mocked.when(() -> MatchInfoDto.from(any(), any(), any()))
                        .thenReturn(mock(MatchInfoDto.class));

                riotApiService.getMatchInfoDtos(serverID, puuid, endTime, start, count);

                mocked.verify(() -> MatchInfoDto.from(puuid, matchFirst, dataDragonService));
                mocked.verify(() -> MatchInfoDto.from(puuid, matchLast, dataDragonService));
                mocked.verify(() -> MatchInfoDto.from(any(), any(), any()), times(matches.size()));
            }

            verify(riotApiClient).callForMatchesList(serverID, puuid, endTime, start, count);
            verify(riotApiClient, times(matches.size())).callForMatchDto(eq(serverID), any());
        }

        @Test
        @DisplayName("Returns an empty list if getMatchDtos also returns empty list")
        void shouldReturnEmptyListWhenNoMatchesFound(){
            List<String> matches = List.of();

            when(riotApiClient.callForMatchesList(serverID, puuid, endTime, start, count))
                    .thenReturn(matches);

            try(MockedStatic<MatchInfoDto> mocked =  mockStatic(MatchInfoDto.class)) {

                List<MatchInfoDto> results = riotApiService.getMatchInfoDtos(serverID, puuid, endTime, start, count);

                assertTrue(results.isEmpty());
                mocked.verify(() -> MatchInfoDto.from(any(), any(), any()), never());
            }
        }

        @Test
        @DisplayName("Throws an Exception if getMatchDtos also throws")
        void shouldThrowExceptionIfGetMatchDtosThrows(){
            when(riotApiClient.callForMatchesList(serverID, puuid, endTime, start, count))
                    .thenThrow(new RateLimitException("Rate limit exceeded"));

            try(MockedStatic<MatchInfoDto> mocked =  mockStatic(MatchInfoDto.class)) {
                assertThrows(RateLimitException.class, () -> {riotApiService.getMatchInfoDtos(serverID, puuid, endTime, start, count);});

                mocked.verify(() -> MatchInfoDto.from(any(), any(), any()), never());
            }
        }

        @Test
        @DisplayName("Throws an Exception if any MatchInfoDto.from also throws")
        void shouldThrowExceptionIfMatchInfoDtoFromAlsoThrows(){
            List<String> matches = TestDtoFactory.createMatchesArr();
            MatchDto match1 = TestDtoFactory.createMatchDto(matches.getFirst());
            MatchDto match2 = TestDtoFactory.createMatchDto(matches.get(1));

            when(riotApiClient.callForMatchesList(serverID, puuid, endTime, start, count))
                    .thenReturn(matches);
            when(riotApiClient.callForMatchDto(eq(serverID), any()))
                    .thenAnswer(invocation -> {
                        String matchId =  invocation.getArgument(1);
                        return TestDtoFactory.createMatchDto(matchId);
                    });
            when(riotApiClient.callForMatchDto(eq(serverID), eq(matches.getFirst())))
                    .thenReturn(match1);
            when(riotApiClient.callForMatchDto(eq(serverID), eq(matches.get(1))))
                    .thenReturn(match2);

            try(MockedStatic<MatchInfoDto> mocked =  mockStatic(MatchInfoDto.class)) {
                mocked.when(() -> MatchInfoDto.from(puuid, match1, dataDragonService))
                        .thenReturn(mock(MatchInfoDto.class));
                mocked.when(() -> MatchInfoDto.from(puuid, match2, dataDragonService))
                        .thenThrow(new RuntimeException("RuntimeException"));

                assertThrows(RuntimeException.class, () -> riotApiService.getMatchInfoDtos(serverID, puuid, endTime, start, count));

                mocked.verify(() -> MatchInfoDto.from(puuid, match1, dataDragonService));
                mocked.verify(() -> MatchInfoDto.from(puuid, match2, dataDragonService));
                mocked.verifyNoMoreInteractions();
            }
        }
    }
    @Nested
    @DisplayName("getMatchDetailsDto")
    class GetMatchDetailsDtoTest {
        private final ServerID serverID = ServerID.KR;
        private final String matchId = "KR_12345678";

        @Test
        @DisplayName("Returns MatchDetailsDto with all players details returned by callForMatchDto")
        void shouldReturnMatchDetailsDto(){
            MatchDto match = TestDtoFactory.createMatchDto(matchId);
            List<ParticipantDto> participants = match.info().participants();

            when(riotApiClient.callForMatchDto(serverID, matchId))
                    .thenReturn(match);

            try(MockedStatic<PlayerDisplayInfoDto> mocked =  mockStatic(PlayerDisplayInfoDto.class)) {
                mocked.when(() -> PlayerDisplayInfoDto.from(any(), any(), any()))
                        .thenReturn(mock(PlayerDisplayInfoDto.class));

                MatchDetailsDto result = riotApiService.getMatchDetailsDto(serverID, matchId);

                assertEquals(participants.size(), result.players().size());

                verify(riotApiClient).callForMatchDto(serverID, matchId);
                mocked.verify(() -> PlayerDisplayInfoDto.from(participants.getFirst(), match.info(), dataDragonService));
                mocked.verify(() -> PlayerDisplayInfoDto.from(participants.getLast(), match.info(), dataDragonService));
                mocked.verify(() -> PlayerDisplayInfoDto.from(any(), eq(match.info()), eq(dataDragonService)), times(participants.size()));
            }
        }

        @Test
        @DisplayName("Throws an Exception on callForMatchDto Exception")
        void shouldThrowExceptionOnCallForMatchDtoException(){
            when(riotApiClient.callForMatchDto(serverID, matchId))
                    .thenThrow(new RateLimitException("Rate limit exceeded"));

            try(MockedStatic<PlayerDisplayInfoDto> mocked =  mockStatic(PlayerDisplayInfoDto.class)) {
                mocked.when(() -> PlayerDisplayInfoDto.from(any(), any(), any()))
                        .thenReturn(mock(PlayerDisplayInfoDto.class));

                assertThrows(RateLimitException.class, () -> riotApiService.getMatchDetailsDto(serverID, matchId));

                mocked.verify(() -> PlayerDisplayInfoDto.from(any(), any(), any()), never());
                verify(riotApiClient).callForMatchDto(serverID, matchId);
            }
        }

        @Test
        @DisplayName("Throws an Exception if any PlayerDisplayInfoDto.from throws")
        void shouldThrowExceptionIfPlayerDisplayInfoDtoAlsoThrows(){
            MatchDto match = TestDtoFactory.createMatchDto(matchId);
            List<ParticipantDto> participants = match.info().participants();

            when(riotApiClient.callForMatchDto(serverID, matchId))
                    .thenReturn(match);

            try(MockedStatic<PlayerDisplayInfoDto> mocked =  mockStatic(PlayerDisplayInfoDto.class)) {
                mocked.when(() -> PlayerDisplayInfoDto.from(any(), any(), any()))
                        .thenThrow(new RuntimeException("RuntimeException"));
                mocked.when(() -> PlayerDisplayInfoDto.from(eq(participants.getFirst()), any(), any()))
                        .thenReturn(mock(PlayerDisplayInfoDto.class));

                assertThrows(RuntimeException.class, () -> riotApiService.getMatchDetailsDto(serverID, matchId));

                verify(riotApiClient).callForMatchDto(serverID, matchId);
                mocked.verify(() -> PlayerDisplayInfoDto.from(eq(participants.getFirst()), any(), any()));
                mocked.verify(() -> PlayerDisplayInfoDto.from(eq(participants.get(1)), any(), any()));
                mocked.verifyNoMoreInteractions();
            }
        }
    }
}