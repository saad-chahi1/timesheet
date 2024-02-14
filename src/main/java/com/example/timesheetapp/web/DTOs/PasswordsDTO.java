package com.example.timesheetapp.web.DTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordsDTO {

    private String password ;
    private String newpassword ;
    private String confirmpassword ;
}
