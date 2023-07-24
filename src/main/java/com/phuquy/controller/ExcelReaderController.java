package com.phuquy.controller;


import com.phuquy.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/excel")
public class ExcelReaderController {
    private final FormService formService;

    @PostMapping("/read")
    public ResponseEntity<String> reader(@RequestParam("file") MultipartFile file, HttpServletRequest request){
        if(formService.createFormByExcel(file,request)){
            String message = "Success";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }else{
            String message = "Username or email has exits";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
    }

}
