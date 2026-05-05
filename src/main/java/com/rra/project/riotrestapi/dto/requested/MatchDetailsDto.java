package com.rra.project.riotrestapi.dto.requested;

import java.util.ArrayList;


public record MatchDetailsDto(
        ArrayList<PlayerDisplayInfoDto> playerList
) {

}
