package com.example.timesheetapp.timesheetnotification;

import com.example.timesheetapp.entities.Notification;
import com.example.timesheetapp.repositories.EmployeRep;
import com.example.timesheetapp.repositories.NotificationRepo;
import com.example.timesheetapp.repositories.TimesheetRepo;
import com.example.timesheetapp.services.NotificationServImp;
import org.apache.tomcat.jni.Local;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class timesheetnotificationscheduler {

     @Autowired
     private NotificationServImp notificationServImp ;


     @Autowired
     private TimesheetRepo timesheetRepo ;


     @Autowired
     private EmployeRep employeRep ;

     @Autowired
     NotificationRepo notificationRepo ;

     Logger logger = LoggerFactory.getLogger(timesheetnotificationscheduler.class);


     @Scheduled(cron = "0 35 14 ? * FRI")
     public void sendalertemailtoemployes() throws MessagingException {
         logger.trace("Sending Alert Notification at :"+ LocalDate.now());
         notificationServImp.TimesheetNotificationAlert();

         List<Notification> notifications = new ArrayList<>();
         employeRep.findAll().forEach(employe -> {
                 Notification notification = new Notification();
                 notification.setVu(false);
                 notification.setMessage("N'oubliez pas de soumettre votre feuille cet après-midi");
                 notification.setSentAt(LocalDate.now());
                 notification.setUtilisateur(employe);
                 notifications.add(notification); 
         });  
         
          notificationRepo.saveAll(notifications);

     }

     @Scheduled(cron = "0 05 11 ? * SAT")
     public void sendunsubmtittedtimesheetnotification() throws MessagingException {
          logger.trace("Sending Unsubmitted Timesheet Notification");
          LocalDate now = LocalDate.now();
          //get period of last weeek
            LocalDate dateDebut = now.minusDays(3);
            LocalDate dateFin = dateDebut.minusDays(4);
            List<Object> result =  employeRep.getEmployesThatnotsubmittedtheretimesheetByPeriod(dateDebut,dateFin);
            List<String> emails = new ArrayList<>();

          result.forEach((t)-> {
             emails.add(t.toString());
          });

          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
          String period = dateDebut.format(formatter) + " - " +dateFin.format(formatter) ;

          notificationServImp.UnsubmittedNotification(emails,period);




     }




}
