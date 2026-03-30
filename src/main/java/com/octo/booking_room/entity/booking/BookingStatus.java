package com.octo.booking_room.entity.booking;

public enum BookingStatus {

  BOOKED,
  CANCELLED;

  public boolean isActive() {
    return this != CANCELLED;
  }

}
