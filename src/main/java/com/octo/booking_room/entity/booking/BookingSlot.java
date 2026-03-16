package com.octo.booking_room.entity.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "booking_slot")
public class BookingSlot {

  @Id
  @Column(name = "slot_id")
  private String slotId;

  @ManyToOne
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  @Column(name = "start_hour", nullable = false)
  private LocalDateTime startHour;

  @Column(name = "end_hour", nullable = false)
  private LocalDateTime endHour;
}
