package com.example.timesheetapp.services;

import com.example.timesheetapp.entities.Manager;
import com.example.timesheetapp.entities.Project;
import com.example.timesheetapp.entities.ProjectDocument;
import com.example.timesheetapp.repositories.ManagerRepo;
import com.example.timesheetapp.repositories.ProjectDocumentRepo;
import com.example.timesheetapp.repositories.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectServicetImpl implements ProjectManagementService {

    @Autowired
    private ProjectRepo projectRepo ;

    @Autowired
    private ProjectDocumentRepo projectDocumentRepo ;

    @Autowired
    private ManagerRepo managerRepo ;



    @Override
    public Project saveProject(Project project) {
         Project project1 = projectRepo.save(project);
         return project1 ;
    }



    @Override
    public void removeProject(Project project) {
        projectRepo.delete(project);
    }

    @Override
    public List<Project> loadallprojects() {
        return (List<Project>) projectRepo.findAll();
    }

    @Override
    public Optional<Project> loadProjectByID(UUID uuid) {

        return projectRepo.findById(uuid);
    }

    @Override
    public void deleteProject(UUID uuid) {
        this.projectRepo.deleteById(uuid);
    }

    @Override
    public List<Project> loadProjectsByManager(Manager manager) {
            return  projectRepo.findAllByManagerAndArchivedFalse(manager);
    }

    @Override
    public List<Project> loadArchivedProjectsByManager(Manager manager) {
       return  projectRepo.findAllByManagerAndArchivedTrue(manager);
    }


}
