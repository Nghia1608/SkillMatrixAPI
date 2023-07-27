package com.phuquy.service;

import com.phuquy.JWT.JWTService;
import com.phuquy.entity.Skill;
import com.phuquy.entity.SkillDomain;
import com.phuquy.entity.User;
import com.phuquy.repository.SkillRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    //Constructor injection
    private final  SkillRepository skillRepository;
    private final JWTService jwtService;
    private final CookieService cookieService;
    private final UserService userService;
    private final SkillDomainService skillDomainService;
    //Method
    public Page<Skill> getWithPaginate(int page, int perPage) {
        Pageable pageable = PageRequest.of(page - 1, perPage);
        return skillRepository.findAll(pageable);
    }
    public List<Skill> getAllSkill() {
        return skillRepository.findAll();
    }
    public Boolean checkSkillInUserRate(int skillID){
        Skill skill = skillRepository.findSkillInUserRate(skillID);
        if(skill==null){
            return false;
        }
        return true;
    }
    public void save(Skill skill){ skillRepository.save(skill); }

    public void delete(Skill skill){ skillRepository.delete(skill);
    }
    public boolean deleteSkill(int skillID, HttpServletRequest request) throws Exception {
        String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
        User user = userService.findUserByUsername(username);
        Skill skill = findById(skillID);
        SkillDomain skillDomain = skillDomainService.findById(skill.getDomain().getDomainID());
        if(skillDomainService.checkUserCanAccessDomain((int) user.getUser_id(),skillDomain.getDomainID())
            && checkSkillInUserRate(skillID)==false){
            delete(skill);
            return true;
        }
        return false;
    }
    public List<Skill> getListByDomainID(int id){
        return skillRepository.findAllByDomain_DomainID(id);
    }
    public Skill findById(int id){
        return skillRepository.findById(id).get();
    }
    public Skill findBySkillName(String name){ return skillRepository.findBySkillName(name); }
    public boolean CheckSkillID(String skillID){
        if(!skillID.matches("\\d+") || skillID.length()>9){
            return false;
        }
        return true;
    }
}
