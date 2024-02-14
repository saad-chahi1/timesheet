package com.example.timesheetapp;

import com.example.timesheetapp.entities.*;
import com.example.timesheetapp.repositories.*;
import com.example.timesheetapp.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@SpringBootApplication
@EnableScheduling

public class TimesheetappApplication  {


	@Autowired
	private AccountService accountService ;

	@Value("${jwt.secret}")
	private String secret ;        //inject the secret key

	@Autowired
	UserRepo userRepo ;

	@Autowired
	ClientRepo clientRepo ;


	@Autowired
	ProjectRepo projectRepo ;



	@Autowired
	AffectationRepo affectationRepo ;



	@Autowired
	TimesheetRepo timesheetRepo ;



	@Autowired
	private BCryptPasswordEncoder encoder ;


	private static final Logger logger = LoggerFactory.getLogger(TimesheetappApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(TimesheetappApplication.class, args);
	}


	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder (){
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {

			Utilisateur a = new Administrateur();
			a.setUsername("saadChahi");
			a.setPassword("12345");
			a.setEmail("S.Chahi@kbm-consulting.com");
			a.setAdresse("Maroc , Casablanca");
			a.setNom("Chahi");
			a.setPrenom("Saad");
			a.setNumeroTele("0634214280");
			a.setDateCreation(new Timestamp(new Date().getTime()));
			a.setEnabled(true);
			a.setRole(Role.ADMIN);


		   Utilisateur utilisateur = userRepo.findByEmail("S.Chahi@kbm-consulting.com").orElse(new Utilisateur());
		   if(utilisateur.getEmail() == null || !utilisateur.getEmail().equals("S.Chahi@kbm-consulting.com")){
			   Utilisateur user = accountService.saveUser(a);
		   }
		};
	}


}
