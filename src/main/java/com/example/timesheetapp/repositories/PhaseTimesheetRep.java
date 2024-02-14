package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.PhaseTimesheet;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface PhaseTimesheetRep extends CrudRepository<PhaseTimesheet, UUID> {

    @Query(value = "SELECT CAST(EXTRACT(MONTH FROM JOUR_TIMESHEET.DATE) AS VARCHAR)  , CAST(SUM(PHASE_TIMESHEET.DURATION ) AS VARCHAR) , 0 as isRejected  FROM PHASE_TIMESHEET  \n" +
            "                          INNER JOIN JOUR_TIMESHEET ON PHASE_TIMESHEET.JOURTIMESHEET_ID =  JOUR_TIMESHEET.id \n" +
            "                                        JOIN TIMESHEET ON TIMESHEET.ID = JOUR_TIMESHEET.TIMESHEET_ID  \n" +
            "                                                 WHERE CAST(EXTRACT(YEAR FROM DATE) AS VARCHAR) = ?1 \n" +
            "                                                        AND STATUS = 'APPROUVED' AND CAST(TIMESHEET.EMPLOYE_ID AS VARCHAR) = ?2" +
            "                                                                 GROUP BY EXTRACT(MONTH FROM DATE)   \n" +
            "UNION ALL \n" +
            "\n" +
            "SELECT CAST(EXTRACT(MONTH FROM JOUR_TIMESHEET.DATE) AS VARCHAR) ,  CAST(SUM(PHASE_TIMESHEET.DURATION) AS VARCHAR) , 1 as isRejected  FROM PHASE_TIMESHEET  \n" +
            "                          INNER JOIN JOUR_TIMESHEET ON PHASE_TIMESHEET.JOURTIMESHEET_ID =  JOUR_TIMESHEET.id \n" +
            "                                        JOIN TIMESHEET ON TIMESHEET.ID = JOUR_TIMESHEET.TIMESHEET_ID  \n" +
            "                                                 WHERE CAST(EXTRACT(YEAR FROM DATE) AS VARCHAR) = ?1 \n" +
            "                                                        AND STATUS = 'REJECTED' AND CAST(TIMESHEET.EMPLOYE_ID AS VARCHAR) = ?2 \n" +
            "                                                                 GROUP BY EXTRACT(MONTH FROM DATE)", nativeQuery = true)

    public List<Object> getApprouvedHoursReportByEmploye(String year,String employeid);


}
