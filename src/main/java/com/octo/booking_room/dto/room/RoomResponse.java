package com.octo.booking_room.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    @JsonProperty("room_id")
    private String roomId;
    private String name;
    private Integer floor;
    @JsonProperty("room_type")
    private RoomTypeDto roomType;
}
