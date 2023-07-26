package com.phuquy.controller;

import com.phuquy.entity.SkillDomain;
import com.phuquy.entity.Team;
import com.phuquy.service.SkillDomainService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/skillDomain")
public class SkillDomainController {
    private final SkillDomainService skillDomainService;

    @PostMapping("/addDomain")
    public ResponseEntity<?> addDomain(@RequestParam String domainName,HttpServletRequest request) throws Exception {
        if(domainName.matches("[a-zA-Z ]+") && domainName.length()<100){
            skillDomainService.addDomain(domainName,request);
            String message = "Success";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }else{
            String message = "Only allow a-z and Space, Length must < 100";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @DeleteMapping("/deleteSkillDomain/{domainID}")
    public ResponseEntity<?> deleteSkillDomain(@PathVariable("domainID") int domainID, HttpServletRequest request){
        try {
            skillDomainService.findById(domainID);
            if (skillDomainService.deleteDomain(domainID,request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "You dont allow to access this domain";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (Exception e) {
            String message = "No team with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @GetMapping("/listAllSkillDomain")
    public ResponseEntity<List<SkillDomain>> getAllDomain() {
        List<SkillDomain> skillDomains = skillDomainService.getList();
        return ResponseEntity.ok(skillDomains);
    }
    @GetMapping("/getSkillDomainInForm/{formID}")
    public ResponseEntity<List<SkillDomain>> getSkillDomainInForm(@PathVariable int formID) {
        List<SkillDomain> skillDomains = skillDomainService.getListDomainNameByFormID(formID);
        return ResponseEntity.ok(skillDomains);
    }
}