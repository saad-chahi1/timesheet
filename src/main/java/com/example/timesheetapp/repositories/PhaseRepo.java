package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.Phase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.UUID;

@Repository
public interface PhaseRepo extends CrudRepository<Phase, UUID> {

    @Query(value = "SELECT phase.phase_type , phase.duree , phase.date_debut , phase.date_fin ,  CAST(SUM(duration)AS VARCHAR) FROM phase \n" +
            "   INNER JOIN phase_timesheet \n" +
            "          ON phase_timesheet.phase_id = phase.id\n" +
            "   INNER JOIN jour_timesheet \n" +
            "          ON jour_timesheet.id = phase_timesheet.jourtimesheet_id \n" +
            "   INNER JOIN timesheet \n" +
            "          ON timesheet.id = jour_timesheet.timesheet_id      \n" +
            "   INNER JOIN project \n" +
            "          ON project.id = phase.project_id\t  \n" +
            "   WHERE timesheet.status = 'APPROUVED'\t AND  CAST(project.id AS VARCHAR) = ?1 \n" +
            "   GROUP BY phase.phase_type ,  phase.duree ,  phase.date_debut , phase.date_fin \n" +
            "   ORDER BY phase.date_debut ; \n" +
            ";\t",nativeQuery = true)
    public List<Object> getGanttView(String UUID);

}
