package com.phuquy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_id")
    private long notiID;

    private String link;
    @Column(length = 1000)
    private String issue;
    private boolean status;
    private String createDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userID;
}