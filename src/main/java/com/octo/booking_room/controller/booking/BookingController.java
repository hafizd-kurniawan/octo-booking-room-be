package com.octo.booking_room.controller.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.CancelBookingResponse;
import com.octo.booking_room.dto.booking.CreateBookingRequest;
import com.octo.booking_room.dto.booking.CreateBookingResponse;
import com.octo.booking_room.service.booking.BookingService;
import com.octo.booking_room.utils.WebResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @GetMapping("/my")
  public ResponseEntity<WebResponse<List<BookingBasicResponse>>> getMyBookings() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    List<BookingBasicResponse> response = bookingService.getMyBookingsByEmail(email);
    return ResponseEntity.ok(new WebResponse<>("success", "Bookings retrieved", response));
  }

  @GetMapping("/{id}")
  public ResponseEntity<WebResponse<BookingDetailResponse>> getBookingDetail(@PathVariable("id") String id) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    BookingDetailResponse response = bookingService.getBookingDetail(email, id);
    return ResponseEntity.ok(new WebResponse<>("success", "Booking detail retrieved", response));
  }

  @PatchMapping("/{id}/cancel")
  public ResponseEntity<WebResponse<CancelBookingResponse>> cancelBooking(@PathVariable("id") String id) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    CancelBookingResponse response = bookingService.cancelBooking(email, id);
    return ResponseEntity.ok(new WebResponse<>("success", "Booking cancelled successfully", response));
  }

  @PostMapping
  public ResponseEntity<WebResponse<CreateBookingResponse>> createBooking(
      @Valid @RequestBody CreateBookingRequest request) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    CreateBookingResponse response = bookingService.createBooking(email, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new WebResponse<>("success", "Booking created successfully", response));
  }
}
