package com.example.timesheetapp.web;


import com.example.timesheetapp.entities.*;
import com.example.timesheetapp.repositories.*;
import com.example.timesheetapp.services.AccountService;
import com.example.timesheetapp.services.EmployeService;
import com.example.timesheetapp.services.ProjectServicetImpl;
import com.example.timesheetapp.web.DTOs.EquipeDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.Response;
import java.io.IOException;
import java.util.*;


@RestController
public class ProjectsController {

     @Autowired
     private ProjectServicetImpl projectService ;


     @Autowired
     private AccountService  accountService ;

     @Autowired
     private ManagerRepo managerRepo ;

     @Autowired
     private ClientRepo clientRepo ;

     @Autowired
     private PhaseRepo  phaseRepo ;

     @Autowired
     private EmployeRep employeRep ;

     @Autowired
     private EmployeService employeService;

     @Autowired
     private ProjectDocumentRepo documentRepo ;


     @Autowired
     private ProjectRepo projectRepo ;
     
     @GetMapping("/Administration/projects")
     public ResponseEntity<?> getallprojects(){
       return ResponseEntity.ok(projectService.loadallprojects());
     }

     @GetMapping("/projects")
     public ResponseEntity<?> getProjectsByManager(){
         //Authentication auth = SecurityContextHolder.getContext().getAuthentication() ;
         //String username = (String)auth.getPrincipal() ;
         //Manager manager  = managerRepo.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Task not found for this username:"+ username));
         return ResponseEntity.ok().body(projectService.loadallprojects());
     }

    @GetMapping("/projects/archive")
    public ResponseEntity<?> getArchivProjectsByManager(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication() ;
        String username = (String)auth.getPrincipal() ;
        Manager manager  = managerRepo.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Task not found for this username:"+ username));
        return ResponseEntity.ok().body(projectService.loadArchivedProjectsByManager((manager)));
    }

    @PostMapping("/projects")
     public ResponseEntity<?> addproject( @RequestParam("model") String ProjectModel , @RequestParam(value = "files",required = false) List<MultipartFile> files) throws JsonProcessingException {

         Authentication auth = SecurityContextHolder.getContext().getAuthentication() ;
         String username = (String)auth.getPrincipal() ;
         Manager manager  = managerRepo.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("Manager not found for this username:"+ username));
         ObjectMapper objectMapper = new ObjectMapper() ;
         Project project  = objectMapper.readValue(ProjectModel,Project.class);
         Client client = clientRepo.findById(project.getClient().getId()).orElseThrow(() -> new ResourceNotFoundException("Client not found for this id:"+ project.getClient().getId()));;
         project.setManager(manager) ;
         project.setClient(client);
         List<ProjectDocument> projectDocuments = new ArrayList<>() ;
         if(files != null ){
            files.forEach( file -> {
                ProjectDocument pdocument = new ProjectDocument() ;
                try {
                    pdocument.setNom(file.getOriginalFilename());
                    pdocument.setData(file.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                pdocument.setProject(project);
                projectDocuments.add(pdocument);
            });
        }
         project.setArchived(false);
         project.setStatus(Projectstatus.EN_COURS);
         project.setProjectDocuments(projectDocuments);
         List<Phase> phases = new ArrayList<>();
        //adding phases based on project type
        switch(project.getType()) {
            case "DEV":
                 Phase cadrage = new Phase();
                  cadrage.setPriority(1);
                 cadrage.setPhaseType(PhaseType.CADRAGE.toString());
                 Phase conception = new Phase();
                  conception.setPriority(2);
                 conception.setPhaseType(PhaseType.CONCEPTION.toString());
                 Phase realisation  = new Phase();
                 realisation.setPriority(3);
                 realisation.setPhaseType(PhaseType.REALISATION.toString());
                 Phase recette = new Phase();
                   recette.setPriority(4);
                 recette.setPhaseType(PhaseType.RECETTES.toString());
                 Phase deploiment = new Phase();
                   deploiment.setPriority(5);
                 deploiment.setPhaseType(PhaseType.DEPLOIEMENT.toString());
                 Phase maintenance = new Phase();
                   maintenance.setPriority(6);
                 maintenance.setPhaseType(PhaseType.MAINTENANCE.toString()) ;
                 phases.add(cadrage);
                 phases.add(conception);
                 phases.add(realisation);
                 phases.add(recette);
                 phases.add(deploiment);
                 phases.add(maintenance);
                break;
            case "MAINT":
                Phase maintenance1 = new Phase();
                 maintenance1.setPriority(1);
                maintenance1.setPhaseType(PhaseType.MAINTENANCE.toString()) ;
                phases.add(maintenance1);
                break;
            case "BI":
                Phase etl = new Phase();
                etl.setPhaseType(PhaseType.ETL.toString()) ;
                 etl.setPriority(1);
                Phase cube = new Phase();
                cube.setPhaseType(PhaseType.CUBE.toString());
                 cube.setPriority(2);
                Phase reporting = new Phase();
                reporting.setPhaseType(PhaseType.REPORTING.toString());
                 reporting.setPriority(3);
                phases.add(etl);
                phases.add(cube);
                phases.add(reporting);

                break;
            default:
              break ;
        }
         phases.forEach(phase -> { project.addphase(phase);});
         Project project1  =  projectService.saveProject(project);
         return ResponseEntity.ok().body(project1);
     }


     @DeleteMapping ("/projects/{id}")
     public ResponseEntity<?> deleteproject(@PathVariable UUID id){
         Project project = projectService.loadProjectByID(id).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ id));
         projectService.deleteProject(id);
         JSONObject object = new JSONObject();
         object.put("STATUS",200);
         object.put("RESPONSE","project a bien été supprimé");
         return ResponseEntity.ok().body(object);
     }


     @PutMapping("/projects/archive/{id}")
     public ResponseEntity<?> archivproject(@PathVariable UUID id){
         Project project = projectService.loadProjectByID(id).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ id));
         project.setArchived(true);
         Project project1 = projectService.saveProject(project);
         return ResponseEntity.ok().body(project1);
     }

     @PutMapping("/projects/{id}")
     public ResponseEntity<?> updateproject(@RequestBody Project project , @PathVariable UUID id){

         Project project1 = projectService.loadProjectByID(id).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ id));
         Client client = clientRepo.findById(project.getClient().getId()).orElseThrow(() -> new ResourceNotFoundException("Client not found for this id:"+ project.getClient().getId()));
         project1.setClient(client);
         project1.setStatus(project.getStatus());
         project1.setNom(project.getNom());
         project1.setDateDebut(project.getDateDebut());
         project1.setDateFin(project.getDateFin());
         project1.setDescription(project.getDescription());
         project1.setDuree(project.getDuree());
         project1.setCoutestim(project.getCoutestim());
         project1.setType(project.getType());
         Project updatedproject = projectService.saveProject(project1) ;
         return ResponseEntity.ok().body(updatedproject);

     }

     @PostMapping("projects/{id}/equipe")
     public ResponseEntity<?> addemployesToProjectTeam(@PathVariable UUID id , @RequestBody EquipeDTO equipeDTO){
         List<Employe> employes = equipeDTO.getEquipe();
         Project project1 = projectService.loadProjectByID(id).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ id));
        // employes.forEach(employe -> { project1.getEquipe().add(employe); });
         employes.forEach(employe -> { project1.addEmployeToProject(employe); });
         Project project= projectService.saveProject(project1);
         JSONObject object = new JSONObject();
         object.put("STATUS",200);
         object.put("RESPONSE",project);
         return ResponseEntity.ok().body(object);
     }

     @PostMapping

     @DeleteMapping("projects/{projectid}/equipe/{employeid}")

     public ResponseEntity<?> removeemployefromobject(@PathVariable UUID projectid , @PathVariable UUID employeid){
         Project project1 = projectService.loadProjectByID(projectid).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ projectid));
         Employe employe = employeRep.findById(employeid).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ employeid));
         project1.getEquipe().remove(employe);
         Project project= projectService.saveProject(project1);
         JSONObject object = new JSONObject();
         object.put("STATUS",200);
         object.put("RESPONSE",project);
         return ResponseEntity.ok().body(object);
     }

     @PostMapping("/projects/{id}/phases")
     public ResponseEntity<?> addphasetoproject(@PathVariable UUID id , @RequestBody Phase phase){
         Project project1 = projectService.loadProjectByID(id).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ id));
         int max = 1 ;
         for(Phase phase1 : project1.getPhases()){
             if(phase1.getPriority() > max ){
                 max = phase1.getPriority();
             }
         }
         phase.setPriority(max+1);
         phase.setProject(project1);
         Phase phase1 = phaseRepo.save(phase);
         return ResponseEntity.ok().body(phase1);
     }

     @GetMapping("/projects/{id}/phases")
     public ResponseEntity<?> getPhases(@PathVariable UUID id){
         Project project1 = projectService.loadProjectByID(id).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ id));
         List<Phase> phases  =  project1.getPhases();
         Collections.sort(phases, Comparator.comparing(Phase::getPriority));
         for( Phase phase :  phases ) {
             List<String> hours = new ArrayList<>();
             List<PhaseTimesheet> phaseTimesheets = phase.getPhaseTimesheets();
             for(PhaseTimesheet phaseTimesheet : phaseTimesheets){
                 if( phaseTimesheet.getJourTimesheet().getTimesheet().getStatus().compareTo(TimesheetStatus.APPROUVED) == 0 ) {
                     hours.add(phaseTimesheet.getDuration().toString());
                 }
             }
             phase.setConsomme(hours);
         }

         project1.setPhases(phases);

         return ResponseEntity.ok().body(project1.getPhases());
     }

     @GetMapping("/projects/{id}")
     public ResponseEntity<?> getprojectbyid(@PathVariable UUID id){
         Project project1 = projectService.loadProjectByID(id).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ id));

         return ResponseEntity.ok().body(project1);
     }

     @PutMapping("/phases/{id}")
     public  ResponseEntity<?> updatephase(@PathVariable UUID id , @RequestBody Phase phase){
         Phase phase1 = phaseRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Phase not found :"+ id));
         phase1.setDuree(phase.getDuree());
         JSONObject object = new JSONObject();
         object.put("status",200) ;
         object.put("resp",phaseRepo.save(phase1));
         return  ResponseEntity.ok().body(object);
     }

     @DeleteMapping("/phases/{id}")
     public  ResponseEntity<?> delephase(@PathVariable UUID id){
         Phase phase = phaseRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Phase not found :"+ id));
          phaseRepo.delete(phase);
          JSONObject object = new JSONObject();
          object.put("STATUS",200) ;
         return  ResponseEntity.ok().body(object);
     }


     @DeleteMapping ("/projects/{projectid}/documents/{documentid}")
     public ResponseEntity<?> deleteDocument(@PathVariable UUID projectid , @PathVariable UUID documentid){
         Project project1 = projectService.loadProjectByID(projectid).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+projectid));
         ProjectDocument projectDocument = documentRepo.findById(documentid).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+documentid));
         project1.getProjectDocuments().remove(projectDocument);
         Project project =  projectService.saveProject(project1);
         JSONObject object = new JSONObject();
         object.put("STATUS",200);

         return ResponseEntity.ok().body(object);
     }

     @PostMapping("projects/{projectid}/documents")
     public ResponseEntity<?> adddocuments(@RequestParam(value = "files") List<MultipartFile> files , @PathVariable UUID projectid ){
         Project project1 = projectService.loadProjectByID(projectid).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+projectid));
         files.forEach(multipartFile -> {
                ProjectDocument projectDocument = new ProjectDocument();
                projectDocument.setNom(multipartFile.getOriginalFilename());
                projectDocument.setProject(project1);
             try {
                 projectDocument.setData(multipartFile.getBytes());
             } catch (IOException e) {
                 e.printStackTrace();
             }

             project1.getProjectDocuments().add(projectDocument);
         });

         Project project = projectService.saveProject(project1);

         JSONObject object = new JSONObject();
         object.put("STATUS",200);
         object.put("RESPONSE",project);

         return  ResponseEntity.ok().body(object);
     }

     @GetMapping("/phases/{id}/percentdone")
    ResponseEntity<?> getpercentdone(@PathVariable UUID id){
       return null ;
     }

     @PreAuthorize("hasAnyAuthority('MANAGER','EMPLOYE','ADMIN')")
     @GetMapping("/phases/{id}/project")
     ResponseEntity<?> getProjectBuPhase(@PathVariable UUID id){
         Phase phase = phaseRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Phase not found :"+ id));
         return ResponseEntity.ok().body(phase.getProject());
     }

     @GetMapping("projects/{projectid}/reporting")
     public ResponseEntity<?> reporting(@PathVariable UUID projectid){
         Project project = projectService.loadProjectByID(projectid).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ projectid));
          List<String> hours = new ArrayList<>();
          List<Phase> phases = project.getPhases();
          for (Phase phase : phases ){
            List<PhaseTimesheet> phaseTimesheets = phase.getPhaseTimesheets();
             for(PhaseTimesheet phaseTimesheet : phaseTimesheets){
                if( phaseTimesheet.getJourTimesheet().getTimesheet().getStatus().compareTo(TimesheetStatus.APPROUVED) == 0 ) {
                      hours.add(phaseTimesheet.getDuration().toString());
                 }
             }
         }
          JSONObject object = new JSONObject();
          object.put("STATUS",200);
          object.put("HOURS",hours);
         return ResponseEntity.ok().body(object);

     }

    @GetMapping("/phases/{phaseid}/reporting")
    public ResponseEntity<?> reportinga(@PathVariable UUID phaseid){


        Phase phase = phaseRepo.findById(phaseid).orElseThrow(() -> new ResourceNotFoundException("Phase not found :"+ phaseid));
        List<String> hours = new ArrayList<>();
        List<PhaseTimesheet> phaseTimesheets = phase.getPhaseTimesheets();
        for(PhaseTimesheet phaseTimesheet : phaseTimesheets){
            if( phaseTimesheet.getJourTimesheet().getTimesheet().getStatus().compareTo(TimesheetStatus.APPROUVED) == 0 ) {
                hours.add(phaseTimesheet.getDuration().toString());
            }
        }

        JSONObject object = new JSONObject();
        object.put("STATUS",200);
        object.put("HOURS",hours);
        return ResponseEntity.ok().body(object);


    }

    @GetMapping("/employes/{empid}/projects/total")
    public ResponseEntity<?> getaffectations(@PathVariable UUID empid){
        Employe employe = employeService.loadEmployeById(empid).orElseThrow(() -> new ResourceNotFoundException("Employe not found :"+ empid));
        JSONObject object = new JSONObject();
        object.put("TOTAL",employe.getProjects().size());
        return   ResponseEntity.ok().body(object)   ;
    }


    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("/projects/report/{empid}")
    public ResponseEntity<?> getProjectReport(@PathVariable String empid){
        List<Object> report = new ArrayList<>();
                if(empid.equals("undefined")){
                    report = projectRepo.getprojectsreport();
                }else{
                    report = projectRepo.getprojectsreportbyEmploye(empid);
                }
        return ResponseEntity.ok().body(report);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("/projects/{id}/gantt")
    public ResponseEntity getProjectView(@PathVariable UUID id){
         List<Object> objects = phaseRepo.getGanttView(id.toString());
         return ResponseEntity.ok().body(objects);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','EMPLOYE')")
    @GetMapping("/projects/{projectid}/equipe/{employeid}/renta")
    public ResponseEntity<?> getRentab(@PathVariable UUID projectid , @PathVariable UUID employeid){
         List<Object> result = employeRep.getWorkDurationByProjectAndByEmploye(projectid.toString(),employeid.toString());
        return ResponseEntity.ok().body(result);
     }



    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
     @GetMapping("/projects/{id}/consumeddetails")
    public  ResponseEntity<?> getChargeConsumedDetails(@PathVariable UUID id){
       List<Object> result = projectRepo.getProjectChargeCosummeReport(id.toString());
       return ResponseEntity.ok().body(result);
     }

    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    @GetMapping("/projects/{projectid}/{employeid}/notAffectedPhases")
    public ResponseEntity<?> getNotAffectedPhasesByProjectAndEmploye(@PathVariable UUID employeid , @PathVariable UUID projectid ){
        List<Object> result = projectRepo.getNotAffectedPhasesByProjectAndEmploye(projectid.toString(),employeid.toString());
        return ResponseEntity.ok().body(result);
    }

}
