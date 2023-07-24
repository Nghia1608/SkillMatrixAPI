package com.phuquy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private int roomID;
    @ManyToOne
    @JoinColumn(name = "containRoom")
    private Room containRoom;
    @Column(name = "room_name")
    private String roomName;
    private Boolean active;
}
