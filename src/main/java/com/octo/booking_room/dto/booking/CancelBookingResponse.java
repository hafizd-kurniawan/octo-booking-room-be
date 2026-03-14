package com.octo.booking_room.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelBookingResponse {
    @JsonProperty("booking_id")
    private String bookingId;
    private String status;

}
