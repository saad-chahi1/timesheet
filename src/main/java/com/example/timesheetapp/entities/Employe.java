package com.example.timesheetapp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;


import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@ToString(callSuper = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Employe extends Utilisateur {

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd")
    private LocalDate dateembauche ;

    private String profession ;



    @Enumerated(EnumType.STRING)
    private TypeEmploye typeEmploye;


    @OneToMany(cascade = CascadeType.ALL , mappedBy = "employe")
    @JsonIgnore
    private List<Affectation> affectations = new ArrayList<>();


     @JsonIgnore
     @ManyToMany(mappedBy = "equipe")
     private List<Project> projects = new ArrayList<>();



    @Builder
    public Employe(UUID id, String username, String password, String email, String adresse, String nom, String prenom, String numeroTele, Timestamp dateCreation, Boolean Enabled,Role role,ProfileImage profileImage) {
        super(id, username, password, email, adresse, nom, prenom, numeroTele, dateCreation, Enabled,role,profileImage);
    }


    public void addproject(Project project){

    }


}
