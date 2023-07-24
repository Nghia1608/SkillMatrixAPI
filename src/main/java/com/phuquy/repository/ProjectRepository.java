package com.phuquy.repository;

import com.phuquy.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByRoom_RoomID(int id);

    Project findByProjectID(int id);
    Project findByProjectName(String projectName);

    @Query("SELECT sd FROM Project sd where sd.room.roomID IS NULL and sd.projectName LIKE CONCAT(:projectName, '%')")
    List<Project> findProjectHasNotRoom(@Param("projectName") String projectName);
    @Query("SELECT p FROM Project p,TeamProject tp where tp.project.projectID = p.projectID and tp.team.teamID = :teamID ")
    List<Project> findProjectByTeam(@Param("teamID") int teamID);
}
