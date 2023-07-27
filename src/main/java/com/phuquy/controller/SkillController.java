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
    public ResponseEntity<Object> getSkillByDomain(@PathVariable String domainID) {
        if(!skillService.CheckSkillID(domainID)){
            String message = "DomainID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            List<Skill> skill = skillService.getListByDomainID(Integer.parseInt(domainID));
            if(skill==null||skill.isEmpty()){
                String message = "No skill in domain";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            return ResponseEntity.ok(skill);
        }catch (Exception e){
            String message = "No domain with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }

    }
    @GetMapping("/getAllSkillWithPaginate/")
    public ResponseEntity<Object> getAllSkillWithPaginate(@RequestParam("page") String page, @RequestParam("perPage") String perPage) {
        if(!skillService.CheckSkillID(page) || !skillService.CheckSkillID(perPage)){
            String message = "Data input only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        int pageInt = Integer.parseInt(page);
        int perPageInt = Integer.parseInt(perPage);
        Page<Skill> skill = skillService.getWithPaginate(pageInt, perPageInt);
        if(skill==null||skill.isEmpty()){
            String message = "No skill";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(skill);
    }
    @GetMapping("/getAmountOfSkill/{amount}")
    public ResponseEntity<Object> getAmountOfSkill(@PathVariable String amount) {
        if(!skillService.CheckSkillID(amount)){
            String message = "Amount only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        List<Skill> skill = skillService.getAllSkill();
        if(skill==null||skill.isEmpty()){
            String message = "No have any  skill";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        int amountOfSkill = skill.size();
        int maxPage;
        int number =Integer.parseInt(amount);
        if(amountOfSkill<= number){
            maxPage = 1;
        }else {
            if(amountOfSkill % number >  0){
                maxPage = amountOfSkill / number + 1;
            }else{
                maxPage = amountOfSkill / number ;
            }
        }
        Object[] amountOfItem = new Object[2];
        amountOfItem[0] = amountOfSkill;
        amountOfItem[1] = maxPage;
        return ResponseEntity.ok(amountOfItem);
    }
    @PostMapping("/addNewSkill")
    public ResponseEntity<String> addNewSkill(@RequestParam String domainID,@RequestParam String skillName){
        if(!skillName.matches("[a-zA-Z ]+") || skillName.length()>50){
            String message = "Skill name only allow a-z and Space, Length must < 50";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        if(!skillService.CheckSkillID(domainID)){
            String message = "DomainID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Skill skill = new Skill();
            skill.setSkillName(skillName);
            skill.setDomain(skillDomainService.findById(Integer.parseInt(domainID)));
            skillService.save(skill);
            String message = "Success";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }catch (NoSuchElementException e) {
            String message = "No domain with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @DeleteMapping("/deleteSkill/{skillID}")
    public ResponseEntity<String> delete(@PathVariable("skillID") String skillID,HttpServletRequest request) {
        if(!skillService.CheckSkillID(skillID)){
            String message = "SkillID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            skillService.findById(Integer.parseInt(skillID));
            if (skillService.deleteSkill(Integer.parseInt(skillID), request)) {
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