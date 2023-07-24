package com.phuquy.service;


import com.phuquy.entity.User;
import com.phuquy.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    public User findUserByUsername(String username){
        return userRepository.findUserByUsername(username);
    }
    public boolean CheckUsernameExist(String username){
        if(userRepository.findUserByUsername(username)==null){
            return false;
        }else{
            return true;
        }
    }
    public boolean CheckEmailExist(String email){
        if(userRepository.findUserByEmail(email)==null){
            return false;
        }else{
            return true;
        }
    }
    public List<User> findUserStartWithEmail(String email,int formID){
        return userRepository.findUserStartWithEmail(email,formID);
    }
    public void save(User user){
         userRepository.save(user);
    }
    public User findUserByUserID(int userID){
        return userRepository.findById(Long.valueOf(userID)).get();
    }
    public List<User> findUserInTeam(int teamID){
        return userRepository.findUserInTeam(teamID);
    }


    public User findById(long id) { return userRepository.findById(id).get(); }
    public List<User> findAll(){ return userRepository.findAll(); }
}
