package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.BookingFilter;

public interface BookingExportService {
  byte[] exportBookingsToExcel(String requesterEmail, BookingFilter filter);

  byte[] exportBookingsToPdf(String requesterEmail, BookingFilter filter);
}
