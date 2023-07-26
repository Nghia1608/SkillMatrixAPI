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

    private final TeamService teamService;

    private final FormParticipantService formParticipantService;
    private final CookieService cookieService;


    @GetMapping("/getSkillDomainBy/form/{formID}")
    public ResponseEntity<Object> getSkillDomainByForm(@PathVariable int formID,HttpServletRequest request) throws Exception {
        String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
        User user = userService.findUserByUsername(username);
        if(!formParticipantService.checkParticipantInForm(formID, (int) user.getUser_id())){
            return ResponseEntity.ok(new ArrayList<>());
        }
        else {
            List<SkillDomain> listSkillDomain = skillDomainService.getListDomainNameByFormID(formID);
            return ResponseEntity.ok(listSkillDomain);
        }
    }
    @GetMapping("/userAndProgressInForm/{formID}")
    public ResponseEntity<Object> userAndProgressInForm(@PathVariable int formID, HttpServletRequest request) throws Exception {
        String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
        User user = userService.findUserByUsername(username);
        if(!formParticipantService.checkParticipantInForm(formID, (int) user.getUser_id()) && !formService.checkOwnerInForm(formID, (int) user.getUser_id())){
            return ResponseEntity.ok(new ArrayList<>());
        }
        else {
            List<User> userList = formParticipantService.formParticipantListInManagerPage(formID);
            Map<Object, Object> mapUser = new HashMap<>();
            for (int i = 0; i < userList.size(); i++) {
                String progress = formParticipantService.calculateProgress(formID, (int) userList.get(i).getUser_id());
                mapUser.put("ID:"+userList.get(i).getUser_id()+"name:"+userList.get(i).getUsername(), progress);
            }
            return ResponseEntity.ok(mapUser);
        }
    }
    @GetMapping("/getAllUserRateInForm/{formID}")
    public ResponseEntity<Object> getAllUserRateInForm(@PathVariable int formID,HttpServletRequest request) throws Exception {
        Object dataUserRate = userRateService.getAllDataUserRateByForm(formID,request);
        return ResponseEntity.ok(dataUserRate);
    }
    @GetMapping("/getOneUserRateInForm/{formID}/ByDomain/{domainID}")
    public ResponseEntity<Object> getOneUserRateInFormByDomain(@PathVariable int formID,@RequestParam int userID,@PathVariable int domainID){
        Object dataUserRate = userRateService.getUserRateInFormByDomain(formID,userID,domainID);
        return ResponseEntity.ok(dataUserRate);
    }
    @GetMapping("/getListUserRateInFormBySkill")
    public ResponseEntity<Object> getListUserRateInFormBySkill(@RequestParam int formID,@RequestParam int skillID){
        Object dataUserRate = userRateService.getUserRateInFormBySkill(formID,skillID);
        return ResponseEntity.ok(dataUserRate);
    }

    //Add more domain to exist form with domains not in form.
    @PostMapping("/addDomainToForm/{formID}")
    public ResponseEntity<String> addDomainToForm(@RequestParam int formID,@RequestParam String listDomainID , HttpServletRequest request){

        try {
            formService.getFormById(formID);
            if (formService.updateForm(formID,listDomainID,request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }else {
                String message = "Invalid input data";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NoSuchElementException e) {
            String message = "No form/domain with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/submitManagerRate/{formID}")
    public ResponseEntity<String> submitManagerRate(@PathVariable int formID,@RequestBody Map<String,String> data,HttpServletRequest request){
        try {
            formService.getFormById(formID);
            if (userRateService.submitManagerRate(formID,data,request)) {
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