package com.phuquy.controller;


import com.phuquy.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthService service;
    private final UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestParam String username,@RequestParam String password, HttpServletResponse response) throws Exception {
        if(service.authenticate(username,password,response)){
            String message = "Success";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }else{
            String message = "Username or Password incorrect !!";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }
    @PostMapping("/registerUser")
    public ResponseEntity<String> toRegister(@RequestBody Map<String,String> data){
        String username = data.get("username");
        String email = data.get("email");
        if(userService.CheckUsernameExist(username)||userService.CheckEmailExist(email)){
            String message = "Username or email has exits";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        if(service.register(data)){
            String message = "Success";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }else{
            String message = "Invalid input data";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response){
        try{
            service.logout(response);
            String message = "Success";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }catch (Exception e){
            String message = "e";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }

    }
}