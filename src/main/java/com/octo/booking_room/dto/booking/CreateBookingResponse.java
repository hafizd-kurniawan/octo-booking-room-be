package com.octo.booking_room.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponse {

  @JsonProperty("booking_id")
  private String bookingId;

  @JsonProperty("room_id")
  private String roomId;

  @JsonProperty("customer_id")
  private String customerId;

  private LocalDate date;

  private String status;

  private List<BookingSlotResponse> slots;
}
