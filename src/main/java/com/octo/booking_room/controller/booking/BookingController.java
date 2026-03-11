package com.octo.booking_room.controller.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.CancelBookingResponse;
import com.octo.booking_room.utils.WebResponse;
import com.octo.booking_room.service.booking.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
            @RequestParam("customer_id") String customerId) {
        List<BookingBasicResponse> response = bookingService.getMyBookings(customerId);
        return ResponseEntity.ok(new WebResponse<>("success", "Bookings retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<BookingDetailResponse>> getBookingDetail(@PathVariable("id") String id) {
        BookingDetailResponse response = bookingService.getBookingDetail(id);
        return ResponseEntity.ok(new WebResponse<>("success", "Booking detail retrieved", response));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<WebResponse<CancelBookingResponse>> cancelBooking(@PathVariable("id") String id) {
        CancelBookingResponse response = bookingService.cancelBooking(id);
        return ResponseEntity.ok(new WebResponse<>("success", "Booking cancelled successfully", response));
    }
}
