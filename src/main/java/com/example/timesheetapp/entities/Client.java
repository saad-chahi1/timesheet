package com.example.timesheetapp.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
  //  @Column(name = "id", updatable = false, nullable = false ,columnDefinition = "VARCHAR(36)")
    @Type(type="org.hibernate.type.PostgresUUIDType")
    private UUID    id ;

    private String  nom_client ;

    private String  email ;

    private String  pays ;

    private String  phone_num ;

    @JsonIgnore
    @OneToMany(mappedBy = "client" , cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();

    @Transient
    private List<String> hours = new ArrayList<String>() ;

}
