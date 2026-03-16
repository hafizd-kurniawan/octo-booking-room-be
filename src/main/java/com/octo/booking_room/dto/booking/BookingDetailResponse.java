package com.octo.booking_room.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailResponse {
    @JsonProperty("booking_id")
    private String bookingId;
    private String room;
    private LocalDate date;
    private String status;
    private java.util.List<BookingSlotBasicResponse> slots;

}
