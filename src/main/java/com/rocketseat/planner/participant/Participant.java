package com.rocketseat.planner.participant;

import java.util.UUID;

import com.rocketseat.planner.trip.Trip;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "participant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name= "is_confirmed", nullable = false)
    private Boolean isConfirmed;

    //Quando o nome do atributo é = ao da tabela, não é necessário o (name = "nome")
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false) //specify the mapping of a foreign key column in a database table.
    private Trip trip;

    public Participant(String email, Trip trip){
        this.name = "";
        this.isConfirmed = false;
        this.email = email;
        this.trip = trip;
    }

}
