package com.phuquy;

import com.phuquy.entity.Skill;
import com.phuquy.repository.SkillDomainRepository;
import com.phuquy.repository.SkillRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TestSkill {
    @Autowired
    SkillRepository repository;
    @Autowired
    SkillDomainRepository domainRepository;
    @Test
    public void testAdd(){
        Skill skill = new Skill();
        skill.setSkillName("Spring");
        skill.setDomain(domainRepository.findById(3).get());
        repository.save(skill);
        Assertions.assertThat(repository.findById(101));
    }
    @Test
    public void testEdit(){
        Skill skill = repository.findById(101).get();
        skill.setSkillName("Spring boot");
        repository.save(skill);
        Assertions.assertThat(repository.findById(101).get().getSkillName())
                .isEqualTo("Spring boot");
    }
    @Test
    public void testDelete(){
        repository.deleteById(101);
        Assertions.assertThat(repository.findById(101)).isEmpty();
    }
    @Test
    public void findByName(){
        System.out.println(repository.findBySkillName("Independent Working"));
        Assertions.assertThat(repository.findBySkillName("Independent Working")).isNotNull();
    }
}
