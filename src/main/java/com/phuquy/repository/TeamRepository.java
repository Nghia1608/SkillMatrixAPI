package com.phuquy.repository;


import com.phuquy.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.phuquy.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    @Query("SELECT t FROM Team t,TeamMember tm WHERE t.teamID = tm.team.teamID and tm.user.user_id = :userID")
    Team findTeamByUserID(@Param("userID") int userID);


    Team findByTeamName(String teamName);

}
