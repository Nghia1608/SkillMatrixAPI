package com.phuquy.repository;

import com.phuquy.entity.Project;
import com.phuquy.entity.Team;
import com.phuquy.entity.TeamProject;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phuquy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamProjectRepository extends JpaRepository<TeamProject, Long> {
    TeamProject findTeamProjectByTeamAndProject(Team team, Project project);

    @Query("SELECT t FROM  Team t,TeamProject tp,Project p where t.teamID =tp.team.teamID and tp.project.projectID = p.projectID and p.projectID = :projectID")
    List<Team> findByProject_ProjectID(@Param("projectID")int projectID);

    @Query("SELECT t FROM Team t " +
            "WHERE t.teamName LIKE CONCAT(:teamName, '%') " +
            "AND t.teamID NOT IN (SELECT tp.team.teamID FROM TeamProject tp WHERE tp.project.projectID = :projectID)")
    List<Team> findTeamNotInProject(@Param("teamName") String teamName, @Param("projectID") int projectID);
}

