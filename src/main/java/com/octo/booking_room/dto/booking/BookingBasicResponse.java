package com.octo.booking_room.dto.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingBasicResponse {
    @JsonProperty("booking_id")
    private String bookingId;
    private RoomDto room;
    private LocalDate date;
    private String status;
    @JsonProperty("customer_email")
    private String customerEmail;
    private java.util.List<BookingSlotBasicResponse> slots;

}
