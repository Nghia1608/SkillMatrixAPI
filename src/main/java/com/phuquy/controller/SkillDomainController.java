package com.phuquy.controller;

import com.phuquy.entity.SkillDomain;
import com.phuquy.service.SkillDomainService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
            String message = "Domain name only allow a-z and Space, Length must < 100";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @DeleteMapping("/deleteSkillDomain/{domainID}")
    public ResponseEntity<?> deleteSkillDomain(@PathVariable("domainID") String domainID, HttpServletRequest request){
        if(!skillDomainService.CheckDomainID(domainID)){
            String message = "DomainID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            skillDomainService.findById(Integer.parseInt(domainID));
            if (skillDomainService.deleteDomain(Integer.parseInt(domainID),request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "You dont allow to access this domain";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (Exception e) {
            String message = "No domain with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @GetMapping("/listAllSkillDomain")
    public ResponseEntity<Object> getAllDomain() {
        List<SkillDomain> skillDomains = skillDomainService.getList();
        if(skillDomains==null||skillDomains.isEmpty()){
            String message = "No domain";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(skillDomains);
    }
    @GetMapping("/getSkillDomainInForm/{formID}")
    public ResponseEntity<Object> getSkillDomainInForm(@PathVariable String formID) {
        if(!skillDomainService.CheckDomainID(formID)){
            String message = "FormID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            List<SkillDomain> skillDomains = skillDomainService.getListDomainNameByFormID(Integer.parseInt(formID));
            if(skillDomains==null||skillDomains.isEmpty()){
                String message = "No domain in this form";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            return ResponseEntity.ok(skillDomains);
        }catch (Exception e){
            String message = "No form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }

    }
}