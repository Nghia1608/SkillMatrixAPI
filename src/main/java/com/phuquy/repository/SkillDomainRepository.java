package com.phuquy.repository;

import com.phuquy.entity.SkillDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillDomainRepository extends JpaRepository<SkillDomain, Integer> {
    SkillDomain findSkillDomainByDomainID(int domainID);
    SkillDomain findByDomainName(String domainname);

    //Select list domain status private (with teamID) not in form.
    @Query("SELECT d FROM SkillDomain d,Team  t " +
            "WHERE d.status='Private' and d.team.teamID = t.teamID and t.teamID = :teamID and d.domainID " +
            "NOT IN (SELECT fd.domain.domainID FROM FormDomain fd " +
            "WHERE fd.form.formID IN :formID)")
    List<SkillDomain> findDomainsPrivateNotInForms(@Param("formID") int formID,@Param("teamID") int teamID);

    SkillDomain findSkillDomainsByDomainName(String domainName);

    //Select list domain status Public not in form.
    @Query("SELECT d FROM SkillDomain d " +
            "WHERE d.status='Public' and  d.domainID " +
            "NOT IN (SELECT fd.domain.domainID FROM FormDomain fd " +
            "WHERE fd.form.formID IN :formID)")
    List<SkillDomain> findDomainsPublicNotInForms(@Param("formID") int formID);

    //Select list Domain in Form by formID
    @Query("SELECT sd FROM SkillDomain sd,FormDomain fd WHERE sd.domainID = fd.domain.domainID and fd.form.formID = :formID ")
    List<SkillDomain> findSkillDomainByFormID(@Param("formID") int formID);
    //Find domain user has rated and self rate
    @Query("SELECT sd FROM SkillDomain sd, Skill s, UserRate ur " +
            "WHERE sd.domainID = s.domain.domainID AND s.skill_id = ur.skillID.skill_id " +
            "AND ur.userID.user_id = :userID AND ur.form.formID = :formID")
    List<SkillDomain> findSkillDomainHasRateByUserIDAndFormID(@Param("userID") int userID, @Param("formID") int formID);

    //Find domain user has rated and self rate value is 0
    @Query("SELECT sd FROM SkillDomain sd " +
            "JOIN Skill s ON sd.domainID = s.domain.domainID " +
            "JOIN UserRate ur ON sd.domainID = s.domain.domainID AND s.skill_id = ur.skillID.skill_id " +
            "WHERE ur.userID.user_id = :userID AND ur.form.formID = :formID " +
            "GROUP BY sd ")
//            "HAVING MIN(ur.selfRate) > 0")
    List<SkillDomain> findListDomainHasRateAndSelfRate0(@Param("userID") int userID, @Param("formID") int formID);

    @Query("SELECT sd FROM SkillDomain sd " +
            "JOIN Skill s ON sd.domainID = s.domain.domainID " +
            "JOIN UserRate ur ON sd.domainID = s.domain.domainID AND s.skill_id = ur.skillID.skill_id " +
            "WHERE ur.userID.user_id = :userID AND ur.form.formID = :formID AND sd.domainID = :domainID " +
            "GROUP BY sd ")
//            "HAVING MIN(ur.selfRate) > 0")
    SkillDomain findDomainHasRateAndSelfRate0(@Param("userID") int userID, @Param("formID") int formID, @Param("domainID") int domainID);
    //Find domain to check it has in table Skill or no.
    @Query("SELECT d FROM SkillDomain d,Skill s WHERE s.domain.domainID = d.domainID and d.domainID = :domainID ")
    SkillDomain findDomainsInSkill(@Param("domainID") int domainID);

    //Find domain to check it has in table Form or no.
    @Query("SELECT d FROM SkillDomain d,FormDomain s WHERE s.domain.domainID = d.domainID and d.domainID = :domainID ")
    SkillDomain findDomainsInFormDomain(@Param("domainID") int domainID);

    //Select list Domain by teamId and status is not Disable
    @Query("SELECT d FROM SkillDomain d WHERE d.team.teamID= :teamID and d.status= 'Private'")
    List<SkillDomain> findDomainByTeamId(@Param("teamID") int teamID);

    //Select list public Domain
    @Query("SELECT d FROM SkillDomain d WHERE d.status= 'Public'")
    List<SkillDomain> findDomainPublic();
    //Find domain by teamID
    @Query("SELECT d FROM SkillDomain d,Team  t,TeamMember tm " +
            "WHERE d.team.teamID = t.teamID and t.teamID =tm.team.teamID " +
            "and tm.user.user_id = :userID and d.domainID = :domainID ")
    SkillDomain findDomainCanAccessByUserID(@Param("userID") int userID,@Param("domainID") int domainID);
}
