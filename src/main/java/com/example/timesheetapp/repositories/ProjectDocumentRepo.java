package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.ProjectDocument;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

 @Repository
 public interface ProjectDocumentRepo extends CrudRepository<ProjectDocument,UUID>{

 }
