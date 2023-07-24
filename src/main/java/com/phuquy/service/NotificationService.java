package com.phuquy.service;

import com.phuquy.entity.Notification;
import com.phuquy.entity.User;
import com.phuquy.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repo;
    public List<Notification> getNotificationByUserID(int userID){
        return repo.getNotificationByUserID(userID);
    }

    public void announce(String content, User user){
        Notification noti = new Notification();
        noti.setIssue(content);
        noti.setUserID(user);
        noti.setStatus(false);
        noti.setCreateDate(new Date(System.currentTimeMillis()).toString());
        repo.save(noti);
    }
    public void delete(Notification noti){
        repo.deleteById(noti.getNotiID());
    }
    public void readed(Notification noti){
        noti.setStatus(true);
        repo.save(noti);
    }

}

