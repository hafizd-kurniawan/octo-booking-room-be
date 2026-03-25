package com.octo.booking_room.controller.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.BookingFilter;
import com.octo.booking_room.dto.booking.BookingMonthlyTableResponse;
import com.octo.booking_room.dto.booking.BookingStatsResponse;
import com.octo.booking_room.dto.booking.CancelBookingResponse;
import com.octo.booking_room.dto.booking.CreateBookingRequest;
import com.octo.booking_room.dto.booking.CreateBookingResponse;
import com.octo.booking_room.entity.booking.BookingStatus;
import com.octo.booking_room.exception.BadRequestException;
import com.octo.booking_room.service.booking.BookingExportService;
import com.octo.booking_room.service.booking.BookingService;
import com.octo.booking_room.utils.WebResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@SecurityRequirement(name = "bearerAuth")
public class BookingController {

  private final BookingService bookingService;
  private final BookingExportService bookingExportService;

  public BookingController(BookingService bookingService, BookingExportService bookingExportService) {
    this.bookingService = bookingService;
    this.bookingExportService = bookingExportService;
  }

  @GetMapping("/my")
  public ResponseEntity<WebResponse<List<BookingBasicResponse>>> getMyBookings() {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();
    List<BookingBasicResponse> response = bookingService.getMyBookingsByEmail(email);
    return ResponseEntity.ok(new WebResponse<>("success", "Bookings retrieved", response));
  }

  @GetMapping("/all")
  public ResponseEntity<WebResponse<List<BookingBasicResponse>>> getAllBookings(
      @RequestParam(name = "room_id", required = false) String roomId,
      @RequestParam(name = "room_type_id", required = false) String roomTypeId,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "month", required = false) Integer month,
      @RequestParam(name = "year", required = false) Integer year) {

    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    BookingFilter filter = new BookingFilter();
    filter.setRoomId(roomId);
    filter.setRoomTypeId(roomTypeId);
    filter.setStatus(parseStatus(status));
    filter.setMonth(month);
    filter.setYear(year);

    List<BookingBasicResponse> response = bookingService.getAllBookings(email, filter);
    return ResponseEntity.ok(new WebResponse<>("success", "All bookings retrieved", response));
  }

  @GetMapping("/export/excel")
  public ResponseEntity<byte[]> exportBookingsToExcel(
      @RequestParam(name = "room_id", required = false) String roomId,
      @RequestParam(name = "room_type_id", required = false) String roomTypeId,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "month", required = false) Integer month,
      @RequestParam(name = "year", required = false) Integer year) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    BookingFilter filter = new BookingFilter();
    filter.setRoomId(roomId);
    filter.setRoomTypeId(roomTypeId);
    filter.setStatus(parseStatus(status));
    filter.setMonth(month);
    filter.setYear(year);

    byte[] file = bookingExportService.exportBookingsToExcel(email, filter);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bookings-export.xlsx")
        .contentType(MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(file);
  }

  @GetMapping("/export/pdf")
  public ResponseEntity<byte[]> exportBookingsToPdf(
      @RequestParam(name = "room_id", required = false) String roomId,
      @RequestParam(name = "room_type_id", required = false) String roomTypeId,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "month", required = false) Integer month,
      @RequestParam(name = "year", required = false) Integer year) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    BookingFilter filter = new BookingFilter();
    filter.setRoomId(roomId);
    filter.setRoomTypeId(roomTypeId);
    filter.setStatus(parseStatus(status));
    filter.setMonth(month);
    filter.setYear(year);

    byte[] file = bookingExportService.exportBookingsToPdf(email, filter);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bookings-export.pdf")
        .contentType(MediaType.APPLICATION_PDF)
        .body(file);
  }

  @GetMapping("/stats")
  public ResponseEntity<WebResponse<BookingStatsResponse>> getBookingStats(
      @RequestParam(name = "room_id", required = false) String roomId,
      @RequestParam(name = "room_type_id", required = false) String roomTypeId,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "month", required = false) Integer month,
      @RequestParam(name = "year", required = false) Integer year) {

    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    BookingFilter filter = new BookingFilter();
    filter.setRoomId(roomId);
    filter.setRoomTypeId(roomTypeId);
    filter.setStatus(parseStatus(status));
    filter.setMonth(month);
    filter.setYear(year);

    BookingStatsResponse response = bookingService.getBookingStats(email, filter);
    return ResponseEntity.ok(new WebResponse<>("success", "Booking statistics retrieved", response));
  }

  @GetMapping("/stats/monthly-table")
  public ResponseEntity<WebResponse<BookingMonthlyTableResponse>> getMonthlyBookingTable(
      @RequestParam(name = "room_id", required = false) String roomId,
      @RequestParam(name = "room_type_id", required = false) String roomTypeId,
      @RequestParam(name = "status", required = false) String status,
      @RequestParam(name = "year", required = false) Integer year) {
    String email = SecurityContextHolder.getContext().getAuthentication().getName();

    BookingFilter filter = new BookingFilter();
    filter.setRoomId(roomId);
    filter.setRoomTypeId(roomTypeId);
    filter.setStatus(parseStatus(status));
    filter.setYear(year);

    BookingMonthlyTableResponse response = bookingService.getMonthlyBookingTable(email, filter);
    return ResponseEntity.ok(new WebResponse<>("success", "Monthly booking table retrieved", response));
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

  private BookingStatus parseStatus(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }

    try {
      return BookingStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException("Invalid status. Allowed values: BOOKED, CANCELLED");
    }
  }
}
