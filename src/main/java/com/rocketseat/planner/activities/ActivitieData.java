package com.rocketseat.planner.activities;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivitieData(UUID id, String tittle, LocalDateTime occurs_at) {
    
}
