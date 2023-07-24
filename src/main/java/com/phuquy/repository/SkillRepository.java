package com.phuquy.repository;

import com.phuquy.entity.Skill;
import com.phuquy.entity.SkillDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {
    List<Skill> findAllByDomain_DomainID(int id);


    @Query("SELECT d FROM Skill d,UserRate s WHERE s.skillID.skill_id = d.skill_id and d.skill_id = :skillID ")
    Skill findSkillInUserRate(@Param("skillID") int skillID);

    Skill findBySkillName(String name);
}
