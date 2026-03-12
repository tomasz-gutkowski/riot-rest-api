package com.rra.project.riotrestapi.dto;

import java.util.List;

public record MetadataDto(
        String dataVersion,
        String matchId,
        List<String> participants)
{}
