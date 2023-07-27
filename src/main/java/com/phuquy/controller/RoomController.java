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
    public ResponseEntity<String> addNewRoom(@RequestParam String roomName, @RequestParam String containRoom){
        if(!roomService.CheckRoomID(containRoom)){
            String message = "Contain room only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        Room room = new Room();
        if(!roomName.matches("[a-zA-Z ]+") || roomName.length()>50){
            String message = "Room name only allow a-z and Space, Length must < 50";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        if(Integer.parseInt(containRoom)!=-1){
            try{
                Room parrentRoom = roomService.findByID(Integer.parseInt(containRoom));
                    room.setRoomName(roomName);
                    room.setContainRoom(parrentRoom);
                    room.setActive(true);
                    roomService.saveRoom(room);
            }catch (NoSuchElementException e) {
                String message = "No Room Contain with this ID";
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
    public ResponseEntity<String> disable(@PathVariable("roomID") String roomID){
        if(!roomService.CheckRoomID(roomID)){
            String message = "RoomID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Room room = roomService.findByID(Integer.parseInt(roomID));
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
    public ResponseEntity<String> enable(@PathVariable("roomID") String roomID){
        if(!roomService.CheckRoomID(roomID)){
            String message = "RoomID only number and length < 10";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        try{
            Room room = roomService.findByID(Integer.parseInt(roomID));
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
    public ResponseEntity<Object> getListRoom(){
        List<Room> listRoom = roomService.listAll();
        if(listRoom==null||listRoom.isEmpty()){
            String message = "No room";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok(listRoom);
    }
}
