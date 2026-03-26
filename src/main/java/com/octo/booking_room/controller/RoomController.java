package com.octo.booking_room.controller;

import com.octo.booking_room.dto.room.BookedSlotsResponse;
import com.octo.booking_room.dto.room.CreateRoomRequest;
import com.octo.booking_room.dto.room.RoomResponse;
import com.octo.booking_room.service.room.RoomService;
import com.octo.booking_room.utils.WebResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<WebResponse<List<RoomResponse>>> getAllRooms() {
        List<RoomResponse> response = roomService.getAllRooms();
        return ResponseEntity.ok(new WebResponse<>("success", "Rooms retrieved successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<RoomResponse>> getRoomById(@PathVariable("id") String roomId) {
        RoomResponse response = roomService.getRoomById(roomId);
        return ResponseEntity.ok(new WebResponse<>("success", "Room retrieved successfully", response));
    }

    @GetMapping("/{id}/booked")
    public ResponseEntity<WebResponse<BookedSlotsResponse>> getBookedSlots(
            @PathVariable("id") String roomId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        BookedSlotsResponse response = roomService.getBookedSlots(roomId, date);
        return ResponseEntity.ok(new WebResponse<>("success", "Booked slots retrieved", response));
    }

    @PostMapping
    public ResponseEntity<WebResponse<RoomResponse>> createRoom(@RequestBody CreateRoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new WebResponse<>("success", "Room created successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WebResponse<RoomResponse>> updateRoom(
            @PathVariable("id") String roomId,
            @RequestBody CreateRoomRequest request) {
        RoomResponse response = roomService.updateRoom(roomId, request);
        return ResponseEntity.ok(new WebResponse<>("success", "Room updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<WebResponse<String>> deleteRoom(@PathVariable("id") String roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok(new WebResponse<>("success", "Room deleted successfully", null));
    }
}
