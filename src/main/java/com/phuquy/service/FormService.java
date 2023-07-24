package com.phuquy.service;

import com.phuquy.JWT.JWTService;
import com.phuquy.entity.*;
import com.phuquy.repository.FormRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FormService {
    private final FormRepository repository;
    private final SkillDomainService skillDomainService;
    private final UserService userService;
    private final FormDomainService formDomainService;
    @Autowired
    private  FormParticipantService formParticipantService;
    private final TeamService teamService;
    private final RoomService roomService;
    private final ProjectService projectService;
    private final  JWTService jwtService;
    private final CookieService cookieService;
    private final SkillService skillService;
    @Autowired
    private UserRateService userRateService;

    public Form getFormById(long id){
        return repository.findById(id).get();
    }
    public Form getFormByDomain(int domainID){
        return repository.findByDomainID(domainID);
    }

    public List<Form> getList(){ return repository.findAll(); }
    public Boolean save(Form form){ return (repository.save(form) != null); }


    public List<Form> getFormHasParticipant(int userID){
        return repository.findFormHasParticipant(userID);
    }
    public List<Form> getListFormOwner(int userID){
        return repository.showListFormOwner(userID);
    }
    public List<Form> getListFormManager(int userID){
        return repository.showListFormManager(userID);
    }
    //Find result in FormParticipant has userID and formID
    public boolean checkDomainHasRate(int userID,int formID,int domainID){
        SkillDomain skillDomain = skillDomainService.findById(domainID);
        SkillDomain skillDomainHasRate = skillDomainService.getDomainNameHasRateAndSelfRate(userID,formID,domainID);
        if(skillDomain==skillDomainHasRate){
            return true;
        }
        return false;
    }
    public Boolean checkManagerInForm(int formID,int userID){
        if(repository.findManagerParticipantInForm(formID,userID)!=null){
            return true;
        }else{
            return false;
        }
    }
    //Find result in FormParticipant has userID and formID ( = owner of this form)
    public Boolean checkOwnerInForm(int formID,int userID){
        if(repository.findFormOwner(formID,userID)!=null){
            return true;
        }else{
            return false;
        }
    }

    //Only allow "1,2,3" or "1"
    public static boolean validateInput(String x) {
        String pattern = "^\\d+(,\\d+)*$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(x);
        return matcher.matches();
    }
    public boolean createForm(Map<String,String> data, HttpServletRequest request){
        String surveyName = data.get("surveyName");
        String surveyDeadline = data.get("surveyDeadline");
        String roomSurvey = data.get("roomSurvey");
        String teamSurvey = data.get("teamSurvey");
        String memberSurvey = data.get("memberSurvey");
        String skillDomainList = data.get("skillDomain");
        String projectSurvey = data.get("projectSurvey");

        if(!(validateInput(roomSurvey) &&validateInput(projectSurvey)
                 && validateInput(teamSurvey) && validateInput(memberSurvey) && validateInput(skillDomainList))){
            return false;
        }

        Form form = new Form();
        Random random = new Random();
        int min = 1000000; // Minimum 7-digit number (inclusive)
        int max = 9999999; // Maximum 7-digit number (inclusive)
        int randomNumber = random.nextInt(max - min + 1) + min;
        //Save to table Form
        String username = jwtService.getUsernameFromToken(cookieService.getCookie(request));
        form.setFormID(randomNumber);
        form.setUserID(userService.findUserByUsername(username));
        form.setFormName(surveyName);
        form.setCreateDate(surveyDeadline);
        save(form);
        //
        if(roomSurvey.trim()!=""){
            String[] roomSurveyArray;
            if(roomSurvey.contains(",")){
                roomSurveyArray = roomSurvey.split(",");
            }else{
                roomSurveyArray = new String[]{roomSurvey};
            }
            for (String room : roomSurveyArray) {
                Room roomID = roomService.findByID(Integer.parseInt(room));
                formParticipantService.addRoomToForm(randomNumber,roomID.getRoomID());
            }
        }
        if(projectSurvey.trim()!=""){
            String[] projectSurveyArray;
            if(projectSurvey.contains(",")){
                projectSurveyArray = projectSurvey.split(",");
            }else{
                projectSurveyArray = new String[]{projectSurvey};
            }
            for (String project : projectSurveyArray) {
                Project projectID = projectService.findByProjectID(Long.parseLong(project));
                formParticipantService.addRoomToForm(randomNumber, (int) projectID.getProjectID());
            }
        }
        if(teamSurvey.trim()!=""){
            String[] teamSurveyArray;
            if(teamSurvey.contains(",")){
                teamSurveyArray = teamSurvey.split(",");
            }else{
                teamSurveyArray = new String[]{teamSurvey};
            }
            for (String team : teamSurveyArray) {
                Team team1 = teamService.findById(Integer.parseInt(team));
                formParticipantService.addProjectInRoomToForm(randomNumber,team1.getTeamID()    );
            }
        }

        //Invite member
        if(memberSurvey.trim()!=""){
            String[] memberSurveyArray;
            if(memberSurvey.contains(",")){
                memberSurveyArray = memberSurvey.split(",");
            }else{
                memberSurveyArray = new String[]{memberSurvey};
            }
            for (String member : memberSurveyArray) {
                //set userID
                User userToSet = new User();
                User user = userService.findById(Long.parseLong(member));
                if (formParticipantService.checkParticipantInForm(randomNumber, (int) user.getUser_id()) == false) {
                    userToSet.setUser_id(user.getUser_id());
                    FormParticipant formParticipant = new FormParticipant();
                    formParticipant.setUser(userToSet);
                    //set formID
                    form.setFormID(randomNumber);
                    formParticipant.setForm(form);
                    formParticipantService.save(formParticipant);
                }
            }
        }
        if(skillDomainList.trim()!=""){
            //Save to form Domain
            String[] skillDomainArray;
            if(skillDomainList.contains(",")){
                skillDomainArray = skillDomainList.split(",");
            }else{
                skillDomainArray = new String[]{skillDomainList};
            }
            for (String skill : skillDomainArray) {
                FormDomain formDomain = new FormDomain();
                formDomain.setForm(form);
                SkillDomain skillDomain = skillDomainService.findByDomainName(skill);
                formDomain.setDomain(skillDomain);
                formDomainService.save(formDomain);
            }
        }
        return true;
    }

    public boolean createFormByExcel(@RequestParam("file") MultipartFile file,HttpServletRequest request){
        List<Skill> listSkill = new ArrayList<>();
        if (!file.isEmpty()) {
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                int startRow = 8;
                int lastRow = 0;
//                Get the last row that contain data
                for(int i = startRow;i<sheet.getLastRowNum();i++){
                    Row rowi = sheet.getRow(i);
                    if(rowi.getCell(1).getStringCellValue() == ""){
                        lastRow = i-1;
                        break;
                    }
                }
                Row row;

//                Create a new form
                Form form = new Form();
                Random random = new Random();
                int min = 1000000; // Minimum 7-digit number (inclusive)
                int max = 9999999; // Maximum 7-digit number (inclusive)
                int id = random.nextInt(max - min + 1) + min;
                form.setFormID(id);
                form.setFormName(file.getOriginalFilename());
                form.setCreateDate(new Date(System.currentTimeMillis()).toString());
                String username = jwtService.getUsernameFromToken(cookieService.getCookie(request));
                form.setUserID(userService.findUserByUsername(username));
                save(form);


//                Setting other config for new form
//                Set skill domain list
                row = sheet.getRow(7);
                for(int i = 4;i<row.getLastCellNum();i++){
                    if(row.getCell(i).getStringCellValue() != ""){
                        FormDomain formDomain = new FormDomain();
                        formDomain.setForm(getFormById(id));
                        formDomain.setDomain(skillDomainService.findByDomainName(row.getCell(i).getStringCellValue()));
                        formDomainService.save(formDomain);
                    }
                }
//                Set Member list
                for(int i = startRow+1;i<=lastRow;i++){
                    row = sheet.getRow(i);
                    if(row.getCell(0) != null && row.getCell(0).getCellType() == CellType.NUMERIC){
                        FormParticipant formParticipant = new FormParticipant();
                        formParticipant.setForm(getFormById(id));
                        if(userService.findUserByUsername( String.valueOf(row.getCell(1).getStringCellValue()))==null){
                            User user = new User();
                            user.setUsername(String.valueOf(row.getCell(1)));
                            userService.save(user);
                            formParticipant.setUser(userService.findUserByUsername( String.valueOf(row.getCell(1).getStringCellValue())));
                            formParticipantService.save(formParticipant);
                        }else{

                        }

                    }
                }

//                Get skill list
                Row skillName = sheet.getRow(8);
                for(int k = 4; k<skillName.getLastCellNum();k++){
                    Cell cell = skillName.getCell(k);
                    listSkill.add(skillService.findBySkillName(cell.getStringCellValue()));
                }

//                Get data of user self evaluated
                for(int i = startRow+1;i<=lastRow;i++){
                    row = sheet.getRow(i);
                    for(int j=4; j<row.getLastCellNum(); j++){
                        UserRate userRate = new UserRate();
                        Cell cell = row.getCell(0);
                        userRate.setUserID(userService.findUserByUsername( String.valueOf(row.getCell(1).getStringCellValue())));
                        cell = row.getCell(j);
                        userRate.setSkillID(listSkill.get(j-4));
                        userRate.setSelfRate((int) cell.getNumericCellValue());
                        userRate.setManagerRate(0);
                        userRate.setForm(getFormById(id));
                        userRateService.save(userRate);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            return false;
        }
        return true;
    }


    public boolean updateForm(int formID,String listDomainID, HttpServletRequest request) {
        String username = jwtService.getUsernameFromToken(cookieService.getCookie(request));
        User user = userService.findUserByUsername(username);
        if(validateInput(listDomainID)==false){
            return false;
        }
        Form form = getFormById(formID);
        String[] listID;
        if(listDomainID.contains(",")){
            listID = listDomainID.split(",");
        }else{
            listID = new String[]{listDomainID};
        }
        if(checkOwnerInForm(formID, (int) user.getUser_id())==false
                && checkManagerInForm(formID, (int) user.getUser_id())==false){
        }else {
            for(int i=0;i<listID.length;i++){
                SkillDomain skillDomain = skillDomainService.findById(Integer.parseInt(listID[i]));
                if(getFormByDomain(Integer.parseInt(listID[i]))!=null){
                    FormDomain formDomain = new FormDomain();
                    formDomain.setForm(form);
                    formDomain.setDomain(skillDomain);
                    formDomainService.save(formDomain);
                }
            }
        }
        return true;
    }
}
