package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.Employe;
import com.example.timesheetapp.entities.Timesheet;
import com.example.timesheetapp.entities.TimesheetStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimesheetRepo extends CrudRepository<Timesheet, UUID> {
      public List<Timesheet> findAllByEmploye(Employe employe);

      //@Query(value = "SELECT * FROM timesheet t WHERE t.DATE_DEBUT = ?1 AND t.DATE_FIN = ?2 AND CAST(EMPLOYE_ID AS VARCHAR) = ?3", nativeQuery = true)
      public Optional<Timesheet> findByDateDebutAndDateFinAndEmploye(LocalDate  dateDebut , LocalDate dateFin , Employe employe);

    //  public List<Timesheet> findAllByEmployeAndDateDebutAndDateFinAndStatus(Employe employe , LocalDate dateDebut , LocalDate dateFin , TimesheetStatus status);

    //  public List<Timesheet> findAllByDateDebutAndDateFinAndStatus(LocalDate dateDebut , LocalDate dateFin , TimesheetStatus status);

     @Query(value = "SELECT * FROM timesheet  \n" +
             "   WHERE  \n" +
             "        CAST(timesheet.employe_id AS VARCHAR) = ?1 \n" +
             "\t\tAND \n" +
             "\t     timesheet.status=?2 \n" +
             "\t    AND(\n" +
             "      ( CAST(timesheet.date_debut AS VARCHAR)  >= ?3 AND CAST(timesheet.date_fin AS VARCHAR) <= ?4 ) \n" +
             "\t  OR \n" +
             "\t  ( CAST(timesheet.date_debut AS VARCHAR) >= ?3  \n" +
             "\t   AND CAST(timesheet.date_debut AS VARCHAR) <= ?4 AND CAST(timesheet.date_fin AS VARCHAR) >= ?4 )\n" +
             "\t  OR  \n" +
             "\t  ( CAST(timesheet.date_debut AS VARCHAR) <= ?3 AND CAST(timesheet.date_fin AS VARCHAR) >= ?3  AND CAST(timesheet.date_fin AS VARCHAR) <= ?3)\n" +
             "      ) \n" , nativeQuery = true)
      public List<Timesheet> fetchAllByEmployeAndDateDebutAndDateFinAndStatus(String employeid , String status ,String dateDebut , String dateFin );

    @Query(value = "SELECT * FROM timesheet  \n" +
            "   WHERE  \n" +
            "        CAST(timesheet.employe_id AS VARCHAR) = ?1 \n" +
            "\t\tAND \n" +
            "      ( CAST(timesheet.date_debut AS VARCHAR)  >= ?2 AND CAST(timesheet.date_fin AS VARCHAR) <= ?3 ) \n" +
            "\t  OR \n" +
            "\t  ( CAST(timesheet.date_debut AS VARCHAR) >= ?2  \n" +
            "\t   AND CAST(timesheet.date_debut AS VARCHAR) <= ?3 AND CAST(timesheet.date_fin AS VARCHAR) >= ?3 )\n" +
            "\t  OR  \n" +
            "\t  ( CAST(timesheet.date_debut AS VARCHAR) <= ?2 AND CAST(timesheet.date_fin AS VARCHAR) >= ?2  AND CAST(timesheet.date_fin AS VARCHAR) <= ?2)\n" +
            "\n" , nativeQuery = true)
    public List<Timesheet> fetchAllByEmployeAndDateDebutAndDateFin(String employeid , String dateDebut , String dateFin );


    @Query(value = "SELECT * FROM timesheet  \n" +
              "   WHERE   \n" +
              "\t     timesheet.status=?1 \n" +
              "\t    AND(\n" +
              "      ( CAST(timesheet.date_debut AS VARCHAR)  >= ?2 AND CAST(timesheet.date_fin AS VARCHAR) <= ?3 ) \n" +
              "\t  OR \n" +
              "\t  ( CAST(timesheet.date_debut AS VARCHAR) >= ?2  \n" +
              "\t   AND CAST(timesheet.date_debut AS VARCHAR) <= ?3 AND CAST(timesheet.date_fin AS VARCHAR) >= ?3 )\n" +
              "\t  OR  \n" +
              "\t  ( CAST(timesheet.date_debut AS VARCHAR) <= ?2 AND CAST(timesheet.date_fin AS VARCHAR) >= ?3  AND CAST(timesheet.date_fin AS VARCHAR) <= ?3)\n" +
              "      ) " , nativeQuery = true)
      public List<Timesheet> fetchAllByDateDebutAndDateFinAndStatus(String status,String dateDebut,String dateFin);

    @Query(value = "SELECT * FROM timesheet  \n" +
            "   WHERE   \n" +
            "      ( CAST(timesheet.date_debut AS VARCHAR)  >= ?1 AND CAST(timesheet.date_fin AS VARCHAR) <= ?2 ) \n" +
            "\t  OR \n" +
            "\t  ( CAST(timesheet.date_debut AS VARCHAR) >= ?1  \n" +
            "\t   AND CAST(timesheet.date_debut AS VARCHAR) <= ?2 AND CAST(timesheet.date_fin AS VARCHAR) >= ?2 )\n" +
            "\t  OR  \n" +
            "\t  ( CAST(timesheet.date_debut AS VARCHAR) <= ?1 AND CAST(timesheet.date_fin AS VARCHAR) >= ?2  AND CAST(timesheet.date_fin AS VARCHAR) <= ?2)\n" +
            "" , nativeQuery = true)
    public List<Timesheet> fetchAllByDateDebutAndDateFin(String dateDebut,String dateFin);

      // @Query(value = "SELECT * FROM timesheet t WHERE CAST(t.EMPLOYE_ID AS VARCHAR) = ?1 AND t.STATUS != ?2" , nativeQuery = true)
      public List<Timesheet> findAllByEmployeAndStatusNot(Employe employe,TimesheetStatus status);

     // @Query(value = "SELECT * FROM timesheet t WHERE CAST(t.EMPLOYE_ID AS VARCHAR) = ?1 AND t.STATUS = ?2",nativeQuery = true)
      public List<Timesheet> findAllByEmployeAndStatus(Employe employe,TimesheetStatus status);


      public List<Timesheet> findAllByStatus(TimesheetStatus status);

      @Query(value = "SELECT count(*) as total , 'APPROUVED' as status \n" +
              "     FROM timesheet \n" +
              "\t      WHERE timesheet.status = 'APPROUVED'  \n" +
              "\t\t    AND timesheet.submitted_at  \n" +
              "\t\t\t   BETWEEN ?2 AND ?3 \n" +
              "\t\t\t       AND CAST(timesheet.employe_id AS VARCHAR) = ?1\n" +
              "\t\t\t    \n" +
              "UNION ALL \n" +
              "\n" +
              "SELECT count(*) as total , 'REJECTED' as status  \n" +
              "      FROM  timesheet WHERE  \n" +
              "\t     timesheet.status  = 'REJECTED'  \n" +
              "\t\t    AND timesheet.submitted_at  \n" +
              "\t\t\t     BETWEEN ?2 AND ?3 \n" +
              "\t\t\t\t   AND CAST(timesheet.employe_id AS VARCHAR) = ?1\n" +
              "UNION ALL \n" +
              "\n" +
              "SELECT count(*) as total , 'SUBMITTED' as status \n" +
              "     FROM  timesheet\n" +
              "\t   WHERE timesheet.status = 'SUBMITTED' \n" +
              "\t       AND timesheet.submitted_at BETWEEN ?2 AND ?3 \n" +
              "\t\t           AND CAST(timesheet.employe_id AS VARCHAR) = ?1" , nativeQuery = true)
      public List<Object> getTimesheetsReportByEmployeAndDatefinAndDatefin(String employeid , String from , String to);

     @Query(value = "SELECT count(*) as total , 'APPROUVED' as status \n" +
             "     FROM timesheet \n" +
             "\t   WHERE timesheet.status = 'APPROUVED'  \t\t    \n" +
             "UNION ALL \n" +
             "SELECT count(*) as total , 'REJECTED' as status  \n" +
             "       FROM  timesheet    \n" +
             "\t         WHERE timesheet.status = 'REJECTED'\n" +
             "UNION ALL \n" +
             "SELECT count(*) as total , 'SUBMITTED' as status \n" +
             "     FROM  timesheet\n" +
             "\t    WHERE timesheet.status = 'SUBMITTED'  ; ",nativeQuery = true )
     public List<Object> getTimesheetsReport();


     @Query(value ="SELECT count(*) as total , 'APPROUVED' as status \n" +
             "     FROM timesheet \n" +
             "\t   WHERE timesheet.status = 'APPROUVED'  \t \n" +
             "\t      AND CAST(timesheet.employe_id AS VARCHAR) = ?1\n" +
             "UNION ALL \n" +
             "SELECT count(*) as total , 'REJECTED' as status  \n" +
             "       FROM  timesheet    \n" +
             "\t         WHERE timesheet.status = 'REJECTED' \n" +
             "\t\t AND CAST(timesheet.employe_id AS VARCHAR) = ?1\t \n" +
             "UNION ALL \n" +
             "SELECT count(*) as total , 'SUBMITTED' as status \n" +
             "     FROM  timesheet\n" +
             "\t    WHERE timesheet.status = 'SUBMITTED'   \n" +
             "\t\tAND CAST(timesheet.employe_id AS VARCHAR) = ?1" , nativeQuery = true)
    public List<Object> getTimesheetReportByEmploye(String id);


}
