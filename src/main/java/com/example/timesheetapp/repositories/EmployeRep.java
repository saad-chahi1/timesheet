package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.Employe;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeRep extends CrudRepository<Employe,UUID> {
    Optional<Employe> findById(UUID uuid);

    @Query(value = "SELECT utilisateur.email FROM timesheet \n" +
            "        JOIN utilisateur ON utilisateur.id = timesheet.employe_id   \n" +
            "          WHERE timesheet.status = 'SAVED' AND timesheet.date_debut = ?1 AND timesheet.date_fin = ?2        \n" +
            "    UNION \n" +
            "   \n" +
            "    SELECT utilisateur.email FROM utilisateur\n" +
            "        LEFT JOIN timesheet ON utilisateur.id = timesheet.employe_id    \n" +
            "          WHERE utilisateur.discriminator = 'EMPLOYE' AND timesheet.id IS NULL ;" ,nativeQuery = true)
    public List<Object> getEmployesThatnotsubmittedtheretimesheetByPeriod(LocalDate dateDebut , LocalDate dateFin);

   @Query(value="SELECT   \n" +
           "\t       phase_type as phase ,    \n" +
           "\t\t   date_affectation ,\n" +
           "\t       SUBSTRING(CAST(sum(duration) AS VARCHAR),1,5) as duration , \n" +
           "\t\t   priority \n" +
           "\t\t  FROM ( \n" +
           "\t\t   SELECT phase.phase_type  , affectation.date_affectation ,  phase_timesheet.duration , phase.priority FROM affectation  \n" +
           "\t\t       INNER JOIN  phase \n" +
           "\t\t\t       ON phase.id = affectation.phase_id \n" +
           "\t\t\t   INNER JOIN  utilisateur \n" +
           "\t\t\t       ON utilisateur.id = affectation.employe_id  \n" +
           "\t\t\t   INNER JOIN phase_timesheet    \n" +
           "\t\t\t       ON phase.id = phase_timesheet.phase_id\n" +
           "\t\t\t   INNER JOIN jour_timesheet \n" +
           "\t\t\t      ON jour_timesheet.id = phase_timesheet.jourtimesheet_id \n" +
           "\t\t\t   INNER JOIN timesheet \n" +
           "\t\t\t      ON timesheet.id = jour_timesheet.timesheet_id AND timesheet.employe_id = utilisateur.id   \n" +
           "\t\t\tWHERE  \n" +
           "\t\t\t  CAST ( utilisateur.id as VARCHAR ) = ?2 \n" +
           "\t\t\t   AND \n" +
           "\t\t\t  CAST(phase.project_id as VARCHAR ) = ?1 \n" +
           "\t\t\t   AND \n" +
           "\t\t\t  timesheet.status = 'APPROUVED'\n" +
           "\t\tUNION ALL  \n" +
           "\t\t SELECT phase.phase_type , affectation.date_affectation , '00:00:00' as duration , phase.priority FROM affectation   \n" +
           "\t\t   INNER JOIN phase \n" +
           "\t\t      ON phase.id = affectation.phase_id  \n" +
           "\t\t    WHERE  \n" +
           "\t\t\t   CAST ( affectation.employe_id as VARCHAR ) = ?2 \n" +
           "\t\t\t    AND \n" +
           "\t\t\t   CAST(phase.project_id as VARCHAR ) = ?1 \n" +
           "\t\t\t  ) AS t \n" +
           "\t\t GROUP BY phase_type , date_affectation , priority \n" +
           "         ORDER BY priority ;", nativeQuery = true)
    public List<Object> getWorkDurationByProjectAndByEmploye(String projectid , String employeid);

    @Query(value = "DELETE FROM project_employe WHERE CAST(employe_id AS VARCHAR) = ?1",nativeQuery = true)
    public void deleteemployefromproject(String empid);
}
