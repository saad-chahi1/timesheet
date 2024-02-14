package com.example.timesheetapp.services;

import com.example.timesheetapp.entities.PasswordResetToken;
import com.example.timesheetapp.repositories.PasswordTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class SecurityService {

    @Autowired
    private PasswordTokenRepo  passwordTokenRepo ;

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepo.findByToken(token);
        return !isTokenFound(passToken) ? "Token Non Trouvé"
                : isTokenExpired(passToken) ? "Session expirée "
                : null;
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
