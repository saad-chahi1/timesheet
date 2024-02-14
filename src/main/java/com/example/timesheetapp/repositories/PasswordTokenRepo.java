package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.PasswordResetToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PasswordTokenRepo extends CrudRepository<PasswordResetToken, UUID> {

     public PasswordResetToken findByToken(String token);
}
