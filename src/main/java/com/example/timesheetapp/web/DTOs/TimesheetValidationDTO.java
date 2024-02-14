package com.example.timesheetapp.web.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetValidationDTO {

    public UUID timesheetid ;

    public String status ;

    public String comment ;

}
