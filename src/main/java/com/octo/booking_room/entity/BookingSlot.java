package com.octo.booking_room.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_slot")
public class BookingSlot {

  @Id
  @Column(name = "slot_id")
  private String slotId;

  @Column(name = "start_hour")
  private LocalDateTime startHour;

  @Column(name = "end_hour")
  private LocalDateTime endHour;

  @ManyToOne
  @JoinColumn(name = "booking_id")
  private Booking booking;

}
