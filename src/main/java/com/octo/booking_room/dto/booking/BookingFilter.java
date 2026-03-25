package com.octo.booking_room.dto.booking;

import com.octo.booking_room.entity.booking.BookingStatus;
import lombok.Data;
 
@Data
public class BookingFilter {
  private String roomId;
  private String roomTypeId;
  private Integer month;
  private Integer year;
  private BookingStatus status;
}
