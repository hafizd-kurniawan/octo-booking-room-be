package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.CancelBookingResponse;
import com.octo.booking_room.dto.booking.RoomDto;
import com.octo.booking_room.repository.booking.BookingRepository;
import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.booking.BookingStatus;
import com.octo.booking_room.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<BookingBasicResponse> getMyBookings(String customerId) {
        List<Booking> bookings = bookingRepository.findByCustomer_CustomerId(customerId);

        return bookings.stream().map(booking -> {
            RoomDto roomDto = new RoomDto(
                booking.getRoom().getRoomId(),
                booking.getRoom().getName(),
                booking.getRoom().getFloor()
            );

            return new BookingBasicResponse(
                booking.getBookingId(),
                roomDto,
                booking.getDate(),
                booking.getStatus().name().toLowerCase()
            );
        }).collect(Collectors.toList());
    }

    @Override
    public BookingDetailResponse getBookingDetail(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking detail retrieved failed: Booking with ID " + bookingId + " not found"));

        return new BookingDetailResponse(
            booking.getBookingId(),
            booking.getRoom().getName(),
            booking.getDate(),
            booking.getStatus().name().toLowerCase()
        );
    }

    @Override
    public CancelBookingResponse cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking with ID " + bookingId + " not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return new CancelBookingResponse(
            booking.getBookingId(),
            "cancelled" // Enforcing lower case string per postman contract
        );
    }
}
