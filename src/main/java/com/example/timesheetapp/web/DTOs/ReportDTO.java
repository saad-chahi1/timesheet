package com.example.timesheetapp.web.DTOs;


import com.example.timesheetapp.configuration.SqlTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {

    public int Month ;

    @JsonFormat(pattern="HH:mm")
    @JsonDeserialize(using = SqlTimeDeserializer.class)
    public Time TotalDuration ;

}
