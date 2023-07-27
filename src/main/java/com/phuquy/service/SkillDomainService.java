package com.phuquy.service;

import com.phuquy.JWT.JWTService;
import com.phuquy.entity.SkillDomain;
import com.phuquy.entity.Team;
import com.phuquy.entity.User;
import com.phuquy.repository.SkillDomainRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillDomainService {
    //Constructor Injection
    private final SkillDomainRepository repository;
    private final CookieService cookieService;
    private final JWTService jwtService;
    private final UserService userService;
    private final TeamService teamService;
    //Method
    public List<SkillDomain> getList(){
        return repository.findAll();
    }

    public void save(SkillDomain domain){
        repository.save(domain);
    }
    public void delete(SkillDomain domain){
        repository.delete(domain);
    }
    public SkillDomain findById(int id){ return repository.findById(id).get(); }
    public SkillDomain findDomainName(int domainID){
        return repository.findSkillDomainByDomainID(domainID);
    }

    public SkillDomain findByDomainName(String domainName){
        return repository.findSkillDomainsByDomainName(domainName);
    }


    public List<SkillDomain> getListDomainNameByFormID(int formID){
        return repository.findSkillDomainByFormID(formID);
    }

    public List<SkillDomain> getListDomainNameNotInFormDetailByFormID(int formID){
        return repository.findDomainsPublicNotInForms(formID);
    }
    public List<SkillDomain> getListDomainPrivateNotInForm(int formID,int teamID){
        return repository.findDomainsPrivateNotInForms(formID,teamID);
    }
    public List<SkillDomain> getListDomainNameInFormDetailByFormID(int formID){
        return repository.findSkillDomainByFormID(formID);
    }
    public List<SkillDomain> getListDomainNameHasRateAndSelfRate0(int userID,int formID){
        return repository.findListDomainHasRateAndSelfRate0(userID,formID);
    }

    public SkillDomain getDomainNameHasRateAndSelfRate(int userID,int formID,int domainID){
        return repository.findDomainHasRateAndSelfRate0(userID,formID,domainID);
    }
    public Boolean checkDomainHasInSkill(int domainID){
        SkillDomain domain = repository.findDomainsInSkill(domainID);
        if(domain==null){
            return false;
        }
        return true;
    }
    public Boolean checkDomainHasInFormDomain(int domainID){
        SkillDomain domain = repository.findDomainsInFormDomain(domainID);
        if(domain==null){
            return false;
        }
        return true;
    }

    public Boolean checkUserCanAccessDomain(int userID,int domainID){
        SkillDomain domain = repository.findDomainCanAccessByUserID(userID,domainID);
        if(domain==null){
            return false;
        }
        return true;
    }

    public void addDomain(@RequestParam String domainName, HttpServletRequest request) throws Exception {
        String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
        User user = userService.findUserByUsername(username);
        Team team = teamService.findTeamByUserID((int) user.getUser_id());
        SkillDomain skillDomain = new SkillDomain();
        //Set teamId for Skill Domain
        skillDomain.setTeam(team);
        skillDomain.setDomainName(domainName);
        if(team.getTeamID()!=0){
            skillDomain.setStatus("Private");
        }
        else {
            skillDomain.setStatus("Public");
        }
        save(skillDomain);
    }
    public boolean deleteDomain(@PathVariable int domainID, HttpServletRequest request) throws Exception {
        String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
        User user = userService.findUserByUsername(username);
        SkillDomain skillDomain = findById(domainID);
        if(checkUserCanAccessDomain((int) user.getUser_id(),domainID)){
            //SET status Disable for Skill Domain if Domain has in Table Skill or table FormDomain / not in -> delete
            if(checkDomainHasInSkill(domainID) || checkDomainHasInFormDomain(domainID)){
                skillDomain.setStatus("Disable");
                save(skillDomain);
            }
            else {
                delete(skillDomain);
            }
            return true;
        }
        return false;
    }
    public boolean CheckDomainID(String domainID){
        if(!domainID.matches("\\d+") || domainID.length()>9){
            return false;
        }
        return true;
    }
}
