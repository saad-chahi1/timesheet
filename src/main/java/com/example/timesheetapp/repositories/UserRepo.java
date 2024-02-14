package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.Role;
import com.example.timesheetapp.entities.Utilisateur;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends CrudRepository<Utilisateur,UUID> {

    Optional<Utilisateur> findByUsername(String username);
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findById(UUID uuid);
    List<Utilisateur> findAllByRole(Role role);

    @Query(value = "SELECT * FROM UTILISATEUR WHERE EMAIL = ?1 OR USERNAME = ?1 ",nativeQuery = true)
    Optional<Utilisateur> findByUsernameOrEmail(String login);

}
