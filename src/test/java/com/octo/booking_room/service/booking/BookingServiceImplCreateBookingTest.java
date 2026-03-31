package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.CreateBookingRequest;
import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.entity.room.Room;
import com.octo.booking_room.entity.room.RoomType;
import com.octo.booking_room.exception.BadRequestException;
import com.octo.booking_room.repository.booking.BookingRepository;
import com.octo.booking_room.repository.customer.CustomerRepository;
import com.octo.booking_room.repository.room.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplCreateBookingTest {

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private CustomerRepository customerRepository;

  @Mock
  private RoomRepository roomRepository;

  private BookingServiceImpl bookingService;

  @BeforeEach
  void setUp() {
    bookingService = new BookingServiceImpl(bookingRepository, customerRepository, roomRepository);
  }

  @Test
  void shouldRejectSlotThatFallsOutsideBookingDate() {
    CreateBookingRequest request = new CreateBookingRequest();
    request.setRoomId("room-1");
    request.setDate(LocalDate.of(2026, 3, 30));
    request.setSlots(List.of(slot(
        LocalDateTime.of(2026, 3, 31, 9, 0),
        LocalDateTime.of(2026, 3, 31, 10, 0))));

    assertThatThrownBy(() -> bookingService.createBooking("user@example.com", request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("All slot times must stay within the booking date");
  }

  @Test
  void shouldRejectMissingBookingDateBeforeComparingSlotDates() {
    CreateBookingRequest request = new CreateBookingRequest();
    request.setRoomId("room-1");
    request.setSlots(List.of(slot(
        LocalDateTime.of(2026, 3, 30, 9, 0),
        LocalDateTime.of(2026, 3, 30, 10, 0))));

    assertThatThrownBy(() -> bookingService.createBooking("user@example.com", request))
        .isInstanceOf(BadRequestException.class)
        .hasMessage("date is required");
  }

  @Test
  void shouldLockRoomBeforeSavingBooking() {
    Room room = room();
    when(customerRepository.findByEmail("user@example.com")).thenReturn(Optional.of(customer()));
    when(roomRepository.findByIdForUpdate("room-1")).thenReturn(Optional.of(room));
    when(bookingRepository.findByRoom_RoomIdAndDate("room-1", LocalDate.of(2026, 3, 30))).thenReturn(List.of());

    CreateBookingRequest request = new CreateBookingRequest();
    request.setRoomId("room-1");
    request.setDate(LocalDate.of(2026, 3, 30));
    request.setSlots(List.of(slot(
        LocalDateTime.of(2026, 3, 30, 9, 0),
        LocalDateTime.of(2026, 3, 30, 10, 0))));

    bookingService.createBooking("user@example.com", request);

    verify(roomRepository).findByIdForUpdate("room-1");

    ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
    verify(bookingRepository).save(bookingCaptor.capture());

    Booking savedBooking = bookingCaptor.getValue();
    assertThat(savedBooking.getRoom().getRoomId()).isEqualTo("room-1");
    assertThat(savedBooking.getDate()).isEqualTo(LocalDate.of(2026, 3, 30));
    assertThat(savedBooking.getBookingSlots()).hasSize(1);
  }

  private Customer customer() {
    Customer customer = new Customer();
    customer.setCustomerId("cust-1");
    customer.setEmail("user@example.com");
    customer.setIsAdmin(false);
    return customer;
  }

  private Room room() {
    RoomType roomType = new RoomType();
    roomType.setTypeId("type-1");
    roomType.setName("Meeting Room");

    Room room = new Room();
    room.setRoomId("room-1");
    room.setName("Meeting Room A");
    room.setFloor(3);
    room.setRoomType(roomType);
    return room;
  }

  private CreateBookingRequest.SlotRequest slot(LocalDateTime startHour, LocalDateTime endHour) {
    CreateBookingRequest.SlotRequest slot = new CreateBookingRequest.SlotRequest();
    slot.setStartHour(startHour);
    slot.setEndHour(endHour);
    return slot;
  }
}
