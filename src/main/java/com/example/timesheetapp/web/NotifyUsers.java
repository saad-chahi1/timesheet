package com.example.timesheetapp.web;


import com.example.timesheetapp.entities.Notification;
import com.example.timesheetapp.entities.Utilisateur;
import com.example.timesheetapp.repositories.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotifyUsers {

     @Autowired
     private NotificationRepo notificationRepo ;

     public void notify(List<Notification> notifications){
           notificationRepo.saveAll(notifications);
     }

}
