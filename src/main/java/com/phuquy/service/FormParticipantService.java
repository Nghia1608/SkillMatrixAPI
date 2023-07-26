package com.phuquy.service;

import com.phuquy.JWT.JWTService;
import com.phuquy.entity.*;
import com.phuquy.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class FormParticipantService {
    private final FormParticipantRepository formParticipantRepository;
    private final UserService userService;
    private final TeamService teamService;
    private final ProjectService projectService;

    private final RoomService roomService;

    private final SkillDomainService skillDomainService;
    private final JWTService jwtService;
    private final CookieService cookieService;
    @Autowired
    private FormService formService;



    public Boolean save(FormParticipant form){ return (formParticipantRepository.save(form) != null); }
    public Boolean checkParticipantInForm(int formID,int userID){
        if(formParticipantRepository.findFormHasParticipant(formID,userID)!=null){
            return true;
        }else{
            return false;
        }
    }
    public List<User> formParticipantList(int formID){
        return formParticipantRepository.findUserHasParticipant(formID);
    }

    public List<User> formParticipantListInManagerPage(int formID){
        return formParticipantRepository.findUserHasParticipantInManagerPage(formID);
    }

    public String calculateProgress(int formID, int userID){
        List<SkillDomain> skillDomainHasRate = skillDomainService.getListDomainNameHasRateAndSelfRate0(userID,formID);
        List<SkillDomain> skillDomainNotRate = skillDomainService.getListDomainNameByFormID(formID);
        float countRate = skillDomainHasRate.size();
        float countNotRate = skillDomainNotRate.size();
        float progress = (countRate / countNotRate) * 100;
        DecimalFormat decimalFormat = new DecimalFormat("#.##"); // Format to two decimal places
        return decimalFormat.format(progress);
    }
    public static boolean validateInput(String x) {
        String pattern = "^\\d+(,\\d+)*$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(x);
        return matcher.matches();
    }
    public boolean addInvitedMemberToForm(String listUser,int formID, HttpServletRequest request) throws Exception {
        String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
        User user = userService.findUserByUsername(username);
        if(validateInput(listUser)){
            if(!formService.checkOwnerInForm(formID, (int) user.getUser_id())
                    && !formService.checkManagerInForm(formID, (int) user.getUser_id())){
                return false;
            }else {
                if(listUser.trim().equals("")){
                    return false;
                }
                String[] usersID;
                if(listUser.contains(",")){
                    usersID = listUser.split(",");
                }else{
                    usersID = new String[]{listUser};
                }
                for(int i=0;i<usersID.length;i++){
                    if (checkParticipantInForm(formID, Integer.parseInt(usersID[i])) == false) {
                        FormParticipant formParticipant = new FormParticipant();
                        //set userID
                        User userToSet = new User();
                        userToSet.setUser_id(Integer.parseInt(usersID[i]));
                        formParticipant.setUser(userToSet);
                        //set formID
                        Form form = formService.getFormById(formID);
                        form.setFormID(formID);
                        formParticipant.setForm(form);
                        save(formParticipant);
                    }
                }
                return true;
            }
        }else{
            return false;
        }
    }
    public boolean addMemberInTeamToForm(int formID,int teamID){
        List<User> listUser = userService.findUserInTeam(teamID);
        if(listUser.isEmpty()){
            return false;
        }
        Form form = new Form();
        form.setFormID(formID);
        for (int i = 0; i < listUser.size(); i++) {
            //check userID in form or No ?
            if (checkParticipantInForm(formID, (int) listUser.get(i).getUser_id()) == false) {
                FormParticipant formParticipant = new FormParticipant();
                User user = new User();
                user.setUser_id((int) listUser.get(i).getUser_id());
                formParticipant.setForm(form);
                formParticipant.setUser(user);
                save(formParticipant);
            }
        }
        return true;
    }
    public boolean addProjectInRoomToForm(int formID,int projectID){
        List<Team> listTeam = teamService.findByProjectId(projectID);
        if(listTeam.isEmpty()){
            return false;
        }
        for(int i=0;i<listTeam.size();i++){
            addMemberInTeamToForm(formID,listTeam.get(i).getTeamID());
        }
        return true;
    }
    public boolean addRoomToForm(int formID,int roomID){

        //Check it contain a child room or no.if no room child in this room.this is the smallest room level
        if(roomService.CheckRoomNotContainChildRoom(roomID)==true){
            //False if room not contain room and project (empty room)
            List<Project> projects = projectService.getListByRoomId(roomID);
            if(projects.isEmpty()){
                return false;
            }
            getProjectInRoom(formID,roomID);
        }
        //SELECT All Room Contain by this room,and project in this room
        else{
            //Get project in this room
            getProjectInRoom(formID,roomID);
            //Get all room contained in this room
            List<Room> roomList =  findAllRoomContainByRoom(roomID);
            for(Room room :roomList){
                if(roomService.CheckRoomNotContainChildRoom(room.getRoomID())){
                    getProjectInRoom(formID,room.getRoomID());
                }else {
                    getProjectInRoom(formID,room.getRoomID());
                    addRoomToForm(formID,room.getRoomID());
                }
            }
        }
        return true;
    }
    //get all room contained by a roomID
    public List<Room> findAllRoomContainByRoom(int roomID){
        return roomService.findAllRoomContain(roomID);
    }
    //get all project in room -> add project to form.
    public void getProjectInRoom(int formID,int roomID){
        List<Project> listProject = projectService.getListByRoomId(roomID);
        for (Project project : listProject) {
            addProjectInRoomToForm(formID, (int) project.getProjectID());
        }
    }


}
