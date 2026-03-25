package com.octo.booking_room.service.room;

import com.octo.booking_room.dto.room.CreateRoomTypeRequest;
import com.octo.booking_room.dto.room.RoomTypeDto;
import java.util.List;

public interface RoomTypeService {
    RoomTypeDto createRoomType(CreateRoomTypeRequest request);
    List<RoomTypeDto> getAllRoomTypes();
    RoomTypeDto getRoomTypeById(String typeId);
    RoomTypeDto updateRoomType(String typeId, CreateRoomTypeRequest request);
    void deleteRoomType(String typeId);
}
