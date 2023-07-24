package com.phuquy.repository;

import com.phuquy.entity.Team;
import com.phuquy.entity.TeamMember;
import com.phuquy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Integer> {
    @Query("SELECT u FROM User u " +
            "WHERE u.email LIKE CONCAT(:email, '%') " +
            "AND u.user_id NOT IN (SELECT fp.user.user_id FROM TeamMember fp WHERE fp.team.teamID = :teamID)")
    List<User> findUserStartWithEmailInTeam(@Param("email") String email, @Param("teamID") int teamID);

    @Query("SELECT DISTINCT t FROM Team t " +
            "JOIN t.teamMembers tm1 JOIN t.teamMembers tm2 " +
            "WHERE tm1.user.user_id = :userID1 AND tm2.user.user_id = :userID2")
    Team findTeamUserJoinBoth(@Param("userID1")int userID1, @Param("userID2")int userID2);
}
