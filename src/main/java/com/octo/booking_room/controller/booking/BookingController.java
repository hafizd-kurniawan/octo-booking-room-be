package com.octo.booking_room.controller.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.CancelBookingResponse;
import com.octo.booking_room.dto.booking.CreateBookingRequest;
import com.octo.booking_room.entity.booking.CreateBookingResponse;
import com.octo.booking_room.utils.WebResponse;
import com.octo.booking_room.service.booking.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/my")
    public ResponseEntity<WebResponse<List<BookingBasicResponse>>> getMyBookings(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("customer_id") String customerId) {
        List<BookingBasicResponse> response = bookingService.getMyBookings(customerId);
        return ResponseEntity.ok(new WebResponse<>("success", "Bookings retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<BookingDetailResponse>> getBookingDetail(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("id") String id) {
        BookingDetailResponse response = bookingService.getBookingDetail(id);
        return ResponseEntity.ok(new WebResponse<>("success", "Booking detail retrieved", response));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<WebResponse<CancelBookingResponse>> cancelBooking(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("id") String id) {
        CancelBookingResponse response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(new WebResponse<>("success", "Booking cancelled successfully", response));
    }

    @PostMapping
    public ResponseEntity<WebResponse<CreateBookingResponse>> createBooking(
            @RequestBody CreateBookingRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CreateBookingResponse response = bookingService.createBooking(email, request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new WebResponse<>("success", "Booking created successfully", response));
    }
}
