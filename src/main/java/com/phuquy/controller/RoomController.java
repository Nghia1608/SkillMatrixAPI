package com.phuquy.controller;

import com.phuquy.entity.Room;
import com.phuquy.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/addNewRoom")
    public ResponseEntity<String> addNewRoom(@RequestParam String roomName, @RequestParam int containRoom){
        Room room = new Room();
        if(!roomName.matches("[a-zA-Z ]+") || roomName.length()>50){
            String message = "Only allow a-z and Space, Length must < 50";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        if(containRoom!=-1){
            try{
                Room parrentRoom = roomService.findByID(containRoom);
                    room.setRoomName(roomName);
                    room.setContainRoom(parrentRoom);
                    room.setActive(true);
                    roomService.saveRoom(room);
            }catch (NoSuchElementException e) {
                String message = "No Room with this ID";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
            }
        }else {
            room.setRoomName(roomName);
            room.setActive(true);
            roomService.saveRoom(room);
        }
        String message = "Success";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PostMapping("/disable/{roomID}")
    public ResponseEntity<String> disable(@PathVariable("roomID") int roomID){
        try{
            Room room = roomService.findByID(roomID);
            if(room!=null){
                room.setActive(false);
                roomService.saveRoom(room);
            }
        }catch (NoSuchElementException e) {
            String message = "No Room with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        String message = "Success";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PostMapping("/enable/{roomID}")
    public ResponseEntity<String> enable(@PathVariable("roomID") int roomID){
        try{
            Room room = roomService.findByID(roomID);
            if(room!=null){
                room.setActive(true);
                roomService.saveRoom(room);
            }
        }catch (NoSuchElementException e) {
            String message = "No Room with this ID";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        String message = "Success";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @GetMapping("/getListRoom")
    public ResponseEntity<List<Room>> getListRoom(){
        List<Room> listRoom = roomService.listAll();
        return ResponseEntity.ok(listRoom);
    }
}
