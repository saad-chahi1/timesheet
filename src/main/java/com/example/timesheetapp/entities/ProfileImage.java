package com.example.timesheetapp.entities;

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
public class ProfileImage {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    //@Column(name = "id", updatable = false, nullable = false ,columnDefinition = "VARCHAR(36)")

    @Type(type="pg-uuid")
    private UUID id;

    @Column(name = "type")
    private String type;

    @Column(name = "picByte")
    @Lob
    private byte[] picByte;

}
