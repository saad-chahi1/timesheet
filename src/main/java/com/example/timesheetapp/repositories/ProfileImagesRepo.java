package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.ProfileImage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProfileImagesRepo extends CrudRepository<ProfileImage, UUID> {

}
