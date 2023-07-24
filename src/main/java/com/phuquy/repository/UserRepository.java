package com.phuquy.repository;

import com.phuquy.entity.TeamMember;
import com.phuquy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsernameAndPassword(String username, String password);

    User findUserByUsername(String username);
    User findUserByEmail(String email);

    //Find use start with email and not in this form. ( use in invite member to form)
    @Query("SELECT u FROM User u " +
            "WHERE u.email LIKE CONCAT(:email, '%') " +
            "AND u.user_id NOT IN (SELECT fp.user.user_id FROM FormParticipant fp WHERE fp.form.formID = :formID)")
    List<User> findUserStartWithEmail(@Param("email") String email, @Param("formID") int formID);

    @Query("SELECT u from User u,TeamMember tm where u.user_id=tm.user.user_id and tm.team.teamID = :teamID")
    List<User> findUserInTeam(@Param("teamID")int teamID);

}
