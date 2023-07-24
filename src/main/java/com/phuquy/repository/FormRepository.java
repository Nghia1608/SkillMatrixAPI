package com.phuquy.repository;

import com.phuquy.entity.Form;

import com.phuquy.entity.FormParticipant;

import com.phuquy.entity.SkillDomain;
import com.phuquy.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {


    @Query("SELECT f FROM Form f,FormParticipant fp WHERE f.formID=fp.form.formID and  fp.user.user_id = :userID ")
    List<Form> findFormHasParticipant(@Param("userID") int userID);

    //Find result in FormParticipant has userID and formID


    @Query("SELECT fp FROM FormParticipant fp,User u, UserRole ur,Role  r " +
            "WHERE fp.user.user_id = :userID and fp.form.formID = :formID " +
            "and fp.user.user_id = u.user_id and u.user_id = ur.user.user_id and ur.role.roleID = r.roleID and r.roleName='Manager' ")
    FormParticipant findManagerParticipantInForm(@Param("formID") int formID, @Param("userID") int userID);
    //Find result in FormParticipant has userID and formID ( = owner of this form)
    @Query("SELECT f FROM Form f WHERE f.formID = :formID and f.userID.user_id = :userID ")
    Form findFormOwner(@Param("formID") int formID, @Param("userID") int userID);

    @Query("SELECT f FROM Form f WHERE f.userID.user_id = :userID ")
    List<Form> showListFormOwner(@Param("userID") int userID);
    @Query("SELECT f FROM Form f,FormParticipant fp,User u, UserRole ur,Role  r " +
            "WHERE f.formID =fp.form.formID and fp.user.user_id = u.user_id and u.user_id = ur.user.user_id " +
            "and ur.role.roleID = r.roleID and r.roleName ='Manager' and u.user_id = :userID")
    List<Form> showListFormManager(@Param("userID") int userID);

    @Query("SELECT fd from FormDomain fd,Form f where fd.form.formID = f.formID and fd.domain.domainID = :domainID")
    Form findByDomainID(int domainID);
}
