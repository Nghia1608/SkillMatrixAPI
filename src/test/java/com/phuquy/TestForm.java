package com.phuquy;

import com.phuquy.entity.Form;
import com.phuquy.entity.User;
import com.phuquy.repository.FormRepository;
import com.phuquy.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TestForm {
    @Autowired
    FormRepository repository;
    @Autowired
    UserRepository userRepo;
    @Test
    public void testAdd(){
        Form form = new Form();
        form.setFormName("Form skill");
        User user = userRepo.findUserByUsername("quy");
        form.setFormName("Form test");
        form.setUserID(user);
        Date date = new Date(System.currentTimeMillis());
        form.setCreateDate(date.toString());
        repository.save(form);
        Assertions.assertThat(repository.save(form) != null);
    }
    @Test
    public void testDelete(){
        repository.deleteById(2L);
        Optional<Form> opForm = repository.findById(2L);
        Form form = opForm.get();
        System.out.println(form.getFormName());
        Assertions.assertThat(repository.findById(2L)).isEmpty();
    }
}
