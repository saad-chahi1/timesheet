package com.example.timesheetapp.web;

import com.example.timesheetapp.entities.*;
import com.example.timesheetapp.helpers.PasswordGenerator;
import com.example.timesheetapp.helpers.file;
import com.example.timesheetapp.repositories.AdminRepo;
import com.example.timesheetapp.repositories.EmployeRep;
import com.example.timesheetapp.repositories.PasswordTokenRepo;
import com.example.timesheetapp.repositories.UserRepo;
import com.example.timesheetapp.services.AccountService;
import com.example.timesheetapp.services.NotificationServImp;
import com.example.timesheetapp.services.SecurityService;
import com.example.timesheetapp.web.DTOs.PasswordsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.DataTruncation;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;


@RestController

public class AccountController {

    @Autowired
    private AccountService accountService ;

    @Autowired
    private NotificationServImp notificationServImp ;

    @Autowired
    private SecurityService securityService ;


    @Autowired
    private PasswordEncoder passwordEncoder ;

    @Autowired
    private  EmployeRep employeRep ;

    @Autowired
    private UserRepo  userRepo ;



    @Autowired
    private PasswordTokenRepo passwordTokenRepo ;



    /////////////////////////////////////////////////////////// Get authenticated user info

    @RequestMapping(value = "/currentuser" , method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Utilisateur>  getInfo(Authentication authentication){
        String username = authentication.getName();
        Utilisateur utilisateur =    accountService.loadUserByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found for this username :" + username));
        return ResponseEntity.ok().body(utilisateur);
    }

///////////////////////////////////////////////////////////// GESTION DES COMPTES

    @GetMapping(value="/administration/users")
    public List<Utilisateur> getAllusers(){

        return accountService.getAllusers();

    }

    @RequestMapping(value="/administration/enable/{id}",method = RequestMethod.POST)
    public ResponseEntity<?> enableaccount (@PathVariable UUID id){
        Utilisateur user = accountService.loadUserById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :" + id));
        user.setEnabled(true);
        accountService.saveUser(user);
        return ResponseEntity.ok(user);
    }

    @RequestMapping(value="/administration/disable/{id}",method = RequestMethod.POST)
    public ResponseEntity<?> disableaccount (@PathVariable UUID id){
        Utilisateur user = accountService.loadUserById(id).orElseThrow(() -> new ResourceNotFoundException("User not found for this id :" + id));
        user.setEnabled(false);
        accountService.saveUser(user);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/administration/users/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id){
        Boolean ispresent = accountService.loadUserById(id).isPresent();
        if (!ispresent){
          return ResponseEntity.badRequest().body("not found");
        }

        Utilisateur user = userRepo.findById(id).orElseThrow(() ->  new ResourceNotFoundException("User not found for this id :" + id));

        if(user.getRole().toString().equals("EMPLOYE")){
            Employe employe = (Employe) user ;
            employe.getProjects().forEach(project -> {
                project.getEquipe().remove(employe);
            });
        }

        accountService.deleteUserbyID(id);
        JSONObject object = new JSONObject();
        object.put("status","200");
        return ResponseEntity.ok(object);
    }

    //managers

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value="/managers",method = RequestMethod.POST)
    public ResponseEntity<?> ajoutermanager(@RequestParam("model") String model , @RequestParam(value = "image",required = false) MultipartFile profileimage ) throws MessagingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Manager manager = objectMapper.readValue(model,Manager.class);

        Boolean ispresent =  accountService.loadUserByEmail(manager.getEmail()).isPresent();
        Boolean ispresent2 = accountService.loadUserByUsername(manager.getUsername()).isPresent();
        if (ispresent){
            JSONObject object = new JSONObject();
            object.put("status","400");
            object.put("response" , "cet email est déjà utilisé. merci d'en saisir un nouveau");
            return ResponseEntity.ok(object);
        }

        if (ispresent2){
            JSONObject object = new JSONObject();
            object.put("status","400");
            object.put("response" , "Ce nom d'utilisateur existe déjà , merci d'en saisir un nouveau");
            return ResponseEntity.ok(object);
        }

        if(profileimage != null){
            ProfileImage profileImage = new ProfileImage();
            profileImage.setType(profileimage.getContentType());
            profileImage.setPicByte(file.compressBytes(profileimage.getBytes()));
            manager.setProfileImage(profileImage);
        }

        manager.setPassword(PasswordGenerator.generatePassword(8).toString());
        manager.setEnabled(true);
        manager.setRole(Role.MANAGER);
        manager.setDateCreation(new Timestamp(new Date().getTime()));
        new Thread(() -> {
            try {
                notificationServImp.RegistrationNotificationMail(manager);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();

        Utilisateur addedadmin =  accountService.saveUser(manager); //adding user

        JSONObject object = new JSONObject();
        object.put("status","200");
        object.put("response",addedadmin);
        return  ResponseEntity.ok(object);
    }

    @PutMapping("/managers/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateManager(@RequestParam("model") String model , @RequestParam(value = "image",required = false) MultipartFile profileimage , @PathVariable UUID id) throws IOException {

        Manager manager = (Manager) accountService.loadUserById(id).orElseThrow(() ->  new ResourceNotFoundException("User not found for this id :" + id));
        ObjectMapper objectMapper = new ObjectMapper();
        Manager manager1 = objectMapper.readValue(model,Manager.class);
        if(!manager1.getEmail().equals(manager.getEmail())){
            Boolean isPresent = this.accountService.loadUserByEmail(manager1.getEmail()).isPresent();
            if (isPresent) {
                JSONObject object = new JSONObject();
                object.put("status","400");
                object.put("response" , "cet email est déjà utilisé. merci d'en saisir un nouveau");
                return ResponseEntity.ok(object);
            }
        }



        if(!manager1.getUsername().equals(manager.getUsername())){
            Boolean isPresent = this.accountService.loadUserByUsername(manager1.getUsername()).isPresent();
            if (isPresent) {
                JSONObject object = new JSONObject();
                object.put("status","400");
                object.put("response" , "Ce nom d'utilisateur existe déjà , merci d'en saisir un nouveau");
                return ResponseEntity.ok(object);
            }
        }

        manager.setEmail(manager1.getUsername());
        manager.setUsername(manager1.getUsername());
        manager.setAdresse(manager1.getAdresse());
        manager.setEmail(manager1.getEmail());
        manager.setNom(manager1.getNom());
        manager.setPrenom(manager1.getPrenom());
        manager.setNumeroTele(manager1.getNumeroTele());

        //

        if (!manager1.getPassword().isEmpty()){
            manager.setPassword(passwordEncoder.encode(manager1.getPassword()));
        }

        if(profileimage != null && manager.getProfileImage() != null ){
            manager.getProfileImage().setType(profileimage.getContentType());
            manager.getProfileImage().setPicByte(file.compressBytes(profileimage.getBytes()));
        }
        JSONObject object = new JSONObject();
        object.put("status",200);
        object.put("response",accountService.saveUserWhitoutEncdoe(manager));
        return ResponseEntity.ok(object);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value="/employes",method = RequestMethod.POST)
    public ResponseEntity<?> ajouterEmploye(@RequestParam("model") String model , @RequestParam(value = "image",required = false) MultipartFile profileimage ) throws MessagingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Employe employe = objectMapper.readValue(model,Employe.class);

        Boolean ispresent =  accountService.loadUserByEmail(employe.getEmail()).isPresent();
        Boolean ispresent2 = accountService.loadUserByUsername(employe.getUsername()).isPresent();
        if (ispresent){
            JSONObject object = new JSONObject();
            object.put("status","400");
            object.put("response" , "cet email est déjà utilisé. merci d'en saisir un nouveau");
            return ResponseEntity.ok(object);
        }

        if (ispresent2){
            JSONObject object = new JSONObject();
            object.put("status","400");
            object.put("response" , "Ce nom d'utilisateur existe déjà , merci d'en saisir un nouveau");
            return ResponseEntity.ok(object);
        }


        if(profileimage != null){
            ProfileImage profileImage = new ProfileImage();
            profileImage.setType(profileimage.getContentType());
            profileImage.setPicByte(file.compressBytes(profileimage.getBytes()));
            employe.setProfileImage(profileImage);
        }

        employe.setEnabled(true);
        employe.setRole(Role.EMPLOYE);
        employe.setDateCreation(new Timestamp(new Date().getTime()));
        employe.setPassword(PasswordGenerator.generatePassword(8).toString());

        new Thread(() -> {
            try {
                notificationServImp.RegistrationNotificationMail(employe);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();

        Utilisateur addedadmin =  accountService.saveUser(employe); //adding user

        JSONObject object = new JSONObject();
        object.put("status","200");
        object.put("response",addedadmin);
        return  ResponseEntity.ok(object);
    }

    @PutMapping("/employes/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateEmploye(@RequestParam("model") String model , @RequestParam(value = "image",required = false) MultipartFile profileimage , @PathVariable UUID id) throws IOException {

        Employe employe = (Employe) accountService.loadUserById(id).orElseThrow(() ->  new ResourceNotFoundException("User not found for this id :" + id));
        ObjectMapper objectMapper = new ObjectMapper();
        Employe newemploye = objectMapper.readValue(model,Employe.class);
        if(!newemploye.getEmail().equals(employe.getEmail())){
            Boolean isPresent = this.accountService.loadUserByEmail(newemploye.getEmail()).isPresent();
            if (isPresent) {
                JSONObject object = new JSONObject();
                object.put("status","400");
                object.put("response" , "cet email est déjà utilisé. merci d'en saisir un nouveau");
                return ResponseEntity.ok(object);
            }
        }

        Boolean ispresent2 = accountService.loadUserByUsername(newemploye.getUsername()).isPresent();


        if (ispresent2){
            JSONObject object = new JSONObject();
            object.put("status","400");
            object.put("response" , "Ce nom d'utilisateur existe déjà , merci d'en saisir un nouveau");
            return ResponseEntity.ok(object);
        }

        employe.setAdresse(newemploye.getAdresse());
        employe.setNom(newemploye.getNom());
        employe.setPrenom(newemploye.getPrenom());
        employe.setNumeroTele(newemploye.getNumeroTele());
        employe.setUsername(newemploye.getUsername());
        employe.setTypeEmploye(newemploye.getTypeEmploye());
        employe.setProfession(newemploye.getProfession());
        employe.setDateembauche(newemploye.getDateembauche());

        if (!newemploye.getPassword().isEmpty()) {
            employe.setPassword(newemploye.getPassword());
        }

        if(profileimage != null && employe.getProfileImage() != null ){
            employe.getProfileImage().setType(profileimage.getContentType());
            employe.getProfileImage().setPicByte(file.compressBytes(profileimage.getBytes()));
        }
        JSONObject object = new JSONObject();
        object.put("status",200);
        object.put("response",accountService.saveUserWhitoutEncdoe(employe));
        return ResponseEntity.ok(object);
    }

    @PutMapping("/administrateurs/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updateadmin(@RequestParam("model") String model , @RequestParam(value = "image",required = false) MultipartFile profileimage , @PathVariable UUID id) throws IOException {

          Administrateur administrateur = (Administrateur) accountService.loadUserById(id).orElseThrow(() ->  new ResourceNotFoundException("User not found for this id :" + id));
          ObjectMapper objectMapper = new ObjectMapper();
          Administrateur newadmin = objectMapper.readValue(model,Administrateur.class);
          if(!newadmin.getEmail().equals(administrateur.getEmail())){
              Boolean isPresent = this.accountService.loadUserByEmail(newadmin.getEmail()).isPresent();
              if (isPresent) {
                  JSONObject object = new JSONObject();
                  object.put("status","400");
                  object.put("response" , "cet email est déjà utilisé. merci d'en saisir un nouveau");
                  return ResponseEntity.ok(object);
              }
          }

        Boolean ispresent2 = accountService.loadUserByUsername(newadmin.getUsername()).isPresent();


        if (ispresent2){
            JSONObject object = new JSONObject();
            object.put("status","400");
            object.put("response" , "Ce nom d'utilisateur existe déjà , merci d'en saisir un nouveau");
            return ResponseEntity.ok(object);
        }

          administrateur.setAdresse(newadmin.getAdresse());
          administrateur.setUsername(newadmin.getUsername());
          administrateur.setNom(newadmin.getNom());
          administrateur.setPrenom(newadmin.getPrenom());
          administrateur.setNumeroTele(newadmin.getNumeroTele());

          if (!newadmin.getPassword().isEmpty()){
              administrateur.setPassword(passwordEncoder.encode(newadmin.getPassword()));
          }

          if(profileimage != null && administrateur.getProfileImage() != null ){
              administrateur.getProfileImage().setType(profileimage.getContentType());
              administrateur.getProfileImage().setPicByte(file.compressBytes(profileimage.getBytes()));
          }
          JSONObject object = new JSONObject();
          object.put("status",200);
          object.put("response",accountService.saveUserWhitoutEncdoe(administrateur));
          return ResponseEntity.ok(object);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/users/block/{id}",method = RequestMethod.PUT)
    public ResponseEntity<?> bloquer(@PathVariable UUID id){
          Utilisateur user = accountService.loadUserById(id).orElseThrow(() ->  new ResourceNotFoundException("User not found for this id :" + id));
          user.setEnabled(false);
          return ResponseEntity.ok().body(userRepo.save(user));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/users/unblock/{id}",method = RequestMethod.PUT)
    public ResponseEntity<?> debloquer(@PathVariable UUID id){
        Utilisateur user = accountService.loadUserById(id).orElseThrow(() ->  new ResourceNotFoundException("User not found for this id :" + id));
        user.setEnabled(true);
        return ResponseEntity.ok().body(userRepo.save(user));
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value="/administrateurs",method = RequestMethod.POST)
    public ResponseEntity<?> ajouter(@RequestParam("model") String model , @RequestParam(value = "image",required = false) MultipartFile profileimage ) throws MessagingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        Administrateur administrateur = objectMapper.readValue(model,Administrateur.class);

        Boolean ispresent =  accountService.loadUserByEmail(administrateur.getEmail()).isPresent();
        Boolean ispresent2 = accountService.loadUserByUsername(administrateur.getUsername()).isPresent();
        if (ispresent){
            JSONObject object = new JSONObject();
            object.put("status","400");
            object.put("response" , "cet email est déjà utilisé. merci d'en saisir un nouveau");
            return ResponseEntity.ok(object);
        }

        if (ispresent2){
            JSONObject object = new JSONObject();
            object.put("status","400");
            object.put("response" , "Ce nom d'utilisateur existe déjà , merci d'en saisir un nouveau");
            return ResponseEntity.ok(object);
        }

        if(profileimage != null){
            ProfileImage profileImage = new ProfileImage();
            profileImage.setType(profileimage.getContentType());
            profileImage.setPicByte(file.compressBytes(profileimage.getBytes())) ;
            administrateur.setProfileImage(profileImage);
        }

        administrateur.setEnabled(true);
        administrateur.setPassword(PasswordGenerator.generatePassword(8).toString());
        administrateur.setRole(Role.ADMIN);
        administrateur.setDateCreation(new Timestamp(new Date().getTime()));
        new Thread(() -> {
            try {
                notificationServImp.RegistrationNotificationMail(administrateur);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();

        Utilisateur addedadmin =  accountService.saveUser(administrateur); //adding user

        JSONObject object = new JSONObject();
        object.put("status","200");
        object.put("response",addedadmin);
        return  ResponseEntity.ok(object);
    }







    @GetMapping("/administration/managers")
    public ResponseEntity<?> getAllManagers(){
        return ResponseEntity.ok(accountService.getAllManagers());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/administrateurs")
    public ResponseEntity<?> getAllAdmins(){
        return ResponseEntity.ok(accountService.getAllAdmins());
    }

    @GetMapping("/administration/employes")
    public ResponseEntity<?> getAllEmployes(){
        return ResponseEntity.ok(accountService.getAllEmploye());
    }

    /*update user info*/

    //////////////////////////////////////////////////////// RESET PASSWORD

    @PostMapping("/resetpassword")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String userEmail) throws MessagingException {
        Utilisateur user = accountService.loadUserByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found for this emil :" + userEmail)); ;
        String token = UUID.randomUUID().toString();
        accountService.createPasswordResetTokenForUser(user,token);

        //Sending Email
        notificationServImp.ResetPasswordEmail(user,token);
        JSONObject json1 = new JSONObject();
        json1.put("ok", "Email a bien été envoyé");
        return ResponseEntity.ok().body(json1);
    }

    @PostMapping("/validtoken/{token}")
    public ResponseEntity validatetoken(@PathVariable String token){
        if ( token == null ) return ResponseEntity.badRequest().body("token is null");
        String result = securityService.validatePasswordResetToken(token) ;
        if ( result != null ){
             return ResponseEntity.badRequest().body("token non valid");
        }else {
             return ResponseEntity.ok("token is valid");
        }
    }
/*
    @GetMapping("/changepassword")
    public String showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("token") String token , HttpServletResponse httpResponse) throws IOException {

        System.out.println("test test");
        String result = securityService.validatePasswordResetToken(token);

        System.out.println("result : "+ result);
        if(result != null) {
            ResponseEntity.badRequest().body(result);
         //   return "redirect:/login.html?lang="
         //           + locale.getLanguage() + "&message=" + result;

        } else {
           // model.addAttribute("token", token);
           // return "redirect:/resetpasswordform.html?lang="+locale.getLanguage();


        }
    } */
   
    @PostMapping(value="/savenewpassword")
    public ResponseEntity<?> changepassword(@RequestBody PasswordDTO passwordDTO){

        if ( ! passwordDTO.password.equals(passwordDTO.confirmpassword)) return ResponseEntity.badRequest().body("Passwords does not match ");
        String result = securityService.validatePasswordResetToken(passwordDTO.getToken()) ;

        if (result != null ) {
             return ResponseEntity.badRequest().body(result);
        } else {

           PasswordResetToken resetToken = accountService.loadPasswordResetTokenByToken(passwordDTO.getToken());
           resetToken.getUser().setPassword(passwordEncoder.encode(passwordDTO.getPassword()));

           return ResponseEntity.ok(passwordTokenRepo.save(resetToken));
        }
    }

    @PostMapping(value="/senemail/{email}")
    public ResponseEntity<?> senemail(@PathVariable String email) throws MessagingException {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        utilisateur.setPassword("TalhiTalhi#1234&");
        new Thread(() -> {
            try {
                notificationServImp.RegistrationNotificationMail(utilisateur);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();

        return ResponseEntity.ok("");

    } 
    
    @PutMapping(value = "/employe/{empid}/profile/password")
    public ResponseEntity<?> changeemployepassword(@PathVariable UUID empid , @RequestBody PasswordsDTO passwordsDTO){
        Employe employe = employeRep.findById(empid).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ empid));
        if(!passwordEncoder.matches(passwordsDTO.getPassword(),employe.getPassword())){
            JSONObject object = new JSONObject();
            object.put("STATUS" ,600);
            return ResponseEntity.ok().body(object);
        }
        if(!passwordsDTO.getNewpassword().equals(passwordsDTO.getConfirmpassword())) {
            JSONObject object = new JSONObject();
            object.put("STATUS" ,400);
            object.put("RESPONSE", "les mots de passe doivent correspondre");
            return ResponseEntity.ok().body(object);
        }

        employe.setPassword(passwordsDTO.getNewpassword());
        accountService.saveUser(employe);
        JSONObject object = new JSONObject();
        object.put("STATUS",200);
        return ResponseEntity.ok().body(object);
    }


    @PutMapping(value = "/manager/{id}/profile/password")
    public ResponseEntity<?> changemanagerpassword(@PathVariable UUID id , @RequestBody PasswordsDTO passwordsDTO){

        Manager manager = (Manager) accountService.loadUserById(id).orElseThrow(() -> new ResourceNotFoundException("Project not found :"+ id));

        if(!passwordEncoder.matches(passwordsDTO.getPassword(),manager.getPassword())){
            JSONObject object = new JSONObject();
            object.put("STATUS" ,600);
            return ResponseEntity.ok().body(object);
        }

        if(!passwordsDTO.getNewpassword().equals(passwordsDTO.getConfirmpassword())) {
            JSONObject object = new JSONObject();
            object.put("STATUS" ,400);
            object.put("RESPONSE", "les mots de passe doivent correspondre");
            return ResponseEntity.ok().body(object);
        }

        manager.setPassword(passwordsDTO.getNewpassword());
        accountService.saveUser(manager);

        JSONObject object = new JSONObject();
        object.put("STATUS",200);
        return ResponseEntity.ok().body(object);
    }


    @PutMapping(value = "/employe/{empid}/profile")
    public ResponseEntity<?> updateprofile( @PathVariable UUID empid , @RequestBody Employe employe ){

        Employe employe1 = employeRep.findById(empid).orElseThrow(() -> new ResourceNotFoundException("Employe not found :"+ empid));
        Boolean isMatch =  employe.getEmail().equals(employe1.getEmail());
        if(isMatch == false) {
            Boolean ispresent = accountService.loadUserByEmail(employe.getEmail()).isPresent();
            if (ispresent){
                employe1.setEmail(employe.getEmail());
                JSONObject object = new JSONObject();
                object.put("STATUS",400);
                object.put("RESPONSE" , "cet email est déjà utilisé. merci d'en saisir un nouveau");
                return ResponseEntity.ok(object);
            }
        }
        employe1.setNom(employe.getNom());
        employe1.setPrenom(employe.getPrenom());
        employe1.setAdresse(employe.getAdresse());
        employe1.setNumeroTele(employe.getNumeroTele());
        Employe employe2 =   employeRep.save(employe1);
        JSONObject object = new JSONObject();
        object.put("STATUS",200);
        object.put("RESPONSE",employe2);
        return ResponseEntity.ok().body(object);
    }

    @PutMapping(value = "/manager/{managerid}/profile")
    public ResponseEntity<?> updatemanagerprofile( @PathVariable UUID managerid , @RequestBody Manager manager2 ){

        Manager manager =  (Manager) accountService.loadUserById(managerid).orElseThrow(() -> new ResourceNotFoundException("Employe not found :"+managerid));
        Boolean isMatch =  manager2.getEmail().equals(manager.getEmail());
        if(isMatch == false) {
            Boolean ispresent = accountService.loadUserByEmail(manager2.getEmail()).isPresent();
            if (ispresent){
                manager.setEmail(manager2.getEmail());
                JSONObject object = new JSONObject();
                object.put("STATUS",400);
                object.put("RESPONSE" , "cet email est déjà utilisé. merci d'en saisir un nouveau");
                return ResponseEntity.ok(object);
            }
        }



        manager.setNom(manager2.getNom());
        manager.setPrenom(manager2.getPrenom());
        manager.setAdresse(manager2.getAdresse());
        manager.setNumeroTele(manager2.getNumeroTele());
        Manager manager1 =  (Manager)accountService.saveUserWhitoutEncdoe(manager);
        JSONObject object = new JSONObject();
        object.put("STATUS",200);
        object.put("RESPONSE",manager1);
        return ResponseEntity.ok().body(object);
    }

}
