package com.octo.booking_room.entity.booking;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

  public BookingSlot(LocalDateTime startHour, LocalDateTime endHour) {
    this.startHour = startHour;
    this.endHour = endHour;
  }
}
