package com.octo.booking_room.controller;

import com.octo.booking_room.dto.room.RoomResponse;
import com.octo.booking_room.dto.room.BookedSlotsResponse;
import com.octo.booking_room.service.room.RoomService;
import com.octo.booking_room.utils.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/{id}/booked")
    public ResponseEntity<WebResponse<BookedSlotsResponse>> getBookedSlots(
            @PathVariable("id") String roomId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        BookedSlotsResponse response = roomService.getBookedSlots(roomId, date);
        return ResponseEntity.ok(new WebResponse<>("success", "Booked slots retrieved", response));
    }
}
