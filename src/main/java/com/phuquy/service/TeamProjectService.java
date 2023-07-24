package com.phuquy.service;

import com.phuquy.entity.Project;
import com.phuquy.entity.Team;
import com.phuquy.entity.TeamProject;
import com.phuquy.entity.User;
import com.phuquy.repository.TeamMemberRepository;
import com.phuquy.repository.TeamProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamProjectService {
    private final  TeamProjectRepository teamProjectRepository;
    public void save(TeamProject teamProject){
        teamProjectRepository.save(teamProject);
    }
    public void delete(TeamProject teamProject){
        teamProjectRepository.delete(teamProject);
    }
    public List<Team> findTeamNotInProject (String teamName,int projectID){
        return teamProjectRepository.findTeamNotInProject(teamName,projectID);
    }
    public TeamProject findTeamProjectByProjectID(Team team, Project project){
        return teamProjectRepository.findTeamProjectByTeamAndProject(team,project);
    }
}
