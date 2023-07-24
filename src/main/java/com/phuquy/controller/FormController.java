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
    private final  JWTService jwtService;
    private final  NotificationService notificationService;
    private final CookieService cookieService;
    private final TeamService teamService;
    @PostMapping("/formCreate")
    public ResponseEntity<String> formCreate(@RequestBody Map<String,String> data, HttpServletRequest request) {
            if (formService.createForm(data,request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "Invalid input data";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
    }

    @PostMapping("/submitUserRate/{formID}")
    public ResponseEntity<String> submitUserRate(@PathVariable int formID, @RequestBody Map<String,String> data, HttpServletRequest request){
        try {
            formService.getFormById(formID);
            if (userRateService.submitRate(formID,data,request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "Invalid input data";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NoSuchElementException e) {
            String message = "No form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @GetMapping("/getSkillDomainAndProgressInForm/{formID}")
    public ResponseEntity<Object[]> getSkillDomainAndProgressInForm(@PathVariable int formID,HttpServletRequest request){
        String username = jwtService.getUsernameFromToken(cookieService.getCookie(request));
        User user = userService.findUserByUsername(username);
        if(!formParticipantService.checkParticipantInForm(formID, (int) user.getUser_id()) && !formService.checkOwnerInForm(formID, (int) user.getUser_id())){
            return ResponseEntity.ok(new ArrayList[]{new ArrayList<>()});
        }
        else {
            List<SkillDomain> listDomain = skillDomainService.getListDomainNameByFormID(formID);
            for (SkillDomain domain : listDomain) {
                if (formService.checkDomainHasRate((int) user.getUser_id(), formID, domain.getDomainID())) {
                    domain.setStatus("Finish");
                } else {
                    domain.setStatus("Unfinished");
                }
            }
            Object[] array1 = new List[]{listDomain};
            return ResponseEntity.ok(array1);
        }
    }

    @GetMapping("/form/{formID}/getSelfRate/{domainID}")
    public ResponseEntity<Object[]> getSelfRateByFormAndDomain(HttpServletRequest request,@PathVariable("formID") int formID, @PathVariable("domainID") int domainID) {
        String username = jwtService.getUsernameFromToken(cookieService.getCookie(request));
        //Get userID from username
        User user = userService.findUserByUsername(username);
        List<Skill> skillList = skillService.getListByDomainID(domainID);
        List<UserRate> listUserRate = new ArrayList<>();
        for (Skill skill : skillList) {
            UserRate userRate = userRateService.findByUserAndSkillAndForm(user,skill,formService.getFormById(formID));
            listUserRate.add(userRate);
        }
        Object[] array1 = new List[]{
                skillList,
                listUserRate
        };
        return ResponseEntity.ok(array1);
    }
    @GetMapping("/formDataByID/{formID}")
    public ResponseEntity<Form> formDataByID(@PathVariable("formID") int formID){
        Form form = formService.getFormById(formID);
        return ResponseEntity.ok(form);
    }

    @GetMapping("/getInvitedMemberInForm/{formID}")
    public ResponseEntity<Object> getInvitedMemberInForm(@PathVariable int formID,HttpServletRequest request){
        String username = jwtService.getUsernameFromToken(cookieService.getCookie(request));
        User user = userService.findUserByUsername(username);
        if(!formService.checkOwnerInForm(formID, (int) user.getUser_id())
                && !formService.checkManagerInForm(formID, (int) user.getUser_id())){
            return ResponseEntity.ok(new String[]{""});
        }else {
            Object array = formParticipantService.formParticipantList(formID);
            return ResponseEntity.ok(array);
        }
    }
    @GetMapping("/searchUserNotInForm")
    public ResponseEntity<List<User>> searchUserNotInForm(@RequestParam("emailUser") String emailUser,@RequestParam int formID) {
        // Logic to perform search based on the query
        List<User> searchResults = userService.findUserStartWithEmail(emailUser,formID);
        return ResponseEntity.ok(searchResults);
    }
    @GetMapping("/getFormOwner")
    public ResponseEntity<List<Form>> getFormOwner(HttpServletRequest request) {
        String username = jwtService.getUsernameFromToken(cookieService.getCookie(request));
        User user = userService.findUserByUsername(username);
        List<Form> searchResults = formService.getListFormOwner((int) user.getUser_id());
        return ResponseEntity.ok(searchResults);
    }
    @GetMapping("/getFormParticipant")
    public ResponseEntity<List<Form>> getFormParticipant(HttpServletRequest request) {
        String username = jwtService.getUsernameFromToken(cookieService.getCookie(request));
        User user = userService.findUserByUsername(username);
        List<Form> searchResults = formService.getFormHasParticipant((int) user.getUser_id());
        return ResponseEntity.ok(searchResults);
    }
    @GetMapping("/getFormManager")
    public ResponseEntity<List<Form>> getFormManager(HttpServletRequest request) {
        String username = jwtService.getUsernameFromToken(cookieService.getCookie(request));
        User user = userService.findUserByUsername(username);
        List<Form> searchResults = formService.getListFormManager((int) user.getUser_id());
        return ResponseEntity.ok(searchResults);
    }

    @PostMapping("/form/{formID}/addInvitedMemberToForm")
    public ResponseEntity<String> addInvitedMemberToForm(@RequestParam String listUserID, @PathVariable int formID, HttpServletRequest request){
        try {
            formService.getFormById(formID);
            if (formParticipantService.addInvitedMemberToForm(listUserID,formID,request)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "Invalid input data";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }

        } catch (NoSuchElementException e) {
            String message = "No Form with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    //Add a team (contains all member) to a form.
    @PostMapping("/form/{formID}/addTeamToForm/{teamID}")
    public ResponseEntity<String> addTeamToForm(@PathVariable int formID, @PathVariable int teamID) {

        try {
            formService.getFormById(formID);
            teamService.findById(teamID);
            if (formParticipantService.addMemberInTeamToForm(formID,teamID)) {
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
    public ResponseEntity<?> addProjectToForm(@PathVariable int formID,@PathVariable int projectID) {
        try {
            formService.getFormById(formID);
            projectService.findByProjectID(projectID);
            if(formParticipantService.addProjectInRoomToForm(formID,projectID)){
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
    public ResponseEntity<?> addRoomToForm(@PathVariable int formID,@PathVariable int roomID) {
        try {
            formService.getFormById(formID);
            roomService.findByID(roomID);
            if(formParticipantService.addRoomToForm(formID,roomID)){
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