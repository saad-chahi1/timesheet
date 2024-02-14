package com.example.timesheetapp.repositories;

import com.example.timesheetapp.entities.Notification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepo extends CrudRepository<Notification,UUID> {
    //get unreadordrednotificationsbyuser

    @Query(value = "SELECT *  FROM notification   \n" +
            "  WHERE  \n" +
            "    CAST(utilisateur_id as VARCHAR ) = ?1 AND vu = false  \n" +
            " ORDER BY sent_at DESC ; ",nativeQuery = true)
    public List<Notification> getNoitificationsByUser(String UUID);

    @Transactional
    @Modifying
    @Query(value = "update notification  \n" +
            "   set vu = true \n" +
            "     WHERE CAST(utilisateur_id as varchar) = ?1 ;",nativeQuery = true)
    public void readnotificationbyuser(String id);

}
