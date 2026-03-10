package com.octo.booking_room.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "room")
public class Room {

  @Id
  @Column(name = "room_id")
  private String roomId;

  private String name;
  private Integer floor;

  @ManyToOne
  @JoinColumn(name = "type_id")
  private RoomType roomType;

  @OneToMany(mappedBy = "room")
  private List<Booking> bookings;

}
