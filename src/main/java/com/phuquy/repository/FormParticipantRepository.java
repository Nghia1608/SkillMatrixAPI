package com.phuquy.repository;

import com.phuquy.entity.FormParticipant;

import com.phuquy.entity.SkillDomain;
import com.phuquy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Objects;

@Repository
public interface FormParticipantRepository extends JpaRepository<FormParticipant, Long> {
    List<FormParticipant> findAllByForm_FormID(int formID);

    @Query("SELECT fp " +
            "FROM FormParticipant fp " +
            "where fp.form.formID = :formID and fp.user.user_id = :userID")
    FormParticipant findByFormAndUser(@Param("formID") int formID,@Param("userID")int userID);

    @Query("SELECT fp FROM FormParticipant fp WHERE fp.user.user_id = :userID and fp.form.formID = :formID")
    FormParticipant findFormHasParticipant(@Param("formID") int formID, @Param("userID") int userID);
    @Query("SELECT u " +
            "FROM User u,FormParticipant fp,TeamMember tm,Team t " +
            "where u.user_id=fp.user.user_id and u.user_id = tm.user.user_id and tm.team.teamID = t.teamID " +
            "and fp.form.formID = :formID")
    List<User> findUserHasParticipant(@Param("formID") int formID);
    @Query("SELECT u " +
            "FROM User u,FormParticipant fp,TeamMember tm,Team t " +
            "where u.user_id=fp.user.user_id and u.user_id = tm.user.user_id and tm.team.teamID = t.teamID " +
            "and fp.form.formID = :formID")
    List<User> findUserHasParticipantInManagerPage(@Param("formID") int formID);
    @Query("SELECT d FROM SkillDomain d " +
            "WHERE d.status='Public' and  d.domainID " +
            "NOT IN (SELECT fd.domain.domainID FROM FormDomain fd " +
            "WHERE fd.form.formID IN :formID)")
    List<SkillDomain> findDomainsPublicNotInForms(@Param("formID") int formID);
}


