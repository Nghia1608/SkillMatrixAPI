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
    public ResponseEntity<?> addNewProject(@RequestParam String projectName,@RequestParam String roomID){
        if(!projectService.checkDataInput(projectName)){
            String message = "Project name only allow a-z and Space, Length must < 50";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        if(!projectService.CheckProjectID(roomID)){
            String message = "RoomId only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Room room = roomService.findByID(Integer.parseInt(roomID));
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
    public ResponseEntity<String> editProject(@PathVariable("projectID") String projectID, @RequestParam String projectName, @RequestParam String roomID){
        if(!projectService.checkDataInput(projectName)){
            String message = "Only allow a-z and Space, Length must < 50";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        if(!projectService.CheckProjectID(projectID)){
            String message = "ProjectID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        if(!projectService.CheckProjectID(roomID)){
            String message = "RoomId only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Room room = roomService.findByID(Integer.parseInt(roomID));
            Project project = projectService.findByProjectID(Long.parseLong(projectID));
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
    public ResponseEntity<String> disable(@PathVariable("projectID") String projectID){
        if(!projectService.CheckProjectID(projectID)){
            String message = "ProjectID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Project project = projectService.findByProjectID(Long.parseLong(projectID));
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
    public ResponseEntity<String> enable(@PathVariable("projectID") String projectID){
        if(!projectService.CheckProjectID(projectID)){
            String message = "ProjectID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Project project = projectService.findByProjectID(Long.parseLong(projectID));
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
    public ResponseEntity<Object> searchProjectHasNotRoom(@RequestParam("projectName") String projectName) {
        List<Project> searchResults = projectService.getListProjectHasNotRoom(projectName);
        if(searchResults==null||searchResults.isEmpty()){
            String message = "No project";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(searchResults);
    }
    @GetMapping("/searchTeamNotInProject")
    public ResponseEntity<Object> getTeamNotInProject(@RequestParam("query") String query,@RequestParam String projectID) {
        if (!projectService.CheckProjectID(projectID)) {
            String message = "ProjectID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        List<Team> searchResults = teamProjectService.findTeamNotInProject(query, Integer.parseInt(projectID));
        if (searchResults == null || searchResults.isEmpty()) {
            String message = "No project";
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(searchResults);
    }
    @PostMapping("/addTeamToProject/{projectID}")
    public ResponseEntity<?> addTeamToProject(@PathVariable String projectID,@RequestParam String selectedTeams){
        if (!projectService.CheckProjectID(projectID)) {
            String message = "ProjectID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            projectService.findByProjectID(Long.parseLong(projectID));
            if (projectService.addTeamToProject(Integer.parseInt(projectID),selectedTeams)) {
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
    public ResponseEntity<?> deleteTeamInProject(@PathVariable String projectID,@RequestParam String selectedTeams){
        if (!projectService.CheckProjectID(projectID)) {
            String message = "ProjectID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try {
            projectService.findByProjectID(Long.parseLong(projectID));
            if (projectService.deleteTeamInProject(Integer.parseInt(projectID),selectedTeams)) {
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
    public ResponseEntity<Object> getProjectByRoom(@PathVariable String roomID) {
        if (!projectService.CheckProjectID(roomID)) {
            String message = "RoomID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            roomService.findByID(Integer.parseInt(roomID));
            List<Project> project = projectService.getListByRoomId(Integer.parseInt(roomID));
            if(project==null||project.isEmpty()){
                String message = "No project by room";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
            return ResponseEntity.ok(project);
        }catch(NoSuchElementException e){
            String message = "No room with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @GetMapping("/getAllProject")
    public ResponseEntity<Object> getAllProject() {
        List<Project> project = projectService.getList();
        if(project==null||project.isEmpty()){
            String message = "No project ";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(project);
    }
}