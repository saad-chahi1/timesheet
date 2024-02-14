package com.example.timesheetapp.services;

import com.example.timesheetapp.entities.*;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {

    public Utilisateur saveUser(Utilisateur utilisateur);
    public Optional<Utilisateur> loadUserByUsername(String username);
    public Optional<Utilisateur> loadUserByEmail(String Email);
    public List<Utilisateur> getAllusers();
    public Optional<Utilisateur> loadUserById(UUID uuid);
    public List<Utilisateur> loadUsersByRole(Role role);
    public Optional<Utilisateur> loadUserByResetPasswordToken(String token) ;
    public PasswordResetToken loadPasswordResetTokenByToken(String token);
    public void createPasswordResetTokenForUser(Utilisateur user, String token);
    public List<Manager> getAllManagers();
    public List<Administrateur> getAllAdmins();
    public List<Employe> getAllEmploye();
    public void deleteUserbyID(UUID id);
    public Utilisateur saveUserWhitoutEncdoe(Utilisateur utilisateur);


}
