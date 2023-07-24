package com.phuquy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_rate")
public class UserRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userRateID;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "form_id")
    private Form form;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User userID;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "skill_id")
    private Skill skillID;
    @Column(name = "self_rate")
    private int selfRate;
    @Column(name = "manager_rate")
    private int managerRate;

}