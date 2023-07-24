package com.phuquy;

import com.phuquy.repository.UserRoleRepository;
import com.phuquy.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class TestUserRole {
    @Autowired
    private UserRoleRepository repository;
    @Test
    public void testRole(){
        String username = "quy";
        System.out.println(repository.findByUserUsername(username).getRole().getRoleName());
        Assertions.assertThat(repository.findByUserUsername(username).getRole().getRoleName()).isNotNull();
    }
}
