package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.CancelBookingResponse;
import com.octo.booking_room.dto.booking.CreateBookingRequest;
import com.octo.booking_room.dto.booking.RoomDto;
import com.octo.booking_room.repository.booking.BookingRepository;
import com.octo.booking_room.repository.customer.CustomerRepository;
import com.octo.booking_room.repository.room.RoomRepository;

import lombok.RequiredArgsConstructor;

import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.booking.BookingSlot;
import com.octo.booking_room.entity.booking.BookingStatus;
import com.octo.booking_room.entity.booking.CreateBookingResponse;
import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.entity.room.Room;
import com.octo.booking_room.exception.ResourceNotFoundException;
import com.octo.booking_room.utils.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;

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

    @Override
    public CreateBookingResponse createBooking(String email, CreateBookingRequest request) {
        Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Customer with email " + email + " not found"));

        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new ResourceNotFoundException("Room " + request.getRoomId() + " not found"));

        String bookingId = IdGenerator.generateId("book-");

        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setDate(request.getDate());
        booking.setStatus(BookingStatus.BOOKED);

        List<BookingSlot> slots = request.getSlots().stream().map(s -> {
            BookingSlot slot = new BookingSlot();
            slot.setSlotId(IdGenerator.generateId("slot-"));
            slot.setStartHour(s.getStartHour());
            slot.setEndHour(s.getEndHour());
            slot.setBooking(booking);
            return slot;
        }).collect(Collectors.toList());

        booking.setBookingSlots(slots);
        bookingRepository.save(booking);

        List<BookingSlot> slotResponses = slots.stream()
            .map(s -> new BookingSlot(s.getStartHour(), s.getEndHour()))
            .collect(Collectors.toList());

        return new CreateBookingResponse(bookingId, room.getRoomId(), customer.getCustomerId(),
            request.getDate(), "booked", slotResponses);
    }
}
