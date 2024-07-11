package com.rocketseat.planner.activities;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivitieRepository extends JpaRepository  <Activitie, UUID>{
    
}
