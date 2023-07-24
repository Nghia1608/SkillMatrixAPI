package com.phuquy.service;


import com.phuquy.entity.*;
import com.phuquy.repository.TeamMemberRepository;
import com.phuquy.repository.TeamProjectRepository;
import java.util.regex.*;
import com.phuquy.entity.Team;
import com.phuquy.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    //Constructor injection
    private final  TeamRepository teamRepository;
    private final  TeamProjectRepository teamProjectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserService userService;
    private final TeamMemberService teamMemberService;

    //Method
    //Only allow "1,2,3" or "1"
    public static boolean validateInput(String x) {
        String pattern = "^\\d+(,\\d+)*$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(x);
        return matcher.matches();
    }
    public Team findTeamByUserID(int userID){
        return teamRepository.findTeamByUserID(userID);
    }
    public List<Team> listAll(){
        return teamRepository.findAll();
    }

    public void save(Team team){
        teamRepository.save(team);
    }
    public Team findById(int id){
        return teamRepository.findById(id).get();
    }
    public Team findByTeamName(String teamName){
        return teamRepository.findByTeamName(teamName);
    }

    public List<Team> findByProjectId(int id) {
        return teamProjectRepository.findByProject_ProjectID(id);
    }
    public boolean addMemberToTeam(int teamID,String listUserID){
        if(validateInput(listUserID)){
            String[] listID;
            if (listUserID.trim().equals("")) {
                return false;
            }
            if(listUserID.contains(",")){
                listID = listUserID.split(",");
            }else{
                listID = new String[]{listUserID};
            }
            for(int i=0;i<listID.length;i++){
                Team teamAlreadyIn = findTeamByUserID(Integer.parseInt(listID[i]));
                Team teamToAdd = findById(teamID);
                User user = userService.findUserByUserID(Integer.parseInt(listID[i]));
                if(teamAlreadyIn==null && teamToAdd!=null && user!=null){
                    TeamMember teamMember = new TeamMember();
                    teamMember.setUser(user);
                    teamMember.setTeam(teamToAdd);
                    teamMemberService.save(teamMember);
                }
            }
            return true;
        }else{
            return false;
        }
    }
    public Object findTeamOfTwoUser(int userID1, int userID2){
        Team team = teamMemberRepository.findTeamUserJoinBoth(userID1,userID2);
        if(team==null){
            return "No team";
        }else {
            return team;
        }
    }
}
