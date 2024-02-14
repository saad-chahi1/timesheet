package com.example.timesheetapp.services;


import com.example.timesheetapp.configuration.CLIENT_URL;
import com.example.timesheetapp.entities.*;
import com.example.timesheetapp.gmailaccount.GmailAccount;
import com.example.timesheetapp.repositories.EmployeRep;
import com.example.timesheetapp.repositories.ManagerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class NotificationServImp implements MailNotificationService {


    @Autowired
    private ManagerRepo managerRepo ;



    @Autowired
    private EmployeRep employeRep ;

    Properties props ;

    Session session ;

    public NotificationServImp(){

        this.props = new Properties() ;
       /*gmail google*/
     //   props.put("mail.smtp.auth", "true") ;
     //   props.put("mail.smtp.starttls.enable", "true") ;
     //   props.put("mail.smtp.host", "smtp.gmail.com") ;
     //   props.put("mail.smtp.port", "587") ;

        //outlook
        props.put("mail.smtp.auth", "true") ;
        props.put("mail.smtp.starttls.enable", "true") ;
        props.put("mail.smtp.host", "smtp.office365.com") ;
        props.put("mail.smtp.port", "587") ;

        this.session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(GmailAccount.Email, GmailAccount.Password);
            }
        });

    }




    @Override
    @Async
    public void RegistrationNotificationMail(Utilisateur utilisateur) throws MessagingException {
/*
        Properties props = new Properties() ;
        props.put("mail.smtp.auth", "true") ;
        props.put("mail.smtp.starttls.enable", "true") ;
        props.put("mail.smtp.host", "smtp.gmail.com") ;
        props.put("mail.smtp.port", "587") ; */

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(GmailAccount.Email, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(utilisateur.getEmail()));
        msg.setSubject("CREATION DE VOTRE COMPTE MYTIME");

        String content = "<html>\n" +
                "<body>\n" +
                 "<div>Bienvenue sur MyTime ,</div>"+
                 "<br>"+
                 "<div>Votre compte est pr&ecirc;t et vous pouvez commencer &agrave; utiliser MyTime d&egrave;s maintenant</div>"+
                 "<br>"+
                 "<div>Vos identifiants de connexion sont les suivants :</div>"
                 +
                " <p> \n" +
                " \n" +
                " <div>Nom d&#039;utilisateur : "+ utilisateur.getUsername() +" ( Vous pouvez utiliser votre e-mail ) </div>  \n" +
                " <br>\n" +
                " <div>Mot de passe : "+utilisateur.getPassword()+"</div> \n" +
                " <br>\n" +
                " <strong>Une fois connect&eacute;(e) , Compl&eacute;ter votre profil et cr&eacute;er un nouveau mot de passe de votre choix </strong> \n" +
                " <br>\n" +
                " <br>\n" +
                " <div>Cliquez ici pour acc&eacute;der &agrave; l'application : <a href="+ CLIENT_URL.url+" >MyTime</a></div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";


        msg.setContent(content,"text/html; charset=UTF-8");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Tutorials point email", "text/html");

        Transport.send(msg);
    }

    @Override
    public void ResetPasswordEmail(Utilisateur utilisateur , String token) throws MessagingException {

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(GmailAccount.Email, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(utilisateur.getEmail()));
        msg.setSubject("Réinitialisez votre mot de passe");


        String content = "\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "<div>Bonjour , </div> \n" +
                "<br> \n" +
                "<div>Pas d&#039;inqui&eacute;tudes. Vous pouvez r&eacute;initialiser votre mot de passe MyTime en cliquant sur le lien ci-dessous : </div> \n" +
                "\n" +
                "<a href='"+CLIENT_URL.url+"resetpassword/"+token+"'>R&eacute;initialiser le mot de passe</a> \n" +
                "<br> \n" +
                "<br>\n" +
                "<strong>\n" +
                " Si vous n'avez pas demand&eacute; la r&eacute;initialisation de votre mot de passe, vous pouvez supprimer cet e-mail \n" +
                "</strong> \n" +
                "<br> \n" +
                "<br>\n" +
                "<div>Cordialement , l&#039;&eacute;quipe MyTime</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        msg.setContent(content,"text/html; charset=UTF-8");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Tutorials point email", "text/html");

        Transport.send(msg);
    }


    public void TimesheetSubmitionNotification (Timesheet timesheet) throws MessagingException {


        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(GmailAccount.Email, false));

        List<Address> adresslist = new ArrayList<>();
        String emails = "" ;

        List<Manager> managers = (List<Manager>) managerRepo.findAll();
        for(int i = 0 ; i < managers.size() ; i++){

            if( i == managers.size() - 1) {
                emails+=managers.get(i).getEmail();
                break;
            }
                emails+=managers.get(i).getEmail()+",";
        }

        String[] recipientList = emails.split(",");
        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
        int counter = 0;
        for (String recipient : recipientList) {
            recipientAddress[counter] = new InternetAddress(recipient.trim());
            counter++;
        }

        msg.setRecipients(Message.RecipientType.TO, recipientAddress);


        msg.setSubject("Une nouvelle feuille de temps à valider !");

        String content = "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, td, th {\n" +
                "  border: 1px solid black;\n" +
                "}\n" +
                "\n" +
                "table {\n" +
                "  width: 100%;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h2>SEMAINE : <strong>"+timesheet.getDateDebut()+" - "+timesheet.getDateFin()+"</strong></h2>\n" +
                "\n" +
                "<table style='width: 100%; border-collapse: collapse; border: 1px solid black;'>\n" +
                "  <tr>\n" +
                "    <th style='border: 1px solid black;'>Employ&eacute;</th>\n" +
                "    <th style='border: 1px solid black;'>Date de d&eacute;but</th> \n" +
                "    <th style='border: 1px solid black;'>Date de Fin</th> \n" +
                "    <th style='border: 1px solid black;'>Nombre total d&#039;heures</th> \n" +
                "    <th style='border: 1px solid black;'>&Eacute;tat</th>\n" +
                "    \n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td style='border: 1px solid black; text-align:center'>"+timesheet.getEmploye().getNom() +"</td>\n" +
                "    <td style='border: 1px solid black; text-align:center'>"+timesheet.getDateDebut()+"</td> \n" +
                "    <td style='border: 1px solid black; text-align:center ;'>"+timesheet.getDateFin()+"</td> \n" +
                "    <td style='border: 1px solid black;text-align : center'>"+timesheet.getTotalduration()+"</td>\n" +
                "    <td style='border: 1px solid black; text-align : center ;'>En attente d&#039;approbation</td>\n" +
                "  </tr>\n" +
                "\n" +
                "</table>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        msg.setContent(content,"text/html; charset=UTF-8");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Tutorials point email", "text/html");

        Transport.send(msg);

    }

    public void TimesheetNotificationAlert() throws MessagingException {


        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(GmailAccount.Email, false));

        List<Address> adresslist = new ArrayList<>();
        String emails = "" ;

        List<Employe> employes = (List<Employe>) employeRep.findAll();
        for(int i = 0 ; i < employes.size() ; i++){

            if( i == employes.size() - 1) {
                emails+=employes.get(i).getEmail();
                break;
            }
            emails+=employes.get(i).getEmail()+",";
        }

        String[] recipientList = emails.split(",");
        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
        int counter = 0;
        for (String recipient : recipientList) {
            recipientAddress[counter] = new InternetAddress(recipient.trim());
            counter++;
        }

        msg.setRecipients(Message.RecipientType.TO, recipientAddress);


        msg.setSubject("Timesheet Alert");

        String content = "<html> <body> " +
                "" +
                "<div>Bonjour " +
                "" + "<br>" +
                "</div>\n<div>N&#039;oubliez pas de remplir et de soumettre votre feuille de temps cet apr&egrave;s midi ,</div><br><div>Bonne journ&eacute;e</div>\n "
                + "<div><strong>MyTime Powered By KBM Consulting</strong></div>" +
                "</body> </html>" ;



        msg.setContent(content,"text/html; charset=UTF-8");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Tutorials point email", "text/html");

        Transport.send(msg);

    }


    @Override
    public void timehseetApprouveNotification(Timesheet timesheet) throws MessagingException {


        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(GmailAccount.Email, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(timesheet.getEmploye().getEmail()));
        msg.setSubject("Votre feuille de temps a été approuvée");

        String content = "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, td, th {\n" +
                "  border: 1px solid black;\n" +
                "}\n" +
                "\n" +
                "table {\n" +
                "  width: 100%;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h2>SEMAINE : <strong>"+timesheet.getDateDebut()+" - "+timesheet.getDateFin()+"</strong></h2>\n" +
                "\n" +
                "<table style='width: 100%; border-collapse: collapse; border: 1px solid black;'>\n" +
                "  <tr>\n" +
                "    <th style='border: 1px solid black;'>Employ&eacute;</th>\n" +
                "    <th style='border: 1px solid black;'>Date de d&eacute;but</th> \n" +
                "    <th style='border: 1px solid black;'>Date de Fin</th> \n" +
                "    <th style='border: 1px solid black;'>Nombre total d'heures</th> \n" +
                "    <th style='border: 1px solid black;'>&Eacute;tat</th>\n" +
                "    \n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td style='border: 1px solid black; text-align:center'>"+timesheet.getEmploye().getNom() +"</td>\n" +
                "    <td style='border: 1px solid black; text-align:center'>"+timesheet.getDateDebut()+"</td> \n" +
                "    <td style='border: 1px solid black; text-align:center ;'>"+timesheet.getDateFin()+"</td> \n" +
                "    <td style='border: 1px solid black;text-align : center'>"+timesheet.getTotalduration()+"</td>\n" +
                "    <td style='border: 1px solid black; text-align : center ; background-color: green ; color : white ;'>Approuv&eacute;e</td>\n" +
                "  </tr>\n" +
                "\n" +
                "\n</table>\n <br> <br>" +

                "\n" +
                "</body>\n" +
                "</html>";
                msg.setContent(content,"text/html; charset=UTF-8");
                msg.setSentDate(new Date());

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent("Tutorials point email", "text/html");
                Transport.send(msg);



    }



    @Override
    public void timesheetRejectionNotification(Timesheet timesheet) throws MessagingException {



        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(GmailAccount.Email, false));

        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(timesheet.getEmploye().getEmail()));
        msg.setSubject("Votre feuille de temps a été rejetée");

        String content = "<html>\n" +
                "<head>\n" +
                "<style>\n" +
                "table, td, th {\n" +
                "  border: 1px solid black;\n" +
                "}\n" +
                "\n" +
                "table {\n" +
                "  width: 100%;\n" +
                "  border-collapse: collapse;\n" +
                "}\n" +
                "</style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<h2>SEMAINE : <strong>"+timesheet.getDateDebut()+" - "+timesheet.getDateFin()+"</strong></h2>\n" +
                "\n" +
                "<table style='width: 100%; border-collapse: collapse; border: 1px solid black;'>\n" +
                "  <tr>\n" +
                "    <th style='border: 1px solid black;'>Employ&eacute;</th>\n" +
                "    <th style='border: 1px solid black;'>Date de d&eacute;but</th> \n" +
                "    <th style='border: 1px solid black;'>Date de Fin</th> \n" +
                "    <th style='border: 1px solid black;'>Nombre total d'heures</th> \n" +
                "    <th style='border: 1px solid black;'>&Eacute;tat</th>\n" +
                "    \n" +
                "  </tr>\n" +
                "  <tr>\n" +
                "    <td style='border: 1px solid black; text-align:center'>"+timesheet.getEmploye().getNom() +"</td>\n" +
                "    <td style='border: 1px solid black; text-align:center'>"+timesheet.getDateDebut()+"</td> \n" +
                "    <td style='border: 1px solid black; text-align:center ;'>"+timesheet.getDateFin()+"</td> \n" +
                "    <td style='border: 1px solid black;text-align : center'>"+timesheet.getTotalduration()+"</td>\n" +
                "    <td style='border: 1px solid black; text-align : center ; background-color: red ; color : white ;'>Rejet&eacute;e</td>\n" +
                "  </tr>\n" +
                "\n" +
                "\n</table>\n <br> <br>" +
                "\n<div> <strong> Commentaire </strong> : <div/>"
                 + "<div>" + timesheet.getRaisonRejection() + "</div>" +

                "\n" +
                "</body>\n" +
                "</html>";

        msg.setContent(content,"text/html; charset=UTF-8");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Tutorials point email", "text/html");

        Transport.send(msg);

    }


    public void UnsubmittedNotification( List<String> emails , String period ) throws MessagingException {
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(GmailAccount.Email, false));

        List<Address> adresslist = new ArrayList<>();
        String useremails = "" ;


        for(int i = 0 ; i < emails.size() ; i++){

            if( i == emails.size() - 1) {
                useremails+=emails.get(i);
                break;
            }
            useremails += emails.get(i)+",";
        }

        String[] recipientList = useremails.split(",");
        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
        int counter = 0;
        for (String recipient : recipientList) {
            recipientAddress[counter] = new InternetAddress(recipient.trim());
            counter++;
        }

        msg.setRecipients(Message.RecipientType.TO, recipientAddress);
        msg.setSubject("Feuille de temps non soumise !");

        String content = "<html>\n" +
                "\n" +
                "<body>\n" +
                "\n" +
                " <div> \n" +
                "    <div> Bonjour ,</div>\n" +
                " \n" +
                "    <p>Vous n&#039;avez pas soumis votre feuille de temps pour la p&eacute;riode , \n" + period +
                "         <p>Merci de la soumettre le plus t&ocirc;t possible</p>\n" +
                "    </p>\n" +
                "\n" +
                "\n" +
                "    <div>Bonne journ&eacute;e .</div> \n" +
                "    <br> \n" +
                "    <br>\n" +
                "    <div>MyTime powered by KBM Consulting</div> \n" +
                "\n" +
                "\n" +
                " </div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        msg.setContent(content,"text/html; charset=UTF-8");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Tutorials point email", "text/html");

        Transport.send(msg);


    }
}

