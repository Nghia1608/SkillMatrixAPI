package com.phuquy;

import com.phuquy.entity.User;
import com.phuquy.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TestLogin {
    @Autowired
    UserRepository repo;

    @org.junit.jupiter.api.Test
    public void testAuthen(){
        User user = new User();
        Assertions.assertThat(repo.findUserByUsernameAndPassword("quy", "123")).isNotNull();
    }
}
