package com.phuquy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "user")
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;
    @Column(unique = true)
    private String username;
    private String password;
    private String trueName;
    private String gender;
    private String phoneNumber;
    private String birthday;
    private String email;
    private boolean status;
    @OneToMany(mappedBy = "userID")
    @JsonIgnore
    private List<Form> formID;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<UserRole> userRoles;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<FormParticipant> forms;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<TeamMember> teamMembers;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}