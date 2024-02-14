package com.example.timesheetapp.web.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AffectationDTO {

    public UUID employe ;
    public List<UUID> phases ;
}
