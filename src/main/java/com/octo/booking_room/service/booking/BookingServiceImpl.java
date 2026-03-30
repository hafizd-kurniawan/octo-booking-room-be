package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.BookingBasicResponse;
import com.octo.booking_room.dto.booking.BookingDetailResponse;
import com.octo.booking_room.dto.booking.BookingMonthlyTableResponse;
import com.octo.booking_room.dto.booking.BookingStatsResponse;
import com.octo.booking_room.dto.booking.BookingFilter;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
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
    log.info("getMyBookings for {}: {} booking(s) found", email, bookings.size());
    return bookings.stream()
        .map(this::toBookingBasicResponse)
        .collect(Collectors.toList());
  }

  @Override
  public List<BookingBasicResponse> getAllBookings(String requesterEmail, BookingFilter filter) {
    requireAdmin(requesterEmail);
 
    List<Booking> bookings = bookingRepository.findAll();
    // log.info("getAllBookings by admin {}: {} total before filter", requesterEmail, bookings.size());
 
    return bookings.stream()
        .filter(b -> matchesFilter(b, filter))
        .map(this::toBookingBasicResponse)
        .collect(Collectors.toList());
  }

  @Override
  public BookingStatsResponse getBookingStats(String requesterEmail, BookingFilter filter) {
    requireAdmin(requesterEmail);
 
    List<Booking> bookings = bookingRepository.findAll().stream()
        .filter(b -> matchesFilter(b, filter))
        .collect(Collectors.toList());
 
    int total = bookings.size();
 
    // By room type 
    Map<String, List<Booking>> byRoomType = bookings.stream()
        .collect(Collectors.groupingBy(b -> b.getRoom().getRoomType().getTypeId()));
 
    List<BookingStatsResponse.RoomTypeStat> roomTypeStats = byRoomType.entrySet().stream()
        .map(e -> {
          var roomType = e.getValue().get(0).getRoom().getRoomType();
          return new BookingStatsResponse.RoomTypeStat(
              roomType.getTypeId(),
              roomType.getName(),
              e.getValue().size());
        })
        .sorted(Comparator.comparingInt(BookingStatsResponse.RoomTypeStat::getCount).reversed())
        .collect(Collectors.toList());

    // By room
    Map<String, List<Booking>> byRoom = bookings.stream()
        .collect(Collectors.groupingBy(b -> b.getRoom().getRoomId()));

    List<BookingStatsResponse.RoomStat> roomStats = byRoom.entrySet().stream()
        .map(e -> {
          var room = e.getValue().get(0).getRoom();
          var roomType = room.getRoomType();
          return new BookingStatsResponse.RoomStat(
              room.getRoomId(),
              room.getName(),
              room.getFloor(),
              roomType.getTypeId(),
              roomType.getName(),
              e.getValue().size());
        })
        .sorted(Comparator.comparingInt(BookingStatsResponse.RoomStat::getCount).reversed()
            .thenComparing(BookingStatsResponse.RoomStat::getRoomName))
        .collect(Collectors.toList());
 
    // By year+month
    Map<String, List<Booking>> byMonth = bookings.stream()
        .collect(Collectors.groupingBy(b -> {
          LocalDate d = b.getDate();
          return d.getYear() + "-" + String.format("%02d", d.getMonthValue());
        }));
 
    List<BookingStatsResponse.MonthStat> monthStats = byMonth.entrySet().stream()
        .map(e -> {
          LocalDate sample = e.getValue().get(0).getDate();
          String label = sample.getMonth()
              .getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + sample.getYear();
          return new BookingStatsResponse.MonthStat(
              sample.getYear(),
              sample.getMonthValue(),
              label,
              e.getValue().size());
        })
        .sorted(Comparator.comparingInt(BookingStatsResponse.MonthStat::getYear)
            .thenComparingInt(BookingStatsResponse.MonthStat::getMonth))
        .collect(Collectors.toList());
 
    return new BookingStatsResponse(total, roomTypeStats, roomStats, monthStats);
  }

  @Override
  public BookingMonthlyTableResponse getMonthlyBookingTable(String requesterEmail, BookingFilter filter) {
    requireAdmin(requesterEmail);

    Map<String, List<Booking>> byMonth = bookingRepository.findAll().stream()
        .filter(b -> matchesFilter(b, filter))
        .collect(Collectors.groupingBy(b -> b.getDate().getYear() + "-" + String.format("%02d", b.getDate().getMonthValue())));

    List<BookingMonthlyTableResponse.MonthlyBookingSummary> monthSummaries = byMonth.values().stream()
        .map(bookings -> {
          LocalDate sample = bookings.get(0).getDate();
          String label = sample.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + sample.getYear();

          List<BookingMonthlyTableResponse.RoomBookingCount> byRoom = bookings.stream()
              .collect(Collectors.groupingBy(b -> b.getRoom().getRoomId()))
              .entrySet().stream()
              .map(entry -> {
                Booking sampleBooking = entry.getValue().get(0);
                return new BookingMonthlyTableResponse.RoomBookingCount(
                    sampleBooking.getRoom().getRoomId(),
                    sampleBooking.getRoom().getName(),
                    entry.getValue().size());
              })
              .sorted(Comparator.comparingInt(BookingMonthlyTableResponse.RoomBookingCount::getCount).reversed()
                  .thenComparing(BookingMonthlyTableResponse.RoomBookingCount::getRoomName))
              .collect(Collectors.toList());

          List<BookingMonthlyTableResponse.RoomTypeBookingCount> byRoomType = bookings.stream()
              .collect(Collectors.groupingBy(b -> b.getRoom().getRoomType().getTypeId()))
              .entrySet().stream()
              .map(entry -> {
                Booking sampleBooking = entry.getValue().get(0);
                return new BookingMonthlyTableResponse.RoomTypeBookingCount(
                    sampleBooking.getRoom().getRoomType().getTypeId(),
                    sampleBooking.getRoom().getRoomType().getName(),
                    entry.getValue().size());
              })
              .sorted(Comparator.comparingInt(BookingMonthlyTableResponse.RoomTypeBookingCount::getCount).reversed()
                  .thenComparing(BookingMonthlyTableResponse.RoomTypeBookingCount::getTypeName))
              .collect(Collectors.toList());

          return new BookingMonthlyTableResponse.MonthlyBookingSummary(
              sample.getYear(),
              sample.getMonthValue(),
              label,
              bookings.size(),
              byRoom,
              byRoomType);
        })
        .sorted(Comparator.comparingInt(BookingMonthlyTableResponse.MonthlyBookingSummary::getYear)
            .thenComparingInt(BookingMonthlyTableResponse.MonthlyBookingSummary::getMonth))
        .collect(Collectors.toList());

    return new BookingMonthlyTableResponse(monthSummaries);
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
  @Transactional
  public CreateBookingResponse createBooking(String requesterEmail, CreateBookingRequest request) {
    validateSlots(request.getDate(), request.getSlots());

    Customer requester = findCustomerByEmail(requesterEmail);
    boolean isAdmin = Boolean.TRUE.equals(requester.getIsAdmin());
    
    Customer bookingOwner;
    if (request.getCustomerEmail() != null && !request.getCustomerEmail().isBlank()) {
      if (!isAdmin) {
        throw new AccessDeniedException("Only admins can create bookings on behalf of other customers");
      }
      bookingOwner = findCustomerByEmail(request.getCustomerEmail());
    } else {
      bookingOwner = requester;
    }

    Room room = roomRepository.findByIdForUpdate(request.getRoomId())
        .orElseThrow(() -> new ResourceNotFoundException("Room " + request.getRoomId() + " not found"));

    validateCrossBooking(room.getRoomId(), request.getDate(), request.getSlots());

    String bookingId = IdGenerator.generateId("book-");
    Booking booking = new Booking();
    booking.setBookingId(bookingId);
    booking.setCustomer(bookingOwner);
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
        bookingOwner.getCustomerId(),
        request.getDate(),
        "booked",
        slotResponses);
  }

  private void requireAdmin(String email) {
    Customer requester = findCustomerByEmail(email);
    if (!Boolean.TRUE.equals(requester.getIsAdmin())) {
      throw new AccessDeniedException("Only admins can perform this action");
    }
  }

  private boolean matchesFilter(Booking booking, BookingFilter filter) {
    if (filter == null) return true;
 
    if (filter.getRoomId() != null && !filter.getRoomId().isBlank()) {
      if (!booking.getRoom().getRoomId().equals(filter.getRoomId())) return false;
    }
 
    if (filter.getRoomTypeId() != null && !filter.getRoomTypeId().isBlank()) {
      if (!booking.getRoom().getRoomType().getTypeId().equals(filter.getRoomTypeId())) return false;
    }
 
    if (filter.getYear() != null) {
      if (booking.getDate().getYear() != filter.getYear()) return false;
    }
 
    if (filter.getMonth() != null) {
      if (booking.getDate().getMonthValue() != filter.getMonth()) return false;
    }

    if (filter.getStatus() != null && booking.getStatus() != filter.getStatus()) {
      return false;
    }
 
    return true;
  }

  private BookingBasicResponse toBookingBasicResponse(Booking booking) {
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
        booking.getCustomer().getEmail(),
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

  private void validateSlots(LocalDate bookingDate, List<CreateBookingRequest.SlotRequest> slots) {
    if (slots == null || slots.isEmpty()) {
      throw new BadRequestException("At least one slot is required");
    }

    List<CreateBookingRequest.SlotRequest> sortedSlots = new ArrayList<>(slots);
    sortedSlots.sort(Comparator.comparing(CreateBookingRequest.SlotRequest::getStartHour));

    for (CreateBookingRequest.SlotRequest slot : sortedSlots) {
      if (!slot.getStartHour().isBefore(slot.getEndHour())) {
        throw new BadRequestException("start_hour must be before end_hour");
      }

      LocalDate startDate = slot.getStartHour().toLocalDate();
      LocalDate endDate = slot.getEndHour().toLocalDate();
      if (!bookingDate.equals(startDate) || !bookingDate.equals(endDate)) {
        throw new BadRequestException("All slot times must stay within the booking date");
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
