package com.phuquy;

import com.phuquy.entity.Form;
import com.phuquy.entity.UserRate;
import com.phuquy.repository.*;
import com.phuquy.repository.UserRateRepository;
import com.phuquy.repository.FormRepository;
import com.phuquy.repository.SkillRepository;
import com.phuquy.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TestUserRate {
    @Autowired
    UserRateRepository repository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SkillRepository skillRepository;
    @Autowired
    FormDomainRepository formDomainRepository;
    @Autowired
    FormRepository formRepository;
    @Test
    public void testAdd(){
        UserRate fullForm = new UserRate();
        Form form = formRepository.findById(1L).get();
        fullForm.setUserID(userRepository.findById(1L).get());
        fullForm.setSkillID(skillRepository.findById(1).get());
        fullForm.setForm(form);
        fullForm.setSelfRate(5);
        fullForm.setManagerRate(3);
        repository.save(fullForm);
        Assertions.assertThat(repository.findById(2L)).isNotNull();
    }
    @Test
    public void testDelete(){
        repository.deleteById(1L);
        Assertions.assertThat(repository.findById(1L)).isEmpty();
    }
}
