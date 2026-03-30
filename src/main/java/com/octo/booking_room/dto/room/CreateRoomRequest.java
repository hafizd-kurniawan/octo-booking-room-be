package com.octo.booking_room.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Floor is required")
    private Integer floor;

    @NotBlank(message = "Type ID is required")
    @JsonProperty("type_id")
    private String typeId;
}
