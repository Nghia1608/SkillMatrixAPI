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
            String message = "Only allow a-z and Space, Length must < 50";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @PostMapping("/addUserToTeam")
    public ResponseEntity<String> addUserToTeam(@RequestParam int teamID,@RequestParam String listUserID){
        try {
            teamService.findById(teamID);
                if (teamService.addMemberToTeam(teamID, listUserID)) {
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
    public ResponseEntity<Object> listMemberInTeam(@PathVariable int teamID){
        Object listUser = userService.findUserInTeam(teamID);
        return ResponseEntity.ok(listUser);
    }
    @GetMapping("/listAllTeam")
    public ResponseEntity<List<Team>> getListTeam() {
        List<Team> searchResults = teamService.listAll();
        return ResponseEntity.ok(searchResults);
    }
    @GetMapping("/searchMemberNotInThisTeam/{teamID}")
    public ResponseEntity<List<User>> searchMemberNotInThisTeam(@RequestParam("emailUser") String emailUser,@PathVariable int teamID) {
        List<User> searchResults = teamMemberService.findUserStartWithEmail(emailUser.trim(),teamID);
        return ResponseEntity.ok(searchResults);
    }

    @GetMapping("/getTeamByProject/{projectID}")
    public ResponseEntity<List<Team>> getTeamByProject(@PathVariable int projectID) {
        List<Team> team = teamService.findByProjectId(projectID);
        return ResponseEntity.ok(team);
    }
    @PostMapping("/enable/{teamID}")
    public ResponseEntity<?> enable(@PathVariable("teamID") int teamID){
        try {
            Team team = teamService.findById(teamID);
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
    public ResponseEntity<?> disable(@PathVariable("teamID") int teamID){
        try {
            Team team = teamService.findById(teamID);
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
