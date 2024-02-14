package com.example.timesheetapp.web.DTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientProjectsView {

    private String nom ;

    private String status ;

    private int duree ;

    private  String conso ;



}
