package com.octo.booking_room.entity.room;

import com.octo.booking_room.entity.booking.Booking;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

