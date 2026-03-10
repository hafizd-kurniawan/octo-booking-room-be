package com.octo.booking_room.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "booking")
public class Booking {

  @Id
  @Column(name = "booking_id")
  private String bookingId;

  @Enumerated(EnumType.STRING)
  private BookingStatus status;

  private LocalDate date;

  @ManyToOne
  @JoinColumn(name = "customer_id")
  private Customer customer;

  @ManyToOne
  @JoinColumn(name = "room_id")
  private Room room;

  @OneToMany(mappedBy = "booking")
  private List<BookingSlot> bookingSlots;

}
