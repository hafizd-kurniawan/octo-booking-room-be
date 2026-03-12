package com.octo.booking_room.dto.booking;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSlotResponse {
    private LocalDateTime startHour;
    private LocalDateTime endHour;

}
