package com.octo.booking_room.dto.booking;

import lombok.Data;
 
@Data
public class BookingFilter {
  private String roomId;
  private String roomTypeId;
  private Integer month;
  private Integer year;
}
