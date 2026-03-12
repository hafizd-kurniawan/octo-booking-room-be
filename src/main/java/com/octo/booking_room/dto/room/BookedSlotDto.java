package com.octo.booking_room.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookedSlotDto {
    @JsonProperty("booking_id")
    private String bookingId;
    @JsonProperty("start_hour")
    private LocalDateTime startHour;
    @JsonProperty("end_hour")
    private LocalDateTime endHour;
    private String status;
}