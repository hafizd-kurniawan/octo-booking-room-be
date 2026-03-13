package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.CancelBookingResponse;
import com.octo.booking_room.dto.booking.CreateBookingRequest;
import com.octo.booking_room.entity.booking.CreateBookingResponse;

import java.util.List;

public interface BookingService {

    List<BookingBasicResponse> getMyBookings(String customerId);

    BookingDetailResponse getBookingDetail(String bookingId);

    CancelBookingResponse cancelBooking(String bookingId);

    CreateBookingResponse createBooking(String email, CreateBookingRequest request);
}
