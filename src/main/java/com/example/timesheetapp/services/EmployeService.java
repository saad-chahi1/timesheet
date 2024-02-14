package com.example.timesheetapp.services;


import com.example.timesheetapp.entities.Employe;
import com.example.timesheetapp.repositories.EmployeRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeService {

    @Autowired
    private EmployeRep employeRep ;

    public Optional<Employe> loadEmployeById(UUID uuid){

         return employeRep.findById(uuid) ;
    }
}
