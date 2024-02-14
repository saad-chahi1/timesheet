package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.Affectation;
import com.example.timesheetapp.entities.Employe;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AffectationRepo extends CrudRepository<Affectation,UUID> {

  public List<Affectation> findAllByEmploye(Employe employe);
}
