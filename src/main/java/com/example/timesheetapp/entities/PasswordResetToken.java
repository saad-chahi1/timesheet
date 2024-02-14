package com.example.timesheetapp.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class PasswordResetToken {

       //private static final int EXPIRATION = 60 * 24;

        @Id
        @GeneratedValue(generator = "UUID")
        @GenericGenerator(
                name = "UUID",
                strategy = "org.hibernate.id.UUIDGenerator"
        )
        //@Column(name = "id", updatable = false, nullable = false ,columnDefinition = "VARCHAR(36)")

        @Type(type="pg-uuid")
        private UUID id;

        private String token;

        @OneToOne(targetEntity = Utilisateur.class, fetch = FetchType.EAGER)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(nullable = false, name = "user_id")
        private Utilisateur user ;

        private Date expiryDate;

 }

