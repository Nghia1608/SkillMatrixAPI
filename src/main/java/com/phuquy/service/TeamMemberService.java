package com.phuquy.service;

import com.phuquy.entity.SkillDomain;
import com.phuquy.entity.Team;
import com.phuquy.entity.TeamMember;
import com.phuquy.entity.User;
import com.phuquy.repository.TeamMemberRepository;
import com.phuquy.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final  TeamMemberRepository teamMemberRepository;
    public void save(TeamMember teamMember){
        teamMemberRepository.save(teamMember);
    }
    public List<User> findUserStartWithEmail(String email,int formID){
        return teamMemberRepository.findUserStartWithEmailInTeam(email,formID);
    }

}
