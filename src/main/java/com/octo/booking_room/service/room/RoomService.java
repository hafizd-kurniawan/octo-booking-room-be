package com.octo.booking_room.service.room;

import com.octo.booking_room.dto.room.RoomResponse;
// import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.dto.room.BookedSlotsResponse;
import com.octo.booking_room.dto.room.CreateRoomRequest;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    List<RoomResponse> getAllRooms();
    BookedSlotsResponse getBookedSlots(String roomId, LocalDate date);
    RoomResponse getRoomById(String roomId);
    RoomResponse createRoom(CreateRoomRequest roomRequest);
    RoomResponse updateRoom(String roomId, CreateRoomRequest roomRequest);
    void deleteRoom(String roomId);
}
