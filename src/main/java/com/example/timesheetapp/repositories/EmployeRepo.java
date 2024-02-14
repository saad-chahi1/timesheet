package com.example.timesheetapp.repositories;


import com.example.timesheetapp.entities.Employe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

@Repository
public interface EmployeRepo extends CrudRepository<Employe, UUID> {


}
