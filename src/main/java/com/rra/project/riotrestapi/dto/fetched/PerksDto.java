package com.rra.project.riotrestapi.dto.fetched;

import java.util.List;

public record PerksDto(
        PerkStatsDto statPerks,
        List<PerkStyleDto> styles
) {}
