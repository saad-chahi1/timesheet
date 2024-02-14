package com.example.timesheetapp.security;

import com.example.timesheetapp.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true )
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailServiceImpl userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AccountService accountService ;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    /*    auth.inMemoryAuthentication()
             .withUser("talhimohammed")
             .password("testtest")
             .roles("ADMIN"); */
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/login/**").permitAll();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/administration/**").hasAnyAuthority("ADMIN","MANAGER");
        http.authorizeRequests().antMatchers("/manager/**").hasAuthority("MANAGER");
        http.authorizeRequests().antMatchers("/employe/**").hasAnyAuthority("ADMIN","MANAGER","EMPLOYE");
        http.authorizeRequests().antMatchers("/changepassword/**").permitAll();
        http.authorizeRequests().antMatchers("/resetpassword/**").permitAll();
        http.authorizeRequests().antMatchers("/savenewpassword/**").permitAll();
        http.authorizeRequests().antMatchers("/projects/**").hasAnyAuthority("ADMIN","MANAGER");
        http.authorizeRequests().antMatchers("/affectation//**").hasAnyAuthority("ADMIN","MANAGER");
        http.authorizeRequests().antMatchers("/clients/**").hasAnyAuthority("ADMIN","MANAGER","EMPLOYE");
        http.authorizeRequests().antMatchers("/employe/**").hasAnyAuthority("ADMIN","MANAGER","EMPLOYE");
        http.authorizeRequests().antMatchers("/validtoken/**").permitAll();
        http.authorizeRequests().antMatchers(("/affectations")).permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(new JWTAuthenticationFilter(authenticationManager()));
        http.addFilterBefore(new JWTAuthorizationFiler(accountService), UsernamePasswordAuthenticationFilter.class);

    }

}
