package com.phuquy;

import com.phuquy.entity.SkillDomain;
import com.phuquy.repository.SkillDomainRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TestSkillDomain {
    @Autowired
    SkillDomainRepository repository;
    @Test
    public void testAdd(){
        SkillDomain sDomain = new SkillDomain();
        sDomain.setDomainName("Backend");
        repository.save(sDomain);
        Assertions.assertThat(repository.findById(1)).isNotNull();
    }
    @Test
    public void testEdit(){
        Optional<SkillDomain> opDomain = repository.findById(12);
        SkillDomain sDomain = opDomain.get();
        sDomain.setDomainName("New Domain");
        repository.save(sDomain);
        Assertions.assertThat(repository.findById(12).get().getDomainName()).isEqualTo("New Domain");
    }
    @Test
    public void testDelete(){
        repository.deleteById(12);
        Assertions.assertThat(repository.findById(12)).isEmpty();
    }
}
