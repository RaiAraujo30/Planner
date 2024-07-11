package com.rocketseat.planner.trip;

import java.util.UUID;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.activities.ActivitieData;
import com.rocketseat.planner.activities.ActivitieRequestPayload;
import com.rocketseat.planner.activities.ActivitieResponse;
import com.rocketseat.planner.activities.ActivitieService;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponse;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.ParticipantCreateResponse;
import com.rocketseat.planner.participant.ParticipantData;
import com.rocketseat.planner.participant.ParticipantRequestPayload;
import com.rocketseat.planner.participant.ParticipantService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController  // This annotation is used to indicate that the class is a controller.
@RequestMapping("/trips")
public class TripController {
    

    @Autowired
    private ParticipantService participantService;


    @Autowired
    private LinkService linkService;

    @Autowired
    private ActivitieService activitieService;

    @Autowired
    private TripRepository  repository ;

    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload){
        Trip newTrip =  new Trip(payload);

        this.repository.save(newTrip);
        this.participantService.registerParticipantsToEvent(payload.emails_to_invate(), newTrip);
        
        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id){

        Optional<Trip> trip = this.repository.findById(id);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTripDetails(@PathVariable UUID id, @RequestBody TripRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip tripToUpdate = trip.get();
            tripToUpdate.setDestination(payload.destination());
            tripToUpdate.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            tripToUpdate.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            this.repository.save(tripToUpdate);

            return ResponseEntity.ok(tripToUpdate);
        } 

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip tripToUpdate = trip.get();
            
            tripToUpdate.setIsConfirmed(true);
            
            this.repository.save(tripToUpdate);
            this.participantService.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(tripToUpdate);
        } 

        return ResponseEntity.notFound().build();
    }
    

    @PostMapping("{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip tripToUpdate = trip.get();

            
            ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToEvent(payload.email(), tripToUpdate);
           
            if(tripToUpdate.getIsConfirmed()){
                this.participantService.triggerConfirmationEmailToParticipant(payload.email());
            }

            return ResponseEntity.ok(participantResponse);
        
        }

        return ResponseEntity.notFound().build();
    }


    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getTripParticipants(@PathVariable UUID id){
        List<ParticipantData> participanntsList = this.participantService.getAllParticipantsFromEvent(id);
        
        return ResponseEntity.ok(participanntsList);
    }

 
    @PostMapping("{id}/activity")
    public ResponseEntity<ActivitieResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivitieRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip tripToUpdate = trip.get();

            
           ActivitieResponse activitieResponse = this.activitieService.registerActivitie(payload, tripToUpdate);
           
            return ResponseEntity.ok(activitieResponse);
        
        }

        return ResponseEntity.notFound().build();
    }


    @GetMapping("/{id}/activity")
    public ResponseEntity<List<ActivitieData>> getAllActivities(@PathVariable UUID id){
        List<ActivitieData> activityDataList = this.activitieService.getAllActivitiesFromId(id);
        
        return ResponseEntity.ok(activityDataList);
    }



    //links
    @PostMapping("{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        if(trip.isPresent()){
            Trip tripToUpdate = trip.get();

            
            LinkResponse linkResponse = this.linkService.registerLink(payload, tripToUpdate);
           
            return ResponseEntity.ok(linkResponse);
        
        }

        return ResponseEntity.notFound().build();
    }
}