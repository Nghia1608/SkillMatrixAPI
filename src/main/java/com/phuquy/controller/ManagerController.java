package com.phuquy.controller;
import com.phuquy.JWT.JWTService;
import com.phuquy.entity.*;
import com.phuquy.service.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {
    private final SkillDomainService skillDomainService;

    private final UserService userService;

    private final UserRateService userRateService;

    private final FormService formService;

    private final SkillService skillService;
    private final JWTService jwtService;

    private final FormParticipantService formParticipantService;
    private final CookieService cookieService;


    @GetMapping("/getSkillDomainBy/form/{formID}")
    public ResponseEntity<Object> getSkillDomainByForm(@PathVariable String formID,HttpServletRequest request) throws Exception {
        if(!formService.CheckFormID(formID)){
            String message = "FormID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            formService.getFormById(Long.parseLong(formID));
            String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
            User user = userService.findUserByUsername(username);
            if(!formParticipantService.checkParticipantInForm(Integer.parseInt(formID), (int) user.getUser_id())){
                String message = "You can't access this form";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            else {
                List<SkillDomain> listSkillDomain = skillDomainService.getListDomainNameByFormID(Integer.parseInt(formID));
                if(listSkillDomain==null||listSkillDomain.isEmpty()){
                    String message = "No have SkillDomain with this formID";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
                }
                return ResponseEntity.ok(listSkillDomain);
            }
        }catch (NoSuchElementException ex){
            String message = "No have Form with this formID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }

    }
    @GetMapping("/userAndProgressInForm/{formID}")
    public ResponseEntity<Object> userAndProgressInForm(@PathVariable String formID, HttpServletRequest request) throws Exception {
        if(!formService.CheckFormID(formID)){
            String message = "FormID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
            User user = userService.findUserByUsername(username);
            formService.getFormById(Long.parseLong(formID));
            if(!formParticipantService.checkParticipantInForm(Integer.parseInt(formID), (int) user.getUser_id()) && !formService.checkOwnerInForm(Integer.parseInt(formID), (int) user.getUser_id())){
                String message = "Can't access this form";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            else {
                List<User> userList = formParticipantService.formParticipantListByFormID(Integer.parseInt(formID));
                System.out.println("test"+userList);
                if(userList==null||userList.isEmpty()){
                    String message = "This form not have user join in";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
                }
                Map<Object, Object> mapUser = new HashMap<>();
                for (User value : userList) {
                    String progress = formParticipantService.calculateProgress(Integer.parseInt(formID), (int) value.getUser_id());
                    if (progress.equals("Empty")) {
                        String message = "This form not have data rate";
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
                    }
                    mapUser.put("ID:" + value.getUser_id() + "name:" + value.getUsername(), progress);
                }
                return ResponseEntity.ok(mapUser);
            }
        }catch (NoSuchElementException e){
            String message = " No have exist value : FormID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @GetMapping("/getAllUserRateInForm/{formID}")
    public ResponseEntity<Object> getAllUserRateInForm(@PathVariable String formID,HttpServletRequest request) throws Exception {
        if(!formService.CheckFormID(formID)){
            String message = "FormID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            int intFormID = Integer.parseInt(formID);
            formService.getFormById(intFormID);
            Object dataUserRate = userRateService.getAllDataUserRateByForm(intFormID,request);
            if(dataUserRate==null){
                String message = " No data with this FormID";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            return ResponseEntity.ok(dataUserRate);
        }catch (NoSuchElementException e){
            String message = " No have exist value : FormID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @GetMapping("/getOneUserRateInForm/{formID}/ByDomain/{domainID}")
    public ResponseEntity<Object> getOneUserRateInFormByDomain(@PathVariable String formID,@RequestParam String userID,@PathVariable String domainID){
        if(!formService.CheckFormID(formID) || !formService.CheckFormID(userID) ||!formService.CheckFormID(domainID)){
            String message = "FormID /UserID/DomainID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            int intFormID = Integer.parseInt(formID);
            int intUserID = Integer.parseInt(userID);
            int intDomainID = Integer.parseInt(domainID);
            formService.getFormById(intFormID);
            userService.findById(intUserID);
            skillDomainService.findById(intDomainID);
            Object dataUserRate = userRateService.getUserRateInFormByDomain(intFormID,intUserID,intDomainID);
            if(dataUserRate==null){
                String message = " No data with this FormID/UserID/DomainID";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            return ResponseEntity.ok(dataUserRate);
        }catch (NoSuchElementException e){
            String message = " No have exist value : FormID/UserID/DomainID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }

    }
    @GetMapping("/getListUserRateInFormBySkill")
    public ResponseEntity<Object> getListUserRateInFormBySkill(@RequestParam String formID,@RequestParam String skillID){
        if(!formService.CheckFormID(formID)||!formService.CheckFormID(skillID)){
            String message = "FormID / SkillID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        int intFormID = Integer.parseInt(formID);
        int intSkillID = Integer.parseInt(skillID);
        try{
            formService.getFormById(intFormID);
            skillService.findById(intSkillID);
            Object dataUserRate = userRateService.getUserRateInFormBySkill(intFormID,intSkillID);
            if(dataUserRate==null){
                String message = "No Data ";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            return ResponseEntity.ok(dataUserRate);
        }
        catch (NoSuchElementException e){
            String message = "No Form/Skill with this ID ";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    //Add more domain to exist form with domains not in form.
    @PostMapping("/addDomainToForm/{formID}")
    public ResponseEntity<String> addDomainToForm(@PathVariable String formID,@RequestParam String listDomainID , HttpServletRequest request){
        if(!formService.CheckFormID(formID)){
            String message = "FormID / SkillID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            formService.getFormById(Long.parseLong(formID));
            if (formService.updateForm(Integer.parseInt(formID),listDomainID,request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }else {
                String message = "Invalid input data";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NoSuchElementException e) {
            String message = "No form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/submitManagerRate/{formID}")
    public ResponseEntity<String> submitManagerRate(@PathVariable String formID,@RequestBody Map<String,String> data,HttpServletRequest request){
        if(!formService.CheckFormID(formID)){
            String message = "FormID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            formService.getFormById(Long.parseLong(formID));
            if (userRateService.submitManagerRate(Integer.parseInt(formID),data,request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "Invalid input data";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NoSuchElementException e) {
            String message = "No form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}