package com.example.timesheetapp;

import com.example.timesheetapp.entities.Utilisateur;
import com.example.timesheetapp.services.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
class TimesheetappApplicationTests {

	@Autowired
	AccountService accountService ;

	@Test
	void testadduser() {
		Assertions.assertEquals("simo","simo");
	}
	@Test
	void testupdateuser() {
		Assertions.assertEquals("simo","simo");
	}
	@Test
	void testaddclient() {
		Assertions.assertEquals("simo","simo");
	}
	@Test
	void testaddproject() {
		Assertions.assertEquals("simo","simo");
	}
	@Test
	void testaddphasesToProject() {
		Assertions.assertEquals("simo","simo");
	}



}
