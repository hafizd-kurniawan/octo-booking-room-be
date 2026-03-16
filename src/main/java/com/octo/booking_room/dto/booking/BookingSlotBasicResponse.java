package com.octo.booking_room.dto.booking;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSlotBasicResponse {

  @JsonProperty("slot_id")
  private String slotId;

  @JsonProperty("start_hour")
  @JsonFormat(pattern = "HH:mm")
  private LocalDateTime startHour;

  @JsonProperty("end_hour")
  @JsonFormat(pattern = "HH:mm")
  private LocalDateTime endHour;
}
