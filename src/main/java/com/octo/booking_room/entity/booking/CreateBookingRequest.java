package com.octo.booking_room.entity.booking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateBookingRequest {
    @JsonProperty("room_id")
    private String roomId;
    private LocalDate date;
    private List<SlotRequest> slots;

    @Data
    public static class SlotRequest {
        @JsonProperty("start_hour")
        private java.time.LocalDateTime startHour;
        @JsonProperty("end_hour")
        private java.time.LocalDateTime endHour;
    }    
}
