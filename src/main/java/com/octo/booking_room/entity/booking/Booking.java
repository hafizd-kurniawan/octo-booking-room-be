package com.octo.booking_room.entity.booking;

import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.entity.room.Room;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

