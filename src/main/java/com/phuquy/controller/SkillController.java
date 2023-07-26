package com.phuquy.controller;

import com.phuquy.entity.Skill;
import com.phuquy.service.SkillDomainService;
import com.phuquy.service.SkillService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skill")
public class SkillController {
    private final SkillService skillService;
    private final SkillDomainService skillDomainService;


    @GetMapping("/getSkillByDomain/{domainID}")
    public ResponseEntity<List<Skill>> getSkillByDomain(@PathVariable int domainID) {
        List<Skill> skill = skillService.getListByDomainID(domainID);
        return ResponseEntity.ok(skill);
    }
    @GetMapping("/getAllSkillWithPaginate/")
    public ResponseEntity<Page<Skill>> getAllSkillWithPaginate(@RequestParam("page") int page, @RequestParam("perPage") int perPage) {
        Page<Skill> skill = skillService.getWithPaginate(page, perPage);
        return ResponseEntity.ok(skill);
    }
    @GetMapping("/getAmountOfSkill/{amount}")
    public ResponseEntity<Object[]> getAmountOfSkill(@PathVariable int amount) {
        List<Skill> skill = skillService.getAllSkill();
        int amountOfSkill = skill.size();
        int maxPage;
        if(amountOfSkill<=amount){
            maxPage = 1;
        }else {
            if(amountOfSkill % amount >  0){
                maxPage = amountOfSkill / amount + 1;
            }else{
                maxPage = amountOfSkill / amount ;
            }
        }
        Object[] amountOfItem = new Object[2];
        amountOfItem[0] = amountOfSkill;
        amountOfItem[1] = maxPage;
        return ResponseEntity.ok(amountOfItem);
    }
    @PostMapping("/addNewSkill")
    public ResponseEntity<String> addNewSkill(@RequestParam int domainID,@RequestParam String skillName){
        if(!skillName.matches("[a-zA-Z ]+") || skillName.length()>50){
            String message = "Only allow a-z and Space, Length must < 50";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Skill skill = new Skill();
            skill.setSkillName(skillName);
            skill.setDomain(skillDomainService.findById(domainID));
            skillService.save(skill);
            String message = "Success";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }catch (NoSuchElementException e) {
            String message = "No domain with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @DeleteMapping("/deleteSkill/{skillID}")
    public ResponseEntity<String> delete(@PathVariable("skillID") int skillID,HttpServletRequest request) {
        try {
            skillService.findById(skillID);
            if (skillService.deleteSkill(skillID, request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "You dont allow to access this Skill";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NoSuchElementException e) {
            String message = "No Skill with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}