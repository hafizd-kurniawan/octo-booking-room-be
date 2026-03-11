package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.CancelBookingResponse;

import java.util.List;

public interface BookingService {

    List<BookingBasicResponse> getMyBookings(String customerId);

    BookingDetailResponse getBookingDetail(String bookingId);

    CancelBookingResponse cancelBooking(String bookingId);

}
