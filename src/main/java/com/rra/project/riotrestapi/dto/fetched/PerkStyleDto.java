package com.rra.project.riotrestapi.dto.fetched;

import java.util.List;

public record PerkStyleDto(
        String description,
        List<PerkStyleSelectionDto> selections,
        int style
) {}
