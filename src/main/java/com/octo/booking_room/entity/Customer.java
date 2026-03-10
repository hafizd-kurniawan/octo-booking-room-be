package com.octo.booking_room.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

  @Column(name = "is_admin")
  private Boolean isAdmin;

  @OneToMany(mappedBy = "customer")
  private List<Booking> bookings;

}
