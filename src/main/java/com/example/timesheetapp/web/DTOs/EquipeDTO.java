package com.example.timesheetapp.web.DTOs;

import com.example.timesheetapp.entities.Employe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipeDTO {

    public List<Employe> equipe = new ArrayList<>();
}
