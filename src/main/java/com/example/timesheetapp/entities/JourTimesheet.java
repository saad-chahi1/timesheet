package com.example.timesheetapp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JourTimesheet  {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )



    @Type(type="pg-uuid")
    private UUID id ;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate date ;

    @ManyToOne
    @JoinColumn(name="timesheet_id")
    @JsonIgnore
    private Timesheet timesheet ;


    @OneToMany(mappedBy = "jourTimesheet" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<PhaseTimesheet> phaseTimesheets = new ArrayList<>();


    public void addPhaseyTimesheet(PhaseTimesheet phaseTimesheet){
        this.getPhaseTimesheets().add(phaseTimesheet);
    }


}
