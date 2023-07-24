package com.phuquy.service;

import com.phuquy.entity.Role;
import com.phuquy.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository repository;

    public Role findById(int roleID){
        return repository.findById(roleID).get();
    }

}
