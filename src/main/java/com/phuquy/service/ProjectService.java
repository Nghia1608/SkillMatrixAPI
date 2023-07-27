package com.phuquy.service;

import com.phuquy.entity.*;
import com.phuquy.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository repository;
    private final TeamService teamService;
    private final TeamProjectService teamProjectService;
    private final RoomService roomService;
    public boolean CheckProjectID(String projectID){
        if(!projectID.matches("\\d+") || projectID.length()>9){
            return false;
        }
        return true;
    }
    public List<Project> getList(){ return repository.findAll(); }
    public List<Project> getListByRoomId(int id){ return repository.findByRoom_RoomID(id); }
    public void save(Project project){ repository.save(project); }

    public Project findByProjectID(long id){
        return  repository.findById(id).get();
    }
    public Project findByProjectName(String name){
        return  repository.findByProjectName(name);
    }
    public List<Project> getListProjectHasNotRoom(String projectName){ return repository.findProjectHasNotRoom(projectName); }
    public List<Project> getListProjectByTeam(int teamID){
        return repository.findProjectByTeam(teamID);
    }
    public boolean checkDataInput(String x){
        if(x.matches("[a-zA-Z ]+") && x.length()<50){
            return true;
        }
        return false;
    }

    public static boolean validateInput(String x) {
        String pattern = "^\\d+(,\\d+)*$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(x);
        return matcher.matches();
    }
    public boolean addTeamToProject(int projectID, String selectedTeams) {
        if(validateInput(selectedTeams)){
            String[] listID;
            if(selectedTeams.trim().equals("")){
                return false;
            }
            if(selectedTeams.contains(",")){
                listID = selectedTeams.split(",");
            }else{
                listID = new String[]{selectedTeams};
            }
            for (String ID : listID) {
                List<Project> projectAlreadyIn = getListProjectByTeam(Integer.parseInt(ID));
                Team teamToAdd = teamService.findById(Integer.parseInt(ID));

                if(projectAlreadyIn!=null && teamToAdd!=null){

                    for(Project project : projectAlreadyIn){
                        if(project.getProjectID()!=projectID){
                            TeamProject teamProject= new TeamProject();
                            teamProject.setProject(project);
                            teamProject.setTeam(teamToAdd);
                            teamProjectService.save(teamProject);
                        }
                    }
                }
            }
            return true;
        }else{
            return false;
        }
    }
    public boolean deleteTeamInProject(int projectID, String selectedTeams) {
        if(validateInput(selectedTeams)){
            String[] listID;
            if(selectedTeams.trim().equals("")){
                return false;
            }
            if(selectedTeams.contains(",")){
                listID = selectedTeams.split(",");
            }else{
                listID = new String[]{selectedTeams};
            }
            for (String ID : listID) {
                Project project = findByProjectID(projectID);
                Team teamToDelete = teamService.findById(Integer.parseInt(ID));
                if(project!=null && teamToDelete!=null){
                    TeamProject teamProject = teamProjectService.findTeamProjectByProjectID(teamToDelete,project);
                    teamProjectService.delete(teamProject);
                }
            }
            return true;
        }else{
            return false;
        }
    }

}
