package com.example.timesheetapp.repositories;


import com.example.timesheetapp.entities.Client;
import com.example.timesheetapp.entities.Manager;
import com.example.timesheetapp.entities.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepo extends CrudRepository<Project, UUID> {
      public List<Project> findAllByManagerAndArchivedFalse(Manager manager);
      public List<Project> findAllByManagerAndArchivedTrue(Manager manager);
      public List<Client> findAllByClient(Client client);

      @Query(value = "SELECT count(*) as Total ,  'en_cours' as Status    \n" +
              "     FROM  project \n" +
              "      WHERE STATUS = 'EN_COURS'   \n" +
              " UNION ALL    \n" +
              "  SELECT count(*) as Total ,  'termine' as Status    \n" +
              "     FROM  project \n" +
              "      WHERE STATUS = 'TERMINE' \n" +
              " UNION ALL  \n" +
              "  SELECT count(*) as Total ,  'annule' as Status    \n" +
              "     FROM  project \n" +
              "      WHERE STATUS = 'ANNULE'   \n" +
              " UNION ALL \n" +
              "  SELECT  count(*) as Total , 'termine_en_retard' as Status \n" +
              "     FROM project \n" +
              "      WHERE STATUS = 'TERMINE_EN_RETARD'   \n" +
              "      \n" +
              "   GROUP BY Status ; " , nativeQuery = true)
      public List<Object> getprojectsreport();

      @Query(value = "SELECT count(*) as Total ,  'en_cours' as Status    \n" +
              "     FROM  project \n" +
              "      WHERE STATUS = 'EN_COURS' AND CAST(project.client_id AS VARCHAR) = ?1   \n" +
              " UNION ALL    \n" +
              "  SELECT count(*) as Total ,  'termine' as Status    \n" +
              "     FROM  project \n" +
              "      WHERE STATUS = 'TERMINE' AND CAST(project.client_id AS VARCHAR) = ?1 \n" +
              " UNION ALL  \n" +
              "  SELECT count(*) as Total ,  'annule' as Status    \n" +
              "     FROM  project \n" +
              "      WHERE STATUS = 'ANNULE' AND CAST(project.client_id AS VARCHAR) = ?1   \n" +
              " UNION ALL \n" +
              "  SELECT  count(*) as Total , 'termine_en_retard' as Status \n" +
              "     FROM project \n" +
              "      WHERE STATUS = 'TERMINE_EN_RETARD' AND CAST(project.client_id AS VARCHAR) = ?1   \n" +
              "      \n" +
              "   GROUP BY Status ; " , nativeQuery = true)
      public List<Object> getprojectsreportbyEmploye(String employeid);

      @Query(value = "SELECT  phase.phase_type as phase ,  \n" +
              "\t         CONCAT(utilisateur.prenom,' ',utilisateur.nom) as consultant  ,  \n" +
              "\t         SUBSTRING(CAST(SUM(phase_timesheet.duration) as VARCHAR),1,5) as temps_consommé ,\n" +
              "\t\t\t  string_agg(COALESCE(phase_timesheet.description,''), ',')  as descriptions \n" +
              "\t\t\tFROM phase \n" +
              "\t    INNER JOIN project \n" +
              "\t\t  ON project.id = phase.project_id \n" +
              "\t    INNER JOIN phase_timesheet  \n" +
              "\t\t  ON phase_timesheet.phase_id = phase.id\n" +
              "\t\tINNER JOIN jour_timesheet  \n" +
              "\t\t  ON jour_timesheet.id = phase_timesheet.jourtimesheet_id\n" +
              "\t\tINNER JOIN timesheet \n" +
              "\t\t  ON timesheet.id = jour_timesheet.timesheet_id\n" +
              "\t\tINNER JOIN utilisateur \n" +
              "\t\t  ON timesheet.employe_id = utilisateur.id  \n" +
              "\t\tINNER JOIN affectation\n" +
              "\t\t ON affectation.phase_id = phase.id AND affectation.employe_id = timesheet.employe_id \n" +
              "\t\t  WHERE timesheet.status = 'APPROUVED' AND CAST(project.id AS VARCHAR) = ?1 \n" +
              "           \n" +
              "\t\tGROUP  BY phase , consultant  ; ",nativeQuery = true)
      public List<Object> getProjectChargeCosummeReport(String projectid);


      @Query(value = "SELECT CAST(phase.id AS VARCHAR) , phase.phase_type from phase  \n" +
              "\t\t\t\t  WHERE  CAST(phase.project_id AS VARCHAR) = ?1   \n" +
              "\t\t\t\t\t   AND \n" +
              "\t\t\t\t\t      phase.id  NOT IN ( SELECT phase.id from affectation \n" +
              "\t\t\t\t\t\t\t\t\t      INNER JOIN phase  \n" +
              "\t\t\t\t\t\t\t\t\t         ON phase.id = affectation.phase_id \n" +
              "\t\t\t\t\t\t\t\t\t            WHERE CAST(affectation.employe_id AS VARCHAR) = ?2 \n" +
              "\t\t\t\t\t\t\t\t\t            AND CAST(phase.project_id AS VARCHAR) = ?1 \n" +
              "\t\t\t\t\t\t\t\t\t  );  ", nativeQuery = true)
      public List<Object> getNotAffectedPhasesByProjectAndEmploye(String projectid , String employeid);
}
