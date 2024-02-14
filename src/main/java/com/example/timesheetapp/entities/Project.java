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
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.swing.text.Document;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Project {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    //@Column(name = "id", updatable = false, nullable = false ,columnDefinition = "VARCHAR(36)")

    @Type(type="pg-uuid")
    private UUID    id ;

    @NotNull
    private String  nom ;

    private String  description ;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateDebut ;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateFin ;

    private int duree ;

    private String  coutestim ;

    private String  type ;

    private Boolean  archived ;

    @Enumerated(EnumType.STRING)
    private Projectstatus status ;
 /*

    */

    @ManyToOne
    @JoinColumn(name = "client_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Client client ;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Manager manager ;

    @OneToMany( targetEntity = ProjectDocument.class ,  cascade = CascadeType.ALL , mappedBy = "project" , orphanRemoval = true)
    private List<ProjectDocument> projectDocuments = new ArrayList<>();


    @ManyToMany
    @JoinTable(name = "Project_Employe",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employe_id")
    )
    private  List<Employe> equipe = new ArrayList<>() ;

    @OneToMany( targetEntity = Phase.class, cascade = CascadeType.ALL , mappedBy = "project")
    @JsonIgnore
    private List<Phase> phases = new ArrayList<>();


    public void addEmployeToProject(Employe employe){
         employe.getProjects().add(this);
         this.getEquipe().add(employe);
    }

    public void addphase(Phase  phase){
        phase.setProject(this);
        this.getPhases().add(phase);
    }

    public void removeEmployeFromProject(Employe employe){
        employe.getProjects().remove(this);
        this.getEquipe().remove(employe);
    }


}
