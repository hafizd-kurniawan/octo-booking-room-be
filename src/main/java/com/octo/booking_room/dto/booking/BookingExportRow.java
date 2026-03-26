package com.octo.booking_room.dto.booking;

public record BookingExportRow(
    String bookingId,
    String date,
    String status,
    String customerId,
    String customerName,
    String customerEmail,
    String roomId,
    String roomName,
    String roomTypeId,
    String roomTypeName,
    Integer floor,
    String slots
) {
}
