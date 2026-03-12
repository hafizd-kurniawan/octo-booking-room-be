package com.octo.booking_room.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookedSlotsResponse {
    @JsonProperty("room_id")
    private String roomId;
    private LocalDate date;
    @JsonProperty("booked_slots")
    private List<BookedSlotDto> bookedSlots;
}