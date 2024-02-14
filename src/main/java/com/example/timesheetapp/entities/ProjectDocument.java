package com.example.timesheetapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDocument {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
   // @Column(name = "id", updatable = false, nullable = false ,columnDefinition = "VARCHAR(36)")

    @Type(type="pg-uuid")
    private UUID id ;

    private String nom ;

    @Column(name = "data")

    private byte[] data ;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Project project ;



}
