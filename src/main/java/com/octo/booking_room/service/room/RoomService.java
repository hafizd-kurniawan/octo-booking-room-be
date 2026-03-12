package com.octo.booking_room.service.room;

import com.octo.booking_room.dto.room.RoomResponse;
import com.octo.booking_room.dto.room.BookedSlotsResponse;
import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    List<RoomResponse> getAllRooms();
    BookedSlotsResponse getBookedSlots(String roomId, LocalDate date);
}
