package com.phuquy.controller;

import com.phuquy.entity.*;
import com.phuquy.service.ProjectService;
import com.phuquy.service.RoomService;
import com.phuquy.service.TeamProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final RoomService roomService;
    private final TeamProjectService teamProjectService;

    @PostMapping("/addNewProject")
    public ResponseEntity<?> addNewProject(@RequestParam String projectName,@RequestParam int roomID){
        if(!projectService.checkDataInput(projectName)){
            String message = "Only allow a-z and Space, Length must < 50";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Room room = roomService.findByID(roomID);
            if(room!=null){
                Project project = new Project();
                project.setProjectName(projectName);
                project.setRoom(room);
                project.setStatus(true);
                projectService.save(project);
            }
        }catch (NoSuchElementException e) {
            String message = "No Room with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        String message = "Success";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
    @PutMapping("/editProject/{projectID}")
    public ResponseEntity<String> editProject(@PathVariable("projectID") int projectID, @RequestParam String projectName, @RequestParam int roomID){
        if(!projectService.checkDataInput(projectName)){
            String message = "Only allow a-z and Space, Length must < 50";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Room room = roomService.findByID(roomID);
            Project project = projectService.findByProjectID(projectID);
            if(room!=null && project!=null){
                project.setProjectName(projectName);
                project.setRoom(room);
                projectService.save(project);
            }
        }catch (NoSuchElementException e) {
            String message = "No Room/Project with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        String message = "Success";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
    @PostMapping("/disable/{projectID}")
    public ResponseEntity<String> disable(@PathVariable("projectID") int projectID){
        try{
            Project project = projectService.findByProjectID(projectID);
            if(project!=null){
                project.setStatus(false);
                projectService.save(project);
            }
        }catch (NoSuchElementException e) {
            String message = "No Project with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        String message = "Success";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PostMapping("/enable/{projectID}")
    public ResponseEntity<String> enable(@PathVariable("projectID") int projectID){
        try{
            Project project = projectService.findByProjectID(projectID);
            if(project!=null){
                project.setStatus(true);
                projectService.save(project);
            }
        }catch (NoSuchElementException e) {
            String message = "No Project with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        String message = "Success";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
    @GetMapping("/searchProjectHasNotRoom")
    public ResponseEntity<List<Project>> searchProjectHasNotRoom(@RequestParam("projectName") String projectName) {
        List<Project> searchResults = projectService.getListProjectHasNotRoom(projectName);
        return ResponseEntity.ok(searchResults);
    }
    @GetMapping("/searchTeamNotInProject")
    public ResponseEntity<List<Team>> getTeamNotInProject(@RequestParam("query") String query,@RequestParam int projectID) {
        List<Team> searchResults = teamProjectService.findTeamNotInProject(query,projectID);
        return ResponseEntity.ok(searchResults);
    }
    @PostMapping("/addTeamToProject/{projectID}")
    public ResponseEntity<?> addTeamToProject(@PathVariable int projectID,@RequestParam String selectedTeams){
        try {
            projectService.findByProjectID(projectID);
            if (projectService.addTeamToProject(projectID,selectedTeams)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "Invalid input data";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NoSuchElementException e) {
            String message = "No project with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @DeleteMapping("/deleteTeamInProject/{projectID}")
    public ResponseEntity<?> deleteTeamInProject(@PathVariable int projectID,@RequestParam String selectedTeams){
        try {
            projectService.findByProjectID(projectID);
            if (projectService.deleteTeamInProject(projectID,selectedTeams)) {
                String message = "Success";
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                String message = "Invalid input data";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        } catch (NoSuchElementException e) {
            String message = "No project with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @GetMapping("/getProjectByRoom/{roomID}")
    public ResponseEntity<List<Project>> getProjectByRoom(@PathVariable int roomID) {
        List<Project> project = projectService.getListByRoomId(roomID);
        return ResponseEntity.ok(project);
    }

    @GetMapping("/getAllProject")
    public ResponseEntity<List<Project>> getAllProject() {
        List<Project> project = projectService.getList();
        return ResponseEntity.ok(project);
    }
}