package com.octo.booking_room.controller;

import com.octo.booking_room.dto.room.CreateRoomTypeRequest;
import com.octo.booking_room.dto.room.RoomTypeDto;
import com.octo.booking_room.service.room.RoomTypeService;
import com.octo.booking_room.utils.WebResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room-types")
@SecurityRequirement(name = "bearerAuth")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @PostMapping
    public ResponseEntity<WebResponse<RoomTypeDto>> createRoomType(@RequestBody CreateRoomTypeRequest request) {
        RoomTypeDto response = roomTypeService.createRoomType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new WebResponse<>("success", "Room type created successfully", response));
    }

    @GetMapping
    public ResponseEntity<WebResponse<List<RoomTypeDto>>> getAllRoomTypes() {
        List<RoomTypeDto> response = roomTypeService.getAllRoomTypes();
        return ResponseEntity.ok(new WebResponse<>("success", "Room types retrieved successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<RoomTypeDto>> getRoomTypeById(@PathVariable("id") String typeId) {
        RoomTypeDto response = roomTypeService.getRoomTypeById(typeId);
        return ResponseEntity.ok(new WebResponse<>("success", "Room type retrieved successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebResponse<RoomTypeDto>> updateRoomType(
            @PathVariable("id") String typeId,
            @RequestBody CreateRoomTypeRequest request) {
        RoomTypeDto response = roomTypeService.updateRoomType(typeId, request);
        return ResponseEntity.ok(new WebResponse<>("success", "Room type updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WebResponse<String>> deleteRoomType(@PathVariable("id") String typeId) {
        roomTypeService.deleteRoomType(typeId);
        return ResponseEntity.ok(new WebResponse<>("success", "Room type deleted successfully", null));
    }
}
