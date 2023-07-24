package com.phuquy;

import com.phuquy.entity.Notification;
import com.phuquy.entity.User;
import com.phuquy.repository.NotificationRepository;
import com.phuquy.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TestNotification {
    @Autowired
    NotificationRepository repository;
    @Autowired
    UserRepository userRepo;
    @Test
    public void testAdd(){
        User user = userRepo.findById(1L).get();
        Notification notif = new Notification();
        notif.setUserID(user);
        notif.setIssue("This a notification");
        notif.setStatus(false);
        notif.setCreateDate(new Date(System.currentTimeMillis()).toString());
        repository.save(notif);
        Assertions.assertThat(repository.findById(1L)).isNotNull();
    }
}
