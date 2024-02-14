package com.example.timesheetapp.web;

import com.example.timesheetapp.entities.*;
import com.example.timesheetapp.repositories.AffectationRepo;
import com.example.timesheetapp.repositories.EmployeRep;
import com.example.timesheetapp.repositories.PhaseRepo;
import com.example.timesheetapp.services.EmployeService;
import com.example.timesheetapp.services.NotificationServImp;
import com.example.timesheetapp.services.ProjectServicetImpl;
import com.example.timesheetapp.web.DTOs.AffectationDTO;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.util.*;

@Controller
public class AffectationController {

    @Autowired
    private AffectationRepo affectationRepo;

    @Autowired
    private EmployeService employeService ;

    @Autowired
    private ProjectServicetImpl projectService ;

    @Autowired
    private NotificationServImp notificationServImp ;

    @Autowired
    private EmployeRep employeRep ;

    @Autowired
    private PhaseRepo phaseRepo ;

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @PostMapping("projects/affectations")
    public ResponseEntity<?>  affecterEmploye(@RequestBody  Affectation affectation) throws MessagingException {
      Employe employe = employeService.loadEmployeById(affectation.getEmploye().getId()).orElseThrow(() -> new ResourceNotFoundException("Employe not found :"+ affectation.getEmploye().getId()));
       Phase phase = phaseRepo.findById(affectation.getPhase().getId()).orElseThrow(() -> new ResourceNotFoundException("Phase  not found :"+ affectation.getPhase().getId()));

       affectation.setEmploye(employe);
       affectation.setPhase(phase);
       affectation.setDateAffectation(LocalDate.now());
       affectation.setStatus(AffectationStatus.EN_COURS);

       Affectation affectation1 = affectationRepo.save(affectation);
       JSONObject resp = new JSONObject();
       resp.put("STATUS",200);
       resp.put("RESPONSE",affectation1);
       return ResponseEntity.ok().body(resp);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @DeleteMapping("/affectations/{id}")
    public ResponseEntity<?> deleteAffec(@PathVariable UUID id){
        Affectation affectation = affectationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Affectation  not found :"+ id));
        affectationRepo.deleteById(id);
        JSONObject object =  new JSONObject();
        object.put("STATUS",200);
        return ResponseEntity.ok().body(object);
    }

    @PutMapping("/affectations/{id}")
    public ResponseEntity<?> updateAffec(@RequestBody Affectation affectation , @PathVariable  UUID id){
        Affectation affectation1 = affectationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Affectation  not found :"+ id));
        Employe employe = employeService.loadEmployeById(affectation.getEmploye().getId()).orElseThrow(() -> new ResourceNotFoundException("Employe not found :"+ affectation.getEmploye().getId()));
        Phase phase = phaseRepo.findById(affectation.getPhase().getId()).orElseThrow(() -> new ResourceNotFoundException("Phase  not found :"+ affectation.getPhase().getId()));

        affectation1.setStatus(affectation.getStatus());
        affectation1.setPhase(phase);
        affectation1.setDescriptionTaches(affectation.getDescriptionTaches());
        affectation1.setDateFinPrevue(affectation.getDateFinPrevue());
        affectation1.setEmploye(employe);
        Affectation Affectation =  affectationRepo.save(affectation1);
        JSONObject object = new JSONObject();
        object.put("STATUS",200);
        object.put("RESPONSE",Affectation);
        return ResponseEntity.ok().body(object);
    }
    
    @GetMapping("/projects/{id}/affectations")
    public ResponseEntity<?> getaffectationsByProject(@PathVariable UUID id){
        List<Affectation> affectations = new ArrayList<>();
        Project project = projectService.loadProjectByID(id).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ id));
        project.getPhases().forEach(
                 phase -> {
                     phase.getAffectations().forEach(affectation -> {
                            affectations.add( affectation);
                     });
                 }
        );
        JSONObject  object = new JSONObject();
        object.put("STATUS",200);
        object.put("RESPONSE",affectations);
        return ResponseEntity.ok().body(object);
    }

    @GetMapping("/employes/{empid}/projects")
    public ResponseEntity<?> getEmployeProjects(@PathVariable UUID empid){
        Employe employe = employeService.loadEmployeById(empid).orElseThrow(() -> new ResourceNotFoundException("Employe not found :"+ empid));
        List<Project> projects = employe.getProjects();

        return ResponseEntity.ok().body(projects);

    }


    @GetMapping("/employes/{empid}/task/report")
    public ResponseEntity<?> getReport(@PathVariable UUID empid){
        Employe employe = employeService.loadEmployeById(empid).orElseThrow(() -> new ResourceNotFoundException("Employe not found :"+ empid));

       List<Affectation> affectations =  affectationRepo.findAllByEmploye(employe);
       return  ResponseEntity.ok().body(affectations);
    }
    
    @GetMapping("employes/{empid}/projects/{projectid}/affectations")
    public ResponseEntity<?> getAffectations(@PathVariable UUID empid , @PathVariable UUID projectid){
        Employe employe = employeService.loadEmployeById(empid).orElseThrow(() -> new ResourceNotFoundException("Employe not found :"+ empid));
        Project project = projectService.loadProjectByID(projectid).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ projectid));
        List<Affectation> affectations = new ArrayList<>();
        project.getPhases().forEach(phase -> {
                      phase.getAffectations().forEach((a) -> {
                          if(a.getEmploye().equals(employe)) {
                              affectations.add(a);
                          }
                      });
        });
        return ResponseEntity.ok().body(affectations);
    }

    @PutMapping("employes/affectations/{id}/updatestate")
    public ResponseEntity<?> updatestate(@PathVariable UUID id) {
        Affectation affectation = affectationRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Affectation  not found :"+ id));
        if(affectation.getStatus().compareTo(AffectationStatus.EN_COURS) == 0) {
            affectation.setStatus(AffectationStatus.TERMINE);
        }else{
            affectation.setStatus(AffectationStatus.EN_COURS);
        }
        Affectation affectation1 = affectationRepo.save(affectation);
        return ResponseEntity.ok().body(affectation1);
    }
    
    @GetMapping("/employes/{empid}/affectations/total")
    public ResponseEntity<?> gettotalaffectations(@PathVariable UUID empid){
        Employe employe = employeService.loadEmployeById(empid).orElseThrow(() -> new ResourceNotFoundException("Employe not found :"+ empid));
        JSONObject object = new JSONObject();
        object.put("TOTAL",employe.getAffectations().size());
          return   ResponseEntity.ok().body(object)   ;
    }
  //V1 affectation
    @GetMapping("/employe/{employeid}/affectations/{projectid}")
    public ResponseEntity<?> getaffectationsByProjectAndEmploye(@PathVariable UUID employeid , @PathVariable UUID projectid ){
        List<Object> result = employeRep.getWorkDurationByProjectAndByEmploye(projectid.toString(),employeid.toString());
        return ResponseEntity.ok().body(result);
    }


  //V2 affectation
  @PostMapping ("employes/projects/{projectid}/affectations")
    public ResponseEntity<?> addaffectations(@RequestBody AffectationDTO affectations){

        UUID employeid  = affectations.getEmploye();

        System.out.println(employeid);
        Employe employe = employeRep.findById(employeid).orElseThrow(() -> new ResourceNotFoundException("Employe not found :"+ employeid));
        List<Affectation> affectationList = new ArrayList<>();
        affectations.getPhases().forEach(s -> {
              Affectation affectation =  new Affectation();
              affectation.setEmploye(employe);
               Phase phase = new Phase();
               phase.setId(s);
              affectation.setPhase(phase);
              affectation.setDateAffectation(LocalDate.now());
              affectationList.add(affectation);
        });

       return ResponseEntity.ok().body( affectationRepo.saveAll(affectationList));
  }



}
