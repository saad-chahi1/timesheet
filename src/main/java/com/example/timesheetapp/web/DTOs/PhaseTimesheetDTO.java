package com.example.timesheetapp.web.DTOs;

import com.example.timesheetapp.configuration.SqlTimeDeserializer;
import com.example.timesheetapp.entities.JourTimesheet;
import com.example.timesheetapp.entities.Phase;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.sql.Time;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhaseTimesheetDTO {

    @JsonFormat(pattern="HH:mm")
    @JsonDeserialize(using = SqlTimeDeserializer.class)
    private Time duration ;


    @ManyToOne
    @JoinColumn(name = "jourtimesheet_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private JourTimesheet jourTimesheet ;
}
