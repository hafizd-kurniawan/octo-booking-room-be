package com.octo.booking_room.entity.room;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_type")
public class RoomType {

  @Id
  @Column(name = "type_id")
  private String typeId;

  private String name;
  private Integer capacity;

  @OneToMany(mappedBy = "roomType")
  private List<Room> rooms;

}
