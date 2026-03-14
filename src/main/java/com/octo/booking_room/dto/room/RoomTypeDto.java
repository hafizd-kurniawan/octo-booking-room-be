package com.octo.booking_room.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeDto {
    @JsonProperty("type_id")
    private String typeId;
    private String name;
    private Integer capacity;
}
