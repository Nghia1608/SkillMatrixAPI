package com.phuquy.repository;

import com.phuquy.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT r FROM Role r,UserRole ur,User u WHERE r.roleID = ur.role.roleID AND ur.user.user_id = u.user_id AND u.username = :username")
    Role getUserRole (@Param("username") String username);
}
