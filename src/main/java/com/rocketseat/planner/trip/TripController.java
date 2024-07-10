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
}