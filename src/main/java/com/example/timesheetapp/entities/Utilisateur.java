package com.example.timesheetapp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn (
        name = "discriminator" ,
        discriminatorType = DiscriminatorType.STRING
)
@Entity
@Table(name = "utilisateur")
public class Utilisateur implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
   // @Column(name = "id", updatable = false, nullable = false ,columnDefinition = "VARCHAR(36)")
    @Type(type="pg-uuid")
    private UUID id;

    @Column(unique = true)
    private String username ;


    private String password ;

    @Column(unique = true)
    @Email
    @NotBlank
    private String email ;

    private String adresse ;

    private String nom ;

    private String prenom ;

    private String numeroTele ;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp dateCreation ;

    private Boolean enabled ;

    private Role role ;

    @OneToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
    private ProfileImage profileImage ;


}
