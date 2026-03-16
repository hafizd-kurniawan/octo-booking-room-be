package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.BookingSlotBasicResponse;
import com.octo.booking_room.dto.booking.BookingSlotResponse;
import com.octo.booking_room.dto.booking.CancelBookingResponse;
import com.octo.booking_room.dto.booking.CreateBookingRequest;
import com.octo.booking_room.dto.booking.CreateBookingResponse;
import com.octo.booking_room.dto.booking.RoomDto;
import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.booking.BookingSlot;
import com.octo.booking_room.entity.booking.BookingStatus;
import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.entity.room.Room;
import com.octo.booking_room.exception.BadRequestException;
import com.octo.booking_room.exception.ResourceNotFoundException;
import com.octo.booking_room.repository.booking.BookingRepository;
import com.octo.booking_room.repository.customer.CustomerRepository;
import com.octo.booking_room.repository.room.RoomRepository;
import com.octo.booking_room.utils.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

  private final BookingRepository bookingRepository;
  private final CustomerRepository customerRepository;
  private final RoomRepository roomRepository;

  @Override
  public List<BookingBasicResponse> getMyBookingsByEmail(String email) {
    Customer customer = findCustomerByEmail(email);

    List<Booking> bookings = bookingRepository.findByCustomer_CustomerId(customer.getCustomerId());
    log.info("bookings: {}", bookings);
    return bookings.stream()
        .map(booking -> {
          RoomDto roomDto = new RoomDto(
              booking.getRoom().getRoomId(),
              booking.getRoom().getName(),
              booking.getRoom().getFloor());

          List<BookingSlotBasicResponse> slotResponses = booking.getBookingSlots().stream()
              .map(s -> new BookingSlotBasicResponse(s.getSlotId(), s.getStartHour(), s.getEndHour()))
              .collect(Collectors.toList());

          return new BookingBasicResponse(
              booking.getBookingId(),
              roomDto,
              booking.getDate(),
              booking.getStatus().name().toLowerCase(),
              slotResponses);
        })
        .collect(Collectors.toList());
  }

  @Override
  public BookingDetailResponse getBookingDetail(String email, String bookingId) {
    Booking booking = findAuthorizedBooking(email, bookingId);

    List<BookingSlotBasicResponse> slotResponses = booking.getBookingSlots().stream()
        .map(s -> new BookingSlotBasicResponse(s.getSlotId(), s.getStartHour(), s.getEndHour()))
        .collect(Collectors.toList());

    return new BookingDetailResponse(
        booking.getBookingId(),
        booking.getRoom().getName(),
        booking.getDate(),
        booking.getStatus().name().toLowerCase(),
        slotResponses);
  }

  @Override
  public CancelBookingResponse cancelBooking(String email, String bookingId) {
    Booking booking = findAuthorizedBooking(email, bookingId);

    if (booking.getStatus() == BookingStatus.CANCELLED) {
      throw new BadRequestException("Booking is already cancelled");
    }

    booking.setStatus(BookingStatus.CANCELLED);
    bookingRepository.save(booking);

    return new CancelBookingResponse(
        booking.getBookingId(),
        "cancelled");
  }

  @Override
  public CreateBookingResponse createBooking(String email, CreateBookingRequest request) {
    validateSlots(request.getSlots());

    // Cross-booking overlap check
    validateCrossBooking(request.getRoomId(), request.getDate(), request.getSlots());

    Customer customer = findCustomerByEmail(email);
    Room room = roomRepository.findById(request.getRoomId())
        .orElseThrow(() -> new ResourceNotFoundException("Room " + request.getRoomId() + " not found"));

    String bookingId = IdGenerator.generateId("book-");
    Booking booking = new Booking();
    booking.setBookingId(bookingId);
    booking.setCustomer(customer);
    booking.setRoom(room);
    booking.setDate(request.getDate());
    booking.setStatus(BookingStatus.BOOKED);

    List<BookingSlot> slots = request.getSlots().stream()
        .map(s -> {
          BookingSlot slot = new BookingSlot();
          slot.setSlotId(IdGenerator.generateId("slot-"));
          slot.setStartHour(s.getStartHour());
          slot.setEndHour(s.getEndHour());
          slot.setBooking(booking);
          return slot;
        })
        .collect(Collectors.toList());

    booking.setBookingSlots(slots);
    bookingRepository.save(booking);

    List<BookingSlotResponse> slotResponses = slots.stream()
        .map(s -> new BookingSlotResponse(s.getSlotId(), s.getStartHour(), s.getEndHour()))
        .collect(Collectors.toList());

    return new CreateBookingResponse(
        bookingId,
        room.getRoomId(),
        customer.getCustomerId(),
        request.getDate(),
        "booked",
        slotResponses);
  }

  private Customer findCustomerByEmail(String email) {
    return customerRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("Customer with email " + email + " not found"));
  }

  private Booking findAuthorizedBooking(String email, String bookingId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new ResourceNotFoundException("Booking with ID " + bookingId + " not found"));

    Customer requester = findCustomerByEmail(email);

    boolean isOwner = booking.getCustomer().getEmail().equalsIgnoreCase(email);
    boolean isAdmin = Boolean.TRUE.equals(requester.getIsAdmin());

    if (!isOwner && !isAdmin) {
      throw new AccessDeniedException("You are not allowed to access this booking");
    }

    return booking;
  }

  private void validateSlots(List<CreateBookingRequest.SlotRequest> slots) {
    if (slots == null || slots.isEmpty()) {
      throw new BadRequestException("At least one slot is required");
    }

    List<CreateBookingRequest.SlotRequest> sortedSlots = new ArrayList<>(slots);
    sortedSlots.sort(Comparator.comparing(CreateBookingRequest.SlotRequest::getStartHour));

    for (CreateBookingRequest.SlotRequest slot : sortedSlots) {
      if (!slot.getStartHour().isBefore(slot.getEndHour())) {
        throw new BadRequestException("start_hour must be before end_hour");
      }
    }

    for (int i = 1; i < sortedSlots.size(); i++) {
      CreateBookingRequest.SlotRequest previous = sortedSlots.get(i - 1);
      CreateBookingRequest.SlotRequest current = sortedSlots.get(i);

      if (previous.getEndHour().isAfter(current.getStartHour())) {
        throw new BadRequestException("Slots in request overlap each other");
      }
    }
  }

  private void validateCrossBooking(String roomId, LocalDate date,
      List<CreateBookingRequest.SlotRequest> requestedSlots) {
    List<Booking> existingBookings = bookingRepository.findByRoom_RoomIdAndDate(roomId, date);

    List<BookingSlot> existingSlots = existingBookings.stream()
        .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
        .flatMap(b -> b.getBookingSlots().stream())
        .collect(Collectors.toList());

    for (CreateBookingRequest.SlotRequest newSlot : requestedSlots) {
      for (BookingSlot existingSlot : existingSlots) {
        if (newSlot.getStartHour().isBefore(existingSlot.getEndHour()) &&
            newSlot.getEndHour().isAfter(existingSlot.getStartHour())) {

          log.warn("Slot overlap detected: requested {}-{}, existing {}-{}",
              newSlot.getStartHour(), newSlot.getEndHour(),
              existingSlot.getStartHour(), existingSlot.getEndHour());

          throw new BadRequestException("Slot " + newSlot.getStartHour() + " - " + newSlot.getEndHour() +
              " overlaps with an existing booking");
        }
      }
    }
  }
}
