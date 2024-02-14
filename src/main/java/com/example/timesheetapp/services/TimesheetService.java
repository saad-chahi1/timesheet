package com.example.timesheetapp.services;


import com.example.timesheetapp.entities.Timesheet;
import com.example.timesheetapp.repositories.TimesheetRepo;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SerializationUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;


@Service
public class TimesheetService {


    @Autowired
    private TimesheetRepo timesheetRepo ;



    public List<LocalDate> getDatesBetweenTwoDates(Date startDate, Date endDate) {

        List<LocalDate> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            LocalDate date2 = result.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            datesInRange.add(date2);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
       }

        public List<LocalDate> getDates( LocalDate startdate , LocalDate endDate ) {
           Long n = ChronoUnit.DAYS.between(startdate, endDate);
           List<LocalDate> localDates = new ArrayList<>();
           localDates.add(startdate);
           int i = 0;
           while (i < n) {
              LocalDate date = startdate.plusDays(1);
              startdate = date ;
              localDates.add(date);
              i++;
          }
          return localDates;
         }


    public Timesheet getEmployeTimesheet(LocalDate datedebut , LocalDate dateFin){

         return null ;
    }



}
