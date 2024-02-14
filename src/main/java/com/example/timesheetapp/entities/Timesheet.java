package com.example.timesheetapp.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Timesheet {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    //@Column(name = "id", updatable = false, nullable = false ,columnDefinition = "VARCHAR(36)")
    @Type(type="pg-uuid")
    private UUID id ;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(updatable = false)
    private LocalDate dateDebut ;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Column(updatable = false)
    private LocalDate dateFin ;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastupdate ;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate submittedAt ;

    private String totalduration ;

    @Type(type = "text")
    private String description ;

    private String rejectedBy ;

    private String approuvedBy ;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime   approuvedAt ;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime   rejectedAt ;

    private String raisonRejection ;

    @Enumerated(EnumType.STRING)
    private TimesheetStatus status ;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "EMPLOYE_ID")
    private Employe employe ;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL , mappedBy = "timesheet" , orphanRemoval = true)
    private List<JourTimesheet> jourTimesheets = new ArrayList<JourTimesheet>();

    public void addjourtimesheet(JourTimesheet jourTimesheet){
        jourTimesheet.setTimesheet(this);
        this.jourTimesheets.add(jourTimesheet);
    }

    public void resettimesheetdetails(){
        this.setApprouvedAt(null);
        this.setApprouvedBy(null);
        this.setRaisonRejection(null);
        this.setRejectedBy(null);
        this.setRejectedAt(null);
        this.setRaisonRejection(null);
    }



}
