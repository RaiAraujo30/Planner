package com.rocketseat.planner.activities;

import java.util.List;
import java.util.UUID;

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

    public List<ActivitieData> getAllActivitiesFromId(UUID tripId){
        return this.repository.findByTripId(tripId).stream().map(activitie -> new ActivitieData(activitie.getId(), activitie.getTittle(), activitie.getOccurs_at())).toList();
    }
}