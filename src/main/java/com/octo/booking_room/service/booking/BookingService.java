package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.CancelBookingResponse;
import com.octo.booking_room.dto.booking.CreateBookingRequest;
import com.octo.booking_room.dto.booking.CreateBookingResponse;

import java.util.List;

public interface BookingService {
  List<BookingBasicResponse> getMyBookingsByEmail(String email);

  List<BookingBasicResponse> getAllBookings(String requesterEmail);

  BookingDetailResponse getBookingDetail(String email, String bookingId);

  CancelBookingResponse cancelBooking(String email, String bookingId);

  CreateBookingResponse createBooking(String email, CreateBookingRequest request);
}