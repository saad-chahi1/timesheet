package com.example.timesheetapp.web;


import lombok.Data;

@Data
public class PasswordDTO {

    public String password ;

    public String confirmpassword ;

    private String token ;
}
