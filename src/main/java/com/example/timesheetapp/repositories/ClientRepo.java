package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.Client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepo extends CrudRepository<Client,UUID> {

  /*  @Query(value = "SELECT project.nom  , project.status , project.duree , CAST(SUM(phase_timesheet.DURATION) AS VARCHAR)   FROM  phase_timesheet  \n" +
            "               INNER JOIN phase \n" +
            "                  ON   \n" +
            "                    phase_timesheet.phase_id  = phase.id  \n" +
            "                       INNER JOIN project \n" +
            "                         ON\n" +
            "                          phase.project_id = project.id \n" +
            "                              INNER JOIN jour_timesheet \n" +
            "                                   ON \n" +
            "                                     jour_timesheet.id = phase_timesheet.jourtimesheet_id \n" +
            "                                        INNER JOIN timesheet \n" +
            "                                               ON jour_timesheet.timesheet_id = timesheet.id      \n" +
            "                                                \n" +
            "                                                    WHERE timesheet.status = 'APPROUVED' AND CAST(project.client_id AS VARCHAR) = ?1   \n" +
            "                                                            GROUP BY project.nom" , nativeQuery = true) */

    @Query(value = "SELECT project.nom , project.status, project.duree , COALESCE(CAST(SUM(phase_timesheet.DURATION) AS VARCHAR),'00:00:00') as sum  FROM project \n" +
            "         INNER JOIN phase  \n" +
            "\t\t    ON project.id = phase.project_id \n" +
            "\t\t LEFT JOIN phase_timesheet \n" +
            "\t\t    ON phase.id = phase_timesheet.phase_id \n" +
            "\t\t LEFT JOIN jour_timesheet \n" +
            "\t\t   ON jour_timesheet.id = phase_timesheet.jourtimesheet_id  \n" +
            "\t\t LEFT JOIN timesheet \n" +
            "\t\t   ON timesheet.id = jour_timesheet.timesheet_id  \n" +
            "\t\t     WHERE  \n" +
            "\t\t\t   CAST(project.client_id AS VARCHAR) = ?1  \n" +
            "\t\t\t     AND  \n" +
            "\t\t\t   ( timesheet.status IS NULL or timesheet.status = 'APPROUVED')\n" +
            "\t    GROUP BY project.nom , project.status , project.duree ;" , nativeQuery = true)
    public List<Object>  clientprojectview(String clientid);



}
