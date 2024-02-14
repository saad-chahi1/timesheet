package com.example.timesheetapp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type="org.hibernate.type.PostgresUUIDType")
    private UUID id ;

    private String message ;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate sentAt ;

    private boolean vu ;


    @ManyToOne
    @JoinColumn(name="utilisateur_id")
    Utilisateur utilisateur ;



}
