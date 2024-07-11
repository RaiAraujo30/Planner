package com.rocketseat.planner.activities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.trip.Trip;

@Service
public class ActivitieService {
    

    @Autowired
    private ActivitieRepository repository;


    public ActivitieResponse registerActivitie(ActivitieRequestPayload payload, Trip trip){
        Activitie newActivitie = new Activitie(payload.tittle(), payload.occurs_at(), trip);

        this.repository.save(newActivitie);

        return new ActivitieResponse(newActivitie.getId());
    }
}
