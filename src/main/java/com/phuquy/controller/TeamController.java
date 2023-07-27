package com.phuquy.controller;


import com.phuquy.entity.*;
import com.phuquy.service.TeamMemberService;
import com.phuquy.service.TeamService;
import com.phuquy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;
import com.phuquy.entity.Team;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {
    private final TeamService teamService;
    private final TeamMemberService teamMemberService;
    private final UserService userService;

    @PostMapping("/addNewTeam")
    public ResponseEntity<String> addNewTeam(@RequestParam String teamName){
        Team team = new Team();
        if(teamName.matches("[a-zA-Z ]+") && teamName.length()<50){
            team.setTeamName(teamName);
            team.setStatus(true);
            teamService.save(team);
            String message = "Success";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }else{
            String message = "Team name only allow a-z and Space, Length must < 50";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @PostMapping("/addUserToTeam")
    public ResponseEntity<String> addUserToTeam(@RequestParam String teamID,@RequestParam String listUserID){
        if(!teamService.CheckTeamID(teamID)){
            String message = "TeamID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            teamService.findById(Integer.parseInt(teamID));
                if (teamService.addMemberToTeam(Integer.parseInt(teamID), listUserID)) {
                    String message = "Success";
                    return ResponseEntity.status(HttpStatus.OK).body(message);
                } else {
                    String message = "Invalid input data";
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
                }
        } catch (NoSuchElementException e) {
            String message = "No team with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @GetMapping("/findTeamOfTwoUser")
    public ResponseEntity<Object> findTeamOfTwoUser(@RequestParam int userID1,@RequestParam int userID2){
        Object team = teamService.findTeamOfTwoUser(userID1,userID2);
        return ResponseEntity.ok(team);
    }
    @GetMapping("/listMemberInTeam/{teamID}")
    public ResponseEntity<Object> listMemberInTeam(@PathVariable String teamID){
        if(!teamService.CheckTeamID(teamID)){
            String message = "TeamID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            teamService.findById(Integer.parseInt(teamID));
            List<User> listUser = userService.findUserInTeam(Integer.parseInt(teamID));
            if(listUser==null||listUser.isEmpty()){
                String message = "No user in team";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            return ResponseEntity.ok(listUser);
        } catch (NoSuchElementException e) {
            String message = "No team with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @GetMapping("/listAllTeam")
    public ResponseEntity<Object> getListTeam() {
        List<Team> searchResults = teamService.listAll();
        if(searchResults==null||searchResults.isEmpty()){
            String message = "No team ";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(searchResults);
    }
    @GetMapping("/searchMemberNotInThisTeam/{teamID}")
    public ResponseEntity<Object> searchMemberNotInThisTeam(@RequestParam("emailUser") String emailUser,@PathVariable String teamID) {
        if(!teamService.CheckTeamID(teamID)){
            String message = "TeamID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            teamService.findById(Integer.parseInt(teamID));
            List<User> searchResults = teamMemberService.findUserStartWithEmail(emailUser.trim(), Integer.parseInt(teamID));
            if(searchResults==null||searchResults.isEmpty()){
                String message = "No member";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            return ResponseEntity.ok(searchResults);
        } catch (NoSuchElementException e) {
            String message = "No team with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @GetMapping("/getTeamByProject/{projectID}")
    public ResponseEntity<Object> getTeamByProject(@PathVariable String projectID) {
        if(!teamService.CheckTeamID(projectID)){
            String message = "ProjectID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            List<Team> team = teamService.findByProjectId(Integer.parseInt(projectID));
            if(team==null||team.isEmpty()){
                String message = "No team in this project";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            return ResponseEntity.ok(team);
        } catch (NoSuchElementException e) {
            String message = "No project with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @PostMapping("/enable/{teamID}")
    public ResponseEntity<?> enable(@PathVariable("teamID") String teamID){
        if(!teamService.CheckTeamID(teamID)){
            String message = "TeamID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            Team team = teamService.findById(Integer.parseInt(teamID));
                team.setStatus(true);
                teamService.save(team);
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);

        } catch (NoSuchElementException e) {
            String message = "No team with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @PostMapping("/disable/{teamID}")
    public ResponseEntity<?> disable(@PathVariable("teamID") String teamID){
        if(!teamService.CheckTeamID(teamID)){
            String message = "TeamID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            Team team = teamService.findById(Integer.parseInt(teamID));
            team.setStatus(false);
            teamService.save(team);
            String message = "Success";
            return ResponseEntity.status(HttpStatus.OK).body(message);

        } catch (NoSuchElementException e) {
            String message = "No team with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }


}
