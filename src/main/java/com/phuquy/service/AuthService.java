package com.phuquy.service;

import com.phuquy.entity.*;
import com.phuquy.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final CookieService cookieService;
    private final UserService userService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final TeamService teamService;
    private final TeamMemberService teamMemberService;
    public boolean authenticate(String username ,String password, HttpServletResponse response){
        if(username.trim().equals("") || password.equals("")){
            return false;
        }
        if(userRepo.findUserByUsernameAndPassword(username.trim(), password.trim()) != null){
            cookieService.generateTokenWhileLogin(username.trim(),response);
            return true;
        }
        return false;
    }
    public boolean register(Map<String,String> data){
        try {
            String username = data.get("username");
            String password = data.get("password");
            String email = data.get("email");
            String gender = data.get("gender");
            String birth = data.get("birth");
            String phone = data.get("phone");
            int roleID = Integer.parseInt(data.get("roleID"));
            int teamID = Integer.parseInt(data.get("teamID"));
            //Check data
            if(userService.CheckUsernameExist(username)||userService.CheckEmailExist(email)){
                return false;
            }
            //Check birth
            String datePattern = "^(\\d{4})/(0?[1-9]|1[0-2])/(0?[1-9]|[12]\\d|3[01])$";
            if(!birth.matches(datePattern)){
                return false;
            }
            //Check email
            String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if(!email.matches(emailPattern)){
                return false;
            }
            //Save to user table
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setGender(gender);
            user.setBirthday(birth);
            user.setPhoneNumber(phone);
            user.setStatus(true);
            userRepo.save(user);
            //Save to user_role table
            UserRole userRole = new UserRole();
            User userHasSave = userService.findUserByUsername(username);
            Role role = roleService.findById(roleID);
            userRole.setUser(userHasSave);
            userRole.setRole(role);
            userRoleService.save(userRole);
            //Save to teamMember
            TeamMember teamMember = new TeamMember();
            Team team = teamService.findById(teamID);
            teamMember.setTeam(team);
            teamMember.setUser(userHasSave);
            teamMemberService.save(teamMember);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    public boolean logout(HttpServletResponse response){
        String[] cookieNames = {"access_token", "refresh_token","username"};
        for (String cookieName : cookieNames) {
            Cookie cookie = new Cookie(cookieName, null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
        return true;
    }
}
