package com.example.timesheetapp.web;


import com.example.timesheetapp.entities.*;
import com.example.timesheetapp.repositories.ClientRepo;
import com.example.timesheetapp.repositories.ProjectRepo;
import com.example.timesheetapp.services.AccountService;
import com.example.timesheetapp.web.DTOs.ClientProjectsView;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class ClientsController {

     @Autowired
     private ClientRepo clientRepo ;

     @Autowired
     private ProjectRepo  projectRepo ;

     @Autowired
     private AccountService accountService ;

     @GetMapping("/clients")
     public ResponseEntity<?> getAllClients(Authentication authentication){
      Boolean aBoolean = authentication.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals("ADMIN") || ga.getAuthority().equals("MANAGER"));
      List<Client> clientList = (List<Client>)clientRepo.findAll();

      for(Client  client :  clientList){
          List<String> hours = new ArrayList<>();
         for ( Project project : client.getProjects()){
             List<Phase> phases = project.getPhases();
             for (Phase phase : phases ){
                 List<PhaseTimesheet> phaseTimesheets = phase.getPhaseTimesheets();
                 for(PhaseTimesheet phaseTimesheet : phaseTimesheets){
                     if( phaseTimesheet.getJourTimesheet().getTimesheet().getStatus().compareTo(TimesheetStatus.APPROUVED) == 0 ) {
                         hours.add(phaseTimesheet.getDuration().toString());
                     }
                 }
             }
         }

         client.setHours(hours);
      }

      return ResponseEntity.ok().body(clientList);
     }

     @PostMapping("/clients")
     public ResponseEntity<?> addclient(@RequestBody Client client){
        return  ResponseEntity.ok(clientRepo.save(client));
     }

     @DeleteMapping("/clients/{id}")
     public  ResponseEntity<?> deleteclient(@PathVariable UUID id){
         Client client = clientRepo.findById(id).orElseThrow(() ->  new ResourceNotFoundException("User not found for this id :" + id));

         clientRepo.delete(client);
         return ResponseEntity.ok().body("le client a bien été supprimé");
     } 
     
     @PutMapping("/clients/{id}")
     public ResponseEntity<?> updateclient(@PathVariable UUID id , @RequestBody Client client){
         Client client1 = clientRepo.findById(id).orElseThrow(() ->  new ResourceNotFoundException("Client not found for this id :" + id));
         client1.setPhone_num(client.getPhone_num());
         client1.setEmail(client.getEmail());
         client1.setNom_client(client.getNom_client());
         client1.setPays(client.getPays());
         JSONObject object = new JSONObject();
         object.put("status",200);
         object.put("response",clientRepo.save(client1));
         return ResponseEntity.ok().body(object);
     } 
     
     
     @GetMapping("/clients/{id}/projects")
     public ResponseEntity<?> getProjectsByClient(@PathVariable UUID id){
         Client client1 = clientRepo.findById(id).orElseThrow(() ->  new ResourceNotFoundException("User not found for this id :" + id));
         List<Object> result =  clientRepo.clientprojectview(client1.getId().toString());
         return ResponseEntity.ok().body(result);
     }



}
