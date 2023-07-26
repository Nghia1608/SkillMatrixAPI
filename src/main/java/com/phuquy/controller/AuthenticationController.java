package com.phuquy.controller;


import com.phuquy.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthService service;

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
        if(service.register(data)){
            String message = "Success";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }else{
            String message = "Username or email has exits";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response){
        service.logout(response);
        String message = "Success";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}