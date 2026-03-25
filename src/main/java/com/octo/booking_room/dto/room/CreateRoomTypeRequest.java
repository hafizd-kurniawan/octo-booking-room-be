package com.octo.booking_room.dto.room;

import lombok.Data;

@Data
public class CreateRoomTypeRequest {
    private String name;
    private Integer capacity;
}