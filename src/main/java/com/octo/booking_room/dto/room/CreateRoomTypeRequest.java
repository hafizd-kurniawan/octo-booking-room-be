package com.octo.booking_room.dto.room;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoomTypeRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Capacity is required")
    private Integer capacity;
}