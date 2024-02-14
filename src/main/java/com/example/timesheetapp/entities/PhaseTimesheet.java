package com.example.timesheetapp.entities;

import com.example.timesheetapp.configuration.SqlTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhaseTimesheet {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    //@Column(name = "id", updatable = false, nullable = false ,columnDefinition = "VARCHAR(36)")

    @Type(type="pg-uuid")
    private UUID id ;

    @JsonFormat(pattern="HH:mm")
    @JsonDeserialize(using = SqlTimeDeserializer.class)
    private Time duration ;

    @Type(type = "text")
    private String description ;

    @ManyToOne
    @JoinColumn(name = "phase_id" , nullable = false)
    private Phase phase ;

    @ManyToOne
    @JoinColumn(name = "jourtimesheet_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private JourTimesheet jourTimesheet ;              //jourtimesheet and phase



}
