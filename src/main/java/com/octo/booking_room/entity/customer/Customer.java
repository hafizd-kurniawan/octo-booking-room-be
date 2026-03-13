package com.octo.booking_room.entity.customer;

import com.octo.booking_room.entity.booking.Booking;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {

  @Id
  @Column(name = "customer_id")
  private String customerId;

  private String name;
  private String phone;
  private String email;
  private String password;

  @Column(name = "is_admin", length = 1)
  private Boolean isAdmin;

  @OneToMany(mappedBy = "customer")
  private List<Booking> bookings;
}