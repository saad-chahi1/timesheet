package com.example.timesheetapp.services;

import com.example.timesheetapp.entities.Affectation;
import com.example.timesheetapp.entities.Timesheet;
import com.example.timesheetapp.entities.Utilisateur;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public interface MailNotificationService {

    public void RegistrationNotificationMail(Utilisateur utilisateur) throws MessagingException;

    public void ResetPasswordEmail(Utilisateur utilisateur ,String token) throws MessagingException;

    public void TimesheetSubmitionNotification (Timesheet timesheet) throws MessagingException;

    public void TimesheetNotificationAlert() throws MessagingException;

    public void timesheetRejectionNotification(Timesheet timesheet) throws MessagingException;

    public void timehseetApprouveNotification(Timesheet timesheet) throws MessagingException;


}
