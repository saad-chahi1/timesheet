package com.example.timesheetapp.repositories;


import com.example.timesheetapp.entities.Manager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ManagerRepo extends CrudRepository<Manager, UUID> {
    public Optional<Manager> findByUsername(String username);
}
