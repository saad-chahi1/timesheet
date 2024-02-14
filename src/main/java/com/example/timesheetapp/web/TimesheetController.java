package com.example.timesheetapp.web;


import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.example.timesheetapp.entities.*;
import com.example.timesheetapp.repositories.ManagerRepo;
import com.example.timesheetapp.repositories.NotificationRepo;
import com.example.timesheetapp.repositories.PhaseTimesheetRep;
import com.example.timesheetapp.repositories.TimesheetRepo;
import com.example.timesheetapp.services.AccountService;
import com.example.timesheetapp.services.EmployeService;
import com.example.timesheetapp.services.NotificationServImp;
import com.example.timesheetapp.services.TimesheetService;
import com.example.timesheetapp.web.DTOs.Dates;
import com.example.timesheetapp.web.DTOs.ReportDTO;
import com.example.timesheetapp.web.DTOs.TimesheetValidationDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
public class TimesheetController {

    @Autowired
    private TimesheetRepo timesheetRepo;

    @Autowired
    private EmployeService employeService;

    @Autowired
    private ManagerRepo managerRepo ;


    @Autowired
    private NotificationServImp notificationServImp ;


    @Autowired
    private AccountService accountService ;


    @Autowired
    private TimesheetService timesheetService;

    @Autowired
    private NotifyUsers notifyUsers ;

    @Autowired
    private NotificationRepo notificationRepo ;


    @Autowired
    private PhaseTimesheetRep phaseTimesheetRep ;

    @GetMapping("/employe/{id}/timesheets")
    public ResponseEntity<?> geSubmittedtTimesheetsByEmploye(@PathVariable UUID id) {

        Employe employe = employeService.loadEmployeById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :" + id));

        List<Timesheet> timesheets = timesheetRepo.findAllByEmployeAndStatusNot(employe, TimesheetStatus.SAVED);

        timesheets.forEach(timesheet -> {
            Collections.sort(timesheet.getJourTimesheets(), Comparator.comparing(JourTimesheet::getDate));
        });

       return ResponseEntity.ok().body(timesheets);

    }

     @GetMapping("/employe/{id}/timesheets/{status}")
     public ResponseEntity<?> getTimesheetsByStatus(@PathVariable UUID id , @PathVariable String status){
         Employe employe = employeService.loadEmployeById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :" + id));
         TimesheetStatus timesheetStatus = TimesheetStatus.valueOf(status);
         List<Timesheet> timesheets =   this.timesheetRepo.findAllByEmployeAndStatus(employe,timesheetStatus);
         return ResponseEntity.ok().body(timesheets);
    }

    @GetMapping("/manager/{id}/timesheets/{dateD}/{dateF}/{status}")
    public ResponseEntity<?> getTimesheetsByEmployeAndPeriodAndStatus(@PathVariable UUID id , @PathVariable String dateD , @PathVariable String dateF , @PathVariable String status){
        Employe employe = employeService.loadEmployeById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :" + id));
        TimesheetStatus timesheetStatus = TimesheetStatus.valueOf(status);
      /*  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateDebut =  LocalDate.parse(dateD,formatter);
        LocalDate dateFin =    LocalDate.parse(dateF,formatter); */
        List<Timesheet> timesheets = this.timesheetRepo.fetchAllByEmployeAndDateDebutAndDateFinAndStatus(employe.getId().toString(),timesheetStatus.toString(),dateD,dateF);
        return ResponseEntity.ok().body(timesheets);
    }

    @GetMapping("/manager/timesheets/{dateD}/{dateF}/{status}")
    public ResponseEntity<?> getAllTimesheetsPeriodAndStatus( @PathVariable String dateD , @PathVariable String dateF , @PathVariable String status){

        TimesheetStatus timesheetStatus = TimesheetStatus.valueOf(status);
        /*
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateDebut =  LocalDate.parse(dateD,formatter);
        LocalDate dateFin =    LocalDate.parse(dateF,formatter); */
        List<Timesheet> timesheets = this.timesheetRepo.fetchAllByDateDebutAndDateFinAndStatus(timesheetStatus.toString(),dateD,dateF);
        return ResponseEntity.ok().body(timesheets);
    }

    @GetMapping("/manager/timesheets/{dateD}/{dateF}")
    public ResponseEntity<?> getAllTimesheetsPeriod( @PathVariable String dateD , @PathVariable String dateF){

        /*
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateDebut =  LocalDate.parse(dateD,formatter);
        LocalDate dateFin =    LocalDate.parse(dateF,formatter); */
        List<Timesheet> timesheets = this.timesheetRepo.fetchAllByDateDebutAndDateFin(dateD,dateF);
        return ResponseEntity.ok().body(timesheets);

    }

    @GetMapping("/manager/{id}/timesheets/{dateD}/{dateF}")
    public ResponseEntity<?> getTimesheetsByEmployeAndPeriod(@PathVariable UUID id , @PathVariable String dateD , @PathVariable String dateF){
        Employe employe = employeService.loadEmployeById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :" + id));

      /*  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateDebut =  LocalDate.parse(dateD,formatter);
        LocalDate dateFin =    LocalDate.parse(dateF,formatter); */
        List<Timesheet> timesheets = this.timesheetRepo.fetchAllByEmployeAndDateDebutAndDateFin(employe.getId().toString(),dateD,dateF);
        return ResponseEntity.ok().body(timesheets);
    }



    @GetMapping("/manager/timesheets")
    public ResponseEntity<?> getAllTimesheets(){
        return ResponseEntity.ok().body(timesheetRepo.findAll());

    }

    @GetMapping("/manager/timesheets/{status}")
    public ResponseEntity<?> getAllTimesheetsbyStatus(@PathVariable String status){

        TimesheetStatus timesheetStatus = TimesheetStatus.valueOf(status);
        List<Timesheet> timesheets =   this.timesheetRepo.findAllByStatus(timesheetStatus);
        return ResponseEntity.ok().body(timesheets);

    }

    @GetMapping("/employe/{id}/timesheet/{dateD}/{dateF}")
    public ResponseEntity<?> gettimesheet(@PathVariable UUID id , @PathVariable String dateD , @PathVariable String dateF ) throws JsonProcessingException {
        //Dates dates =   new ObjectMapper().readValue(dateModal,Dates.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateDebut =  LocalDate.parse(dateD,formatter);
        LocalDate dateFin =    LocalDate.parse(dateF,formatter);
        Employe employe = employeService.loadEmployeById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :" + id));
        Optional<Timesheet> timesheet1 =   timesheetRepo.findByDateDebutAndDateFinAndEmploye(dateDebut,dateFin,employe);

        if(timesheet1.isPresent()){
            //ordering
            Timesheet timesheet = timesheet1.get();
            Collections.sort(timesheet.getJourTimesheets(), Comparator.comparing(JourTimesheet::getDate));

           JSONObject object = new JSONObject();
           object.put("STATUS",200);
           object.put("RESPONSE",timesheet);
           return ResponseEntity.ok().body(object);
        }else{
           JSONObject object = new JSONObject();
           object.put("STATUS",404);
           return ResponseEntity.ok().body(object);
       }
    }
  

   @PutMapping("employe/{employeid}/timesheets/{timesheetid}/submit")
   public ResponseEntity<?> submittimesheet(@PathVariable UUID  employeid , @PathVariable UUID timesheetid){
       Employe employe = employeService.loadEmployeById(employeid).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :" + employeid));
       Timesheet timesheet =  timesheetRepo.findById(timesheetid).orElseThrow(() -> new ResourceNotFoundException("Timesheet not found for this id :" + timesheetid));
       timesheet.setStatus(TimesheetStatus.SUBMITTED);
       timesheet.setSubmittedAt(LocalDate.now());
       timesheet.resettimesheetdetails();
       JSONObject object = new JSONObject();
       new Thread(() -> {
           try {
              notificationServImp.TimesheetSubmitionNotification(timesheet);
           } catch (MessagingException e) {
               e.printStackTrace();
           }
       }).start();
        timesheetRepo.save(timesheet);
        //notify managers

        List<Manager> managers = (List<Manager>) managerRepo.findAll();
        List<Notification> notifications = new ArrayList<>();
        managers.forEach(manager -> {
            Notification notification = new Notification();
            notification.setMessage( employe.getNom()+" "+employe.getPrenom()+" a soumis sa feuille de temps du "+timesheet.getDateDebut() +" au "+timesheet.getDateFin());
            notification.setSentAt(LocalDate.now());
            notification.setVu(false);
            notification.setUtilisateur(manager);
            notifications.add(notification);
        });

        notifyUsers.notify(notifications);



       object.put("STATUS",200);
       return  ResponseEntity.ok().body(object);
   }



    @PostMapping("/employe/{id}/timesheets")
    public ResponseEntity<?> postandupdatetimesheets(@RequestBody Timesheet timesheet, @PathVariable UUID id) {
        Employe employe = employeService.loadEmployeById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :" + id));
        if (timesheet.getId() == null) {
            Boolean ispresent = timesheetRepo.findByDateDebutAndDateFinAndEmploye(timesheet.getDateDebut(), timesheet.getDateFin() , employe).isPresent();
            if (!ispresent) {
                timesheet.getJourTimesheets().forEach((j)-> {
                     j.setTimesheet(timesheet);
                     j.getPhaseTimesheets().forEach((p)->{
                          p.setJourTimesheet(j);
                         // p.getPhase().getPhaseTimesheets().add(p);   //added
                     });
                });

                timesheet.setEmploye(employe);
                timesheet.setLastupdate(LocalDateTime.now());
                timesheet.setStatus(TimesheetStatus.SAVED);
                Timesheet t =  timesheetRepo.save(timesheet);
                System.out.print("TIMESHEET ID : "+ t.getId());
                return ResponseEntity.ok(t);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("please change dates");
            }
        } else {
            Timesheet timesheet1 = timesheetRepo.findById(timesheet.getId()).get();
            timesheet1.setDescription(timesheet.getDescription());
            timesheet1.setLastupdate(LocalDateTime.now());
            timesheet1.setTotalduration(timesheet.getTotalduration());
            timesheet1.getJourTimesheets().clear();
            timesheet.getJourTimesheets().forEach((j) -> {
                    /// j.setTimesheet(timesheet1);//
                    j.getPhaseTimesheets().forEach(phaseTimesheet -> {
                        phaseTimesheet.setJourTimesheet(j);
                    });

                    timesheet1.addjourtimesheet(j);

                });
            Timesheet timesheetf =  timesheetRepo.save(timesheet1);
            return ResponseEntity.ok(timesheetf);

        }
    }


    @PostMapping("/manager/timesheetadministration/{timesheetid}/reject/{rejectraison}")
    public ResponseEntity<?> timesheetrejection(@PathVariable UUID timesheetid , Authentication authentication , @PathVariable String rejectraison ){

        String username = authentication.getName();
        Utilisateur manager =    accountService.loadUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found for this username :" + username));
        Timesheet timesheet = timesheetRepo.findById(timesheetid).orElseThrow(() -> new ResourceNotFoundException("Timesheet not found for this id :" + timesheetid));
        timesheet.setStatus(TimesheetStatus.REJECTED);
        timesheet.setRaisonRejection(rejectraison);
        timesheet.setRejectedBy(manager.getNom()+" "+manager.getPrenom());
        timesheet.setRejectedAt(LocalDateTime.now());
        Timesheet newtimesheet = timesheetRepo.save(timesheet);

        new Thread(() -> {
            try {

                notificationServImp.timesheetRejectionNotification(timesheet);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();

        //Persisting notification
        Notification notification = new Notification();
        notification.setMessage(manager.getNom()+" "+manager.getPrenom()+" a refusé votre feuille de temps du "+timesheet.getDateDebut()+" au "+timesheet
                .getDateFin());
        notification.setUtilisateur(timesheet.getEmploye());
        notification.setVu(false);
        notification.setSentAt(LocalDate.now());
        notificationRepo.save(notification);

        JSONObject object = new JSONObject();
        object.put("STATUS",200);
        object.put("RESPONSE",newtimesheet);
        return ResponseEntity.ok().body(object);
    }

    @PostMapping("/manager/timesheetadministration/{timesheetid}/approuve")
    public ResponseEntity<?> timesheetapprouve(@PathVariable UUID timesheetid , Authentication authentication){

        String username = authentication.getName();
        Utilisateur manager =    accountService.loadUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found for this username :" + username));
        Timesheet timesheet = timesheetRepo.findById(timesheetid).orElseThrow(() -> new ResourceNotFoundException("Timesheet not found for this id :" + timesheetid));
        timesheet.setStatus(TimesheetStatus.APPROUVED);
        timesheet.setApprouvedBy(manager.getNom()+" "+manager.getPrenom());
        timesheet.setApprouvedAt(LocalDateTime.now());
        Timesheet newtimesheet = timesheetRepo.save(timesheet);

        new Thread(() -> {
            try {
                notificationServImp.timehseetApprouveNotification(timesheet);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();

        //Persisting notification in database

        Notification notification = new Notification();
        notification.setMessage(manager.getNom()+" "+manager.getPrenom()+" a approuvé votre feuille du "+timesheet.getDateDebut()+"/"+timesheet
        .getDateFin());
        notification.setUtilisateur(timesheet.getEmploye());
        notification.setVu(false);
        notification.setSentAt(LocalDate.now());
        notificationRepo.save(notification);


        JSONObject object = new JSONObject();
        object.put("STATUS",200);
        object.put("RESPONSE",newtimesheet);
        return ResponseEntity.ok().body(object);

    }

    @GetMapping("/employe/{id}/report/{year}")
    public ResponseEntity<?> getReport(@PathVariable UUID id, @PathVariable String year){
        Employe employe = employeService.loadEmployeById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :" + id));
        List<Object> values = phaseTimesheetRep.getApprouvedHoursReportByEmploye(year,employe.getId().toString());
        return ResponseEntity.ok().body(values);
   }


    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
   @GetMapping("/timesheets/report/{empid}/{from}/{to}")
    public ResponseEntity<?> getAllReport(@PathVariable String empid , @PathVariable String from , @PathVariable String to){
        List<Object> objects = new ArrayList<>() ;
        if(empid.equals("undefined") && from.equals("undefined") && to.equals("undefined")){
            objects = timesheetRepo.getTimesheetsReport();
        }else if ( !empid.equals("undefined") && from.equals("undefined") && to.equals("undefined")){

            objects = timesheetRepo.getTimesheetReportByEmploye(empid.toString());

        }

        return ResponseEntity.ok().body(objects);

   }
/*
   @GetMapping("/test")
   public ResponseEntity<?> testemaileconcodage() throws MessagingException {
         Timesheet timesheet = new Timesheet();
         Employe employe = new Employe();
         employe.setEmail("M.Talhi@kbm-consulting.com");
         employe.setNom("TALHI");
         timesheet.setEmploye(employe);
         timesheet.setRaisonRejection("Test : éééééééé ààààààààà àààààààà");

         this.notificationServImp.timesheetRejectionNotification(timesheet);

         return ResponseEntity.ok().body("Success");


   }
 */


}
