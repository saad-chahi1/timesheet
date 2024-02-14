package com.example.timesheetapp.security;

import com.example.timesheetapp.entities.Utilisateur;
import com.example.timesheetapp.repositories.UserRepo;
import com.example.timesheetapp.services.AccountService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {


    @Autowired
    AccountService accountService ;

    @Autowired
    UserRepo userRepo ;

    @Autowired
    BCryptPasswordEncoder encoder ;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
         //Utilisateur currentUser = accountService.loadUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username does not exist"));

         Utilisateur currentUser = userRepo.findByUsernameOrEmail(username.trim()).orElseThrow(() -> new UsernameNotFoundException("Username does not exist"));

            return new User(currentUser.getUsername(),currentUser.getPassword(),currentUser.getEnabled(), true, true, true, AuthorityUtils.createAuthorityList(currentUser.getRole().name()));





    }
}
