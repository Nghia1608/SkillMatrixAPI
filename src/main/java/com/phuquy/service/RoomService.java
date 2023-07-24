package com.phuquy.service;

import com.phuquy.entity.Room;
import com.phuquy.repository.RoomRepository;
import com.phuquy.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final  RoomRepository repository;
    public void saveRoom(Room room){ repository.save(room); }
    public List<Room> listAll(){
        return repository.findAll();
    }

    public Room findByID(int ID) { return repository.findById(ID).get(); }
    public Room findByName(String roomName) { return repository.findByRoomName(roomName); }
    public Room findByProjectID(int projectID) { return repository.findByProjectID(projectID); }
    public boolean CheckRoomNotContainChildRoom(int roomID) {
        List<Room> room = repository.findAllRoomByContainRoom(roomID);
        if (room == null || room.isEmpty()) {
            return true;
        }
        return false;
    }

    public List<Room> findAllRoomContain(int roomID){
        return repository.findAllRoomByContainRoom(roomID);
    }

    public List<Room> getList(){ return repository.findAll(); }

}
