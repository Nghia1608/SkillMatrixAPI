package com.phuquy.service;

import com.phuquy.JWT.JWTService;
import com.phuquy.entity.*;
import com.phuquy.repository.UserRateRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRateService {

    private final UserRateRepository repository;
    private final JWTService jwtService;
    private final CookieService cookieService;
    private final UserService userService;
    private final SkillService skillService;
    private final FormParticipantService formParticipantService;
    private final SkillDomainService skillDomainService;
    @Autowired
    private FormService formService;

    public void save(UserRate form){
        repository.save(form);
    }
    public Boolean checkExist(User user, Skill skill, Form form){
        return repository.findByUserIDAndSkillIDAndForm(user, skill, form) != null;
    }
    public User findUserByUserRateID(long userRateID){
        return repository.findUserByUserRate(userRateID);
    }
    public UserRate findByUserAndSkillAndForm(User user, Skill skill, Form form){
        return repository.findByUserIDAndSkillIDAndForm(user, skill, form);
    }
    //Find result UserRate By DomainID and FormID
    public List<UserRate> findUserRateByDomainID(long domainID, int formID){
        return repository.findByDomainId(domainID,formID);
    }
    public List<UserRate> findOneUserRateByDomainID(int formID,int userID,int domainID){
        return repository.findOneUserByDomainId(formID,userID, (long) domainID);
    }
    public List<UserRate> findUserRateBySkillID(int formID,int skillID){
        return repository.findUserBySkillID(formID,skillID);
    }
    public static boolean validateInput(String x) {
        String pattern = "^\\d+(,\\d+)*$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(x);
        return matcher.matches();
    }
    public boolean submitRate(int formID,Map<String, String> data, HttpServletRequest request) throws Exception {
        try{
            String skillID = data.get("skillID");
            String rate = data.get("rate");
            if (skillID == null || skillID.isEmpty() ||
                    rate == null || rate.isEmpty()) {
                return false;
            }
            if(!validateInput(skillID) || !validateInput(rate)){
                return false;
            }
            String[] listSkillID;
            if(skillID.contains(",")){
                listSkillID = skillID.split(",");
            }else{
                listSkillID = new String[]{skillID};
            }
            String[] listRate;
            if(rate.contains(",")){
                listRate = rate.split(",");
            }else{
                listRate = new String[]{rate};
            }
            String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
            User user = userService.findUserByUsername(username);
            Form form = formService.getFormById(formID);
            for(int i=0;i<listSkillID.length;i++){
                Skill skill = skillService.findById(Integer.parseInt(listSkillID[i]));
                if(checkExist(user,skill,form)){
                    UserRate userRate1 = findByUserAndSkillAndForm(user,skill,form);
                    userRate1.setSelfRate(Integer.parseInt(listRate[i]));
                    save(userRate1);
                }else{
                    UserRate userRate1 = new UserRate();
                    userRate1.setForm(form);
                    userRate1.setUserID(user);
                    userRate1.setSkillID(skill);
                    userRate1.setSelfRate(Integer.parseInt(listRate[i]));
                    userRate1.setManagerRate(0);
                    save(userRate1);
                }
            }
            return true;
        }catch (NoSuchElementException e){
            return false;
        }
    }
    public boolean submitManagerRate(int formID,Map<String, String> data, HttpServletRequest request) throws Exception {
        try{
            String skillID = data.get("skillID");
            String rate = data.get("rate");
            String userID = data.get("userIDToSubmit");
            if (skillID == null || skillID.isEmpty() ||
                    userID == null || userID.isEmpty() ||
                    rate == null || rate.isEmpty()) {
                return false;
            }
            if(!validateInput(skillID) || !validateInput(rate)){
                return false;
            }
            String[] listSkillID;
            if(skillID.contains(",")){
                listSkillID = skillID.split(",");
            }else{
                listSkillID = new String[]{skillID};
            }
            String[] listRate;
            if(rate.contains(",")){
                listRate = rate.split(",");
            }else{
                listRate = new String[]{rate};
            }
            String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
            User managerCurrent = userService.findUserByUsername(username);
            if(!formService.checkOwnerInForm(formID, (int) managerCurrent.getUser_id())
                    && !formService.checkManagerInForm(formID, (int) managerCurrent.getUser_id())){
                return false;
            }else {
                User user = userService.findUserByUserID(Integer.parseInt(userID));
                Form form = formService.getFormById(formID);
                for(int i=0;i<listSkillID.length;i++){
                    Skill skill = skillService.findById(Integer.parseInt(listSkillID[i]));
                    if(checkExist(user,skill,form)){
                        UserRate userRate1 = findByUserAndSkillAndForm(user,skill,form);
                        userRate1.setManagerRate(Integer.parseInt(listRate[i]));
                        save(userRate1);
                    }
                }
                return true;
            }
        }catch (NoSuchElementException e){
            return false;
        }

    }

    public Object getAllDataUserRateByForm(int formID, HttpServletRequest request) throws Exception {
        List<SkillDomain> skillDomain = skillDomainService.getListDomainNameByFormID(formID);
        String username = jwtService.getUsernameFromToken(cookieService.getAccessToken(request));
        User user = userService.findUserByUsername(username);
        if(formParticipantService.checkParticipantInForm(formID, (int) user.getUser_id())==false){
            //Return empty []
            return new Object[0];
        }
        else {
            List<Object> listUserRateWithListSkill = new ArrayList<>();
            for (SkillDomain domain : skillDomain) {
                //Get list Skill by domainID
                List<Skill> skillsInDomain = skillService.getListByDomainID(domain.getDomainID());
                //Get list Skill name
                List<String> skillName = new ArrayList<>();
                for (Skill name : skillsInDomain) {
                    skillName.add(name.getSkillName());
                }
                //Get list UserRate by domainID and FormID
                List<UserRate> userInUserRate = findUserRateByDomainID(domain.getDomainID(), formID);

                //Grouping UserRate data by UserID,Username
                Map<String, List<UserRate>> userGroups = userInUserRate.stream()
                        .collect(Collectors.groupingBy(userRate -> userRate.getUserID().getUser_id() + ", " + userRate.getUserID().getUsername()));

                //Map the Skills with UserRates result
                Map<Object, Object> mapUser = new HashMap<>();
                mapUser.put(skillName, userGroups);
                listUserRateWithListSkill.add(mapUser);
            }

            //Map DomainSkill with Skills
            Map<Object,Object> mapData = new HashMap<>();
            for(int i=0;i<skillDomain.size();i++){
                mapData.put(skillDomain.get(i).getDomainName(),listUserRateWithListSkill.get(i));
            }
            return new Map[]{mapData};
        }
    }

    public Object getUserRateInFormByDomain(int formID,int userID,int domainID) {
        List<UserRate> userInUserRate = findOneUserRateByDomainID(formID, userID, domainID);
        return userInUserRate.stream()
                .collect(Collectors.groupingBy(userRate -> userRate.getUserID().getUser_id() + ", " + userRate.getUserID().getUsername()));
    }

    public Object getUserRateInFormBySkill(int formID,int skillID) {
        List<UserRate> userInUserRate = findUserRateBySkillID(formID, skillID);
        return userInUserRate.stream()
                .collect(Collectors.groupingBy(userRate -> userRate.getUserID().getUser_id() + ", " + userRate.getUserID().getUsername()));
    }
}
