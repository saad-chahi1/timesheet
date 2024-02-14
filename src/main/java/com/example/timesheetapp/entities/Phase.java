package com.example.timesheetapp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "phase")
public class Phase {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    //@Column(name = "id", updatable = false, nullable = false ,columnDefinition = "VARCHAR(36)")

    @Type(type="pg-uuid")
    private UUID id;

    private int duree ;

    private Integer priority ;

    @Transient
    private List<String> consomme ;

    private String phaseType ;

    @OneToMany(mappedBy = "phase" , cascade = CascadeType.ALL)
    @JsonIgnore
    List<Affectation> affectations = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "phase" , cascade = CascadeType.ALL)
    private  List<PhaseTimesheet> phaseTimesheets = new ArrayList<PhaseTimesheet>() ;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project ;


    int getdonepercent(){
            int done = 0 ;
            int l = this.getAffectations().size();
            for (Affectation affectation : this.getAffectations()){
                if(affectation.getStatus().compareTo(AffectationStatus.TERMINE) == 0){
                    done ++ ;
                }
            }
              return (done/l)*100 ;
    }

    public void addPhaseTimesheet(PhaseTimesheet phaseTimesheet){
        this.getPhaseTimesheets().add(phaseTimesheet);
    }



}
