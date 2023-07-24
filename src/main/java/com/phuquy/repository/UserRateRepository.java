package com.phuquy.repository;

import com.phuquy.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRateRepository extends JpaRepository<UserRate, Long> {
    UserRate findByUserIDAndSkillIDAndForm(User user, Skill skill, Form form);

    List<UserRate> findAllByForm(Form form);

    //Find result UserRate By DomainID and FormID
    @Query("SELECT f FROM UserRate f WHERE f.skillID.domain.domainID = :domainID AND f.form.formID = :formID")
    List<UserRate> findByDomainId(@Param("domainID") Long domainID, @Param("formID") int formID);

    @Query("SELECT f FROM UserRate f WHERE f.skillID.domain.domainID = :domainID AND f.form.formID = :formID and f.userID.user_id = :userID ")
    List<UserRate> findOneUserByDomainId(@Param("formID") int formID,@Param("userID") int userID,@Param("domainID") Long domainID);
    @Query("SELECT f FROM UserRate f WHERE f.skillID.skill_id = :skillID AND f.form.formID = :formID ")
    List<UserRate> findUserBySkillID(@Param("formID") int formID,@Param("skillID") int skillID);

    @Query("SELECT DISTINCT u FROM User u,UserRate ur where u.user_id=ur.userID.user_id and ur.userRateID = :userRateID")
    User findUserByUserRate(@Param("userRateID") long userRateID);
}
