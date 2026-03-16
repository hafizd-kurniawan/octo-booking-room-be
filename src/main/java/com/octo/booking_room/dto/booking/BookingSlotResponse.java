package com.octo.booking_room.dto.booking;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSlotResponse {

  @JsonProperty("slot_id")
  private String slotId;

  @JsonProperty("start_hour")
  private LocalDateTime startHour;

  @JsonProperty("end_hour")
  private LocalDateTime endHour;
}
