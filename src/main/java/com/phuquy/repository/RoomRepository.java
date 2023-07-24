package com.phuquy.repository;

import com.phuquy.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    @Query("SELECT r FROM Room r WHERE r.containRoom.roomID = :roomID ")
    List<Room> findAllRoomByContainRoom(@Param("roomID")int roomID);

    Room findByRoomName(String roomName);

    @Query("SELECT r FROM Room r,Project p where p.room.roomID=r.roomID and p.projectID = :projectID ")
    Room findByProjectID(@Param("projectID")int projectID);
}
