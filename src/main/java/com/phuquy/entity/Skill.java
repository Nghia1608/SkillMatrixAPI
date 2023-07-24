package com.phuquy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "Skill")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int skill_id;
    @Column(name = "skill_name")
    private String skillName;
    @ManyToOne
    @JoinColumn(name = "domain_id")
    private SkillDomain domain;
}
