package com.octo.booking_room.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "room_type")
public class RoomType {

  @Id
  @Column(name = "type_id")
  private String typeId;

  private Integer capacity;

  @OneToMany(mappedBy = "roomType")
  private List<Room> rooms;

}
