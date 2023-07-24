package com.phuquy.service;

import com.phuquy.entity.Role;
import com.phuquy.entity.UserRole;
import com.phuquy.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository repository;

    public Role getRoleByUsername(String username){
        return repository.findByUserUsername(username).getRole();
    }
    public void save(UserRole userRole){
        repository.save(userRole);
    }

}
