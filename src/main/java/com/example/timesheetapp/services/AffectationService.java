package com.example.timesheetapp.services;


import com.example.timesheetapp.entities.Affectation;
import com.example.timesheetapp.repositories.AffectationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AffectationService {

     @Autowired
     private AffectationRepo affectationRepo ;

     public Affectation affecterEmploye(Affectation affectation){
           return affectationRepo.save(affectation);
     }

     public void DeleteAffectation(UUID ID){
          affectationRepo.deleteById(ID);
     }



}
