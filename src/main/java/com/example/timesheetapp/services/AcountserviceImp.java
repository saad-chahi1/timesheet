package com.example.timesheetapp.services;

import com.example.timesheetapp.entities.*;
import com.example.timesheetapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class AcountserviceImp implements AccountService {


    @Autowired
    private UserRepo userRepo ;

    @Autowired
    private BCryptPasswordEncoder encoder ;

    @Autowired
    private PasswordTokenRepo passwordTokenRepo ;

    @Autowired
    private ManagerRepo managerRepo ;

    @Autowired
    private AdminRepo adminRepo ;

    @Autowired
    private EmployeRepo employeRepo ;



    @Override
    public Utilisateur saveUser(Utilisateur utilisateur) {

        String hashpassword = encoder.encode(utilisateur.getPassword());
        utilisateur.setPassword(hashpassword);

        utilisateur.setPassword(hashpassword);
        return userRepo.save(utilisateur);
    }


    @Override
    public Optional<Utilisateur> loadUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public Optional<Utilisateur> loadUserByEmail(String email) {
        return userRepo.findByEmail(email) ;
    }

    @Override
    public List<Utilisateur> getAllusers() {

         return  (List) userRepo.findAll();

    }

    @Override
    public Optional<Utilisateur> loadUserById(UUID uuid) {
           return  userRepo.findById(uuid);
    }

    @Override
    public List<Utilisateur> loadUsersByRole(Role role) {
        return  userRepo.findAllByRole(role);
    }

    @Override
    public Optional<Utilisateur> loadUserByResetPasswordToken(String token) {
        return Optional.empty();
    }

    @Override
    public PasswordResetToken loadPasswordResetTokenByToken(String token) {
        return passwordTokenRepo.findByToken(token);
    }


    @Override
    public void createPasswordResetTokenForUser(Utilisateur user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY,2);
        resetToken.setExpiryDate(calendar.getTime());
        resetToken.setUser(user);
        resetToken.setToken(token);
        passwordTokenRepo.save(resetToken);
    }

    @Override
    public void deleteUserbyID(UUID id) {
        userRepo.deleteById(id);
    }

    @Override
    public Utilisateur saveUserWhitoutEncdoe(Utilisateur utilisateur) {
        return userRepo.save(utilisateur);
    }



    @Override
    public List<Manager> getAllManagers() {
        return (List<Manager>)this.managerRepo.findAll();
    }

    @Override
    public List<Administrateur> getAllAdmins() {
        return (List<Administrateur>)this.adminRepo.findAll();
    }

    @Override
    public List<Employe> getAllEmploye() {
        return (List<Employe>)this.employeRepo.findAll();
    }


}
