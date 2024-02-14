package com.example.timesheetapp.services;

import com.example.timesheetapp.entities.Manager;
import com.example.timesheetapp.entities.Project;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectManagementService {

     public Project saveProject(Project project);
     public void    removeProject(Project project);
     public List<Project> loadallprojects();
     public Optional<Project> loadProjectByID(UUID uuid);
     public  List<Project> loadProjectsByManager(Manager manager);
     public List<Project> loadArchivedProjectsByManager(Manager manager);
     public void deleteProject(UUID uuid);
}
