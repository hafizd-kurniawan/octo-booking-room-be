package com.octo.booking_room.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {
    private String name;
    private Integer floor;
    @JsonProperty("type_id")
    private String typeId;
}
