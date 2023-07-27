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
@RequestMapping("/participant")
public class FormController {
    private final SkillDomainService skillDomainService;
    private final UserService userService;
    private final FormParticipantService formParticipantService;
    private final UserRateService userRateService;
    private final FormService formService;
    private final SkillService skillService;
    private final ProjectService projectService;
    private final RoomService roomService;
    private final JWTService jwtService;
    private final CookieService cookieService;
    private final TeamService teamService;
    @PostMapping("/formCreate")
    public ResponseEntity<String> formCreate(@RequestBody Map<String,String> data, HttpServletRequest request) throws Exception {
            if (formService.createForm(data,request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "Invalid input data";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
    }

    @PostMapping("/submitUserRate/{formID}")
    public ResponseEntity<String> submitUserRate(@PathVariable String formID, @RequestBody Map<String,String> data, HttpServletRequest request){
        if(!formService.CheckFormID(formID)){
            String message = "FormID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            formService.getFormById(Long.parseLong(formID));
            if (userRateService.submitRate(Integer.parseInt(formID),data,request)) {
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

    @GetMapping("/getSkillDomainAndProgressInForm/{formID}")
    public ResponseEntity<Object> getSkillDomainAndProgressInForm(@PathVariable String formID,HttpServletRequest request) throws Exception {
        if(!formService.CheckFormID(formID)){
            String message = "FormID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            int intFormID =  Integer.parseInt(formID);
            formService.getFormById(intFormID);

            String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
            User user = userService.findUserByUsername(username);
            if(!formParticipantService.checkParticipantInForm(intFormID, (int) user.getUser_id())){
                String message = "Can't access this form";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            else {
                List<SkillDomain> listDomain = skillDomainService.getListDomainNameByFormID(intFormID);
                if(listDomain==null||listDomain.isEmpty()){
                    String message = "This form not have data Domain";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
                }
                for (SkillDomain domain : listDomain) {
                    if (formService.checkDomainHasRate((int) user.getUser_id(), intFormID, domain.getDomainID())) {
                        domain.setStatus("Finish");
                    } else {
                        domain.setStatus("Unfinished");
                    }
                }
                Object[] array1 = new List[]{listDomain};
                return ResponseEntity.ok(array1);
            }
        }catch (NoSuchElementException e){
            String message = "No form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @GetMapping("/form/{formID}/getSelfRate/{domainID}")
    public ResponseEntity<Object> getSelfRateByFormAndDomain(HttpServletRequest request,@PathVariable("formID") String formID, @PathVariable("domainID") String domainID) throws Exception {
        if(!formService.CheckFormID(formID)||!skillDomainService.CheckDomainID(domainID)){
            String message = "FormID / DomainID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            int intFormID = Integer.parseInt(formID);
            int intDomainID = Integer.parseInt(domainID);
            formService.getFormById(intFormID);
            skillDomainService.findById(intDomainID);
            //Get userID from username
            String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
            User user = userService.findUserByUsername(username);

            List<Skill> skillList = skillService.getListByDomainID(intDomainID);
            if(skillList==null||skillList.isEmpty()){
                String message = "Domain has no skill";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            //If in a Domain in Skill , have 4 skill and user just submit 2 , 2 skill was not submitted will display NULL
            List<UserRate> listUserRate = new ArrayList<>();
            for (Skill skill : skillList) {
                UserRate userRate = userRateService.findByUserAndSkillAndForm(user,skill,formService.getFormById(intFormID));
                listUserRate.add(userRate);
            }
            return ResponseEntity.ok(listUserRate);
        } catch (NoSuchElementException e) {
            String message = "No Form/Domain with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @GetMapping("/formDataByID/{formID}")
    public ResponseEntity<Object> formDataByID(@PathVariable("formID") String formID){
        if(!formService.CheckFormID(formID)){
            String message = "FormID  only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Form form = formService.getFormById(Long.parseLong(formID));
            return ResponseEntity.ok(form);
        }catch (NoSuchElementException e){
            String message = "No Form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @GetMapping("/getInvitedMemberInForm/{formID}")
    public ResponseEntity<Object> getInvitedMemberInForm(@PathVariable String formID,HttpServletRequest request) throws Exception {
        if(!formService.CheckFormID(formID)){
            String message = "FormID  only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            int intFormID = Integer.parseInt(formID);
            formService.getFormById(intFormID);
            String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
            User user = userService.findUserByUsername(username);
            if(!formService.checkOwnerInForm(intFormID, (int) user.getUser_id())
                    && !formService.checkManagerInForm(intFormID, (int) user.getUser_id())){
                String message = "Can't access this form";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }else {
                List<User> userList = formParticipantService.formParticipantListByFormID(intFormID);
                if(userList == null||userList.isEmpty()){
                    String message = "No user in this form";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
                }
                return ResponseEntity.ok(userList);
            }
        }catch (NoSuchElementException e){
            String message = "No Form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @GetMapping("/searchUserNotInForm")
    public ResponseEntity<Object> searchUserNotInForm(@RequestParam("emailUser") String emailUser,@RequestParam int formID) {
        // Logic to perform search based on the query
        List<User> searchResults = userService.findUserStartWithEmail(emailUser,formID);
        if(searchResults==null||searchResults.isEmpty()){
            String message = "No have User";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(searchResults);
    }
    @GetMapping("/getFormOwner")
    public ResponseEntity<Object> getFormOwner(HttpServletRequest request) throws Exception {
        String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
        User user = userService.findUserByUsername(username);
        List<Form> searchResults = formService.getListFormOwner((int) user.getUser_id());
        if(searchResults==null||searchResults.isEmpty()){
            String message = "No have form";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(searchResults);
    }

    @GetMapping("/getFormParticipant")
    public ResponseEntity<Object> getFormParticipant(HttpServletRequest request) throws Exception {
            String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
            User user = userService.findUserByUsername(username);
            List<Form> searchResults = formService.getFormHasParticipant((int) user.getUser_id());
        if(searchResults==null||searchResults.isEmpty()){
            String message = "No have form";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
            return ResponseEntity.ok(searchResults);
    }
    @GetMapping("/getFormManager")
    public ResponseEntity<Object> getFormManager(HttpServletRequest request) throws Exception {
        String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
        User user = userService.findUserByUsername(username);
        List<Form> searchResults = formService.getListFormManager((int) user.getUser_id());
        if(searchResults==null||searchResults.isEmpty()){
            String message = "No have form";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(searchResults);
    }

    @PostMapping("/form/{formID}/addInvitedMemberToForm")
    public ResponseEntity<String> addInvitedMemberToForm(@RequestParam String listUserID, @PathVariable String formID, HttpServletRequest request){
        if(!formService.CheckFormID(formID)){
            String message = "FormID  only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            formService.getFormById(Long.parseLong(formID));
            if (formParticipantService.addInvitedMemberToForm(listUserID, Integer.parseInt(formID),request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "Invalid input data";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }

        } catch (NoSuchElementException e) {
            String message = "No Form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //Add a team (contains all member) to a form.
    @PostMapping("/form/{formID}/addTeamToForm/{teamID}")
    public ResponseEntity<String> addTeamToForm(@PathVariable String formID, @PathVariable String teamID) {
        if(!formService.CheckFormID(formID)||!teamService.CheckTeamID(teamID)){
            String message = "FormID / ProjectID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            int intFormID = Integer.parseInt(formID);
            int intTeamID = Integer.parseInt(teamID);
            formService.getFormById(intFormID);
            teamService.findById(intTeamID);
            if (formParticipantService.addMemberInTeamToForm(intFormID,intTeamID)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "Empty team  !!";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }

        } catch (NoSuchElementException e) {
            String message = "No Team/Form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    //Add a project (contains all team - member) to a form.
    @PostMapping("/form/{formID}/addProjectToForm/{projectID}")
    public ResponseEntity<?> addProjectToForm(@PathVariable String formID,@PathVariable String projectID) {
        if(!formService.CheckFormID(formID)||!projectService.CheckProjectID(projectID)){
            String message = "FormID / ProjectID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            int intFormID = Integer.parseInt(formID);
            int intProjectID = Integer.parseInt(projectID);
            formService.getFormById(intFormID);
            projectService.findByProjectID(intProjectID);
            if(formParticipantService.addProjectInRoomToForm(intFormID,intProjectID)){
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }
            else {
                String message = "Empty project !!";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NoSuchElementException e) {
            String message = "No Project/Form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    //Add a room (contains all project - team - member) to a form.
    @PostMapping("/form/{formID}/addRoomToForm/{roomID}")
    public ResponseEntity<?> addRoomToForm(@PathVariable String formID,@PathVariable String roomID) {
        if(!formService.CheckFormID(formID)||!roomService.CheckRoomID(roomID)){
            String message = "FormID / RoomID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            int intFormID = Integer.parseInt(formID);
            int intRoomID = Integer.parseInt(roomID);
            formService.getFormById(intFormID);
            roomService.findByID(intRoomID);
            if(formParticipantService.addRoomToForm(intFormID,intRoomID)){
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }else{
                String message = "Empty room !!";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NoSuchElementException e) {
            String message = "No Room/Form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
}