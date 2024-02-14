package com.example.timesheetapp.entities;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Manager extends Utilisateur {

    public Manager(UUID id, String username, String password, String email, String adresse, String nom, String prenom,String numeroTele,Timestamp dateCreation, Boolean Enabled,Role role,ProfileImage profileImage) {
        super(id, username, password, email, adresse, nom, prenom, numeroTele, dateCreation, Enabled,role,profileImage);
    }
}
