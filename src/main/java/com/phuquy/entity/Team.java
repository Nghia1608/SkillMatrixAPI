package com.phuquy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private int teamID;
    @Column(name = "team_name")
    private String teamName;
    private boolean status;
    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<TeamMember> teamMembers;
    @OneToMany(mappedBy = "team")
    @JsonIgnore
    private List<TeamProject> teamProjects;

}