package com.phuquy.repository;

import com.phuquy.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n from Notification  n where n.userID.user_id = :userID")
    List<Notification> getNotificationByUserID(@Param("userID") int userID);
}
