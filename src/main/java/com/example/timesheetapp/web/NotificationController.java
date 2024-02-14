package com.example.timesheetapp.web;

import com.example.timesheetapp.entities.Notification;
import com.example.timesheetapp.repositories.NotificationRepo;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


import java.util.List;
import java.util.UUID;

@Controller
public class NotificationController {

    @Autowired
    private com.example.timesheetapp.repositories.NotificationRepo notificationRepo ;

    @GetMapping("/utilisateur/{id}/notifications")
    public ResponseEntity<?> getNotification(@PathVariable UUID id){
        List<Notification> notifications = notificationRepo.getNoitificationsByUser(id.toString());
        return ResponseEntity.ok().body(notifications);
    }

    @PutMapping("/utilisateur/{id}/notifications/vu")
    public ResponseEntity<?> ToutMarquerCommevu(@PathVariable UUID id){
        notificationRepo.readnotificationbyuser(id.toString());
        JSONObject object = new JSONObject();
        object.put("status",200);
        return ResponseEntity.ok().body(object);
    }




}
