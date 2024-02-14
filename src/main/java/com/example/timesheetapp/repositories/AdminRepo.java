package com.example.timesheetapp.repositories;


import com.example.timesheetapp.entities.Administrateur;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminRepo extends CrudRepository<Administrateur,UUID> {
}
