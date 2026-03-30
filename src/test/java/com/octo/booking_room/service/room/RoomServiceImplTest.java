package com.octo.booking_room.service.room;

import com.octo.booking_room.dto.room.BookedSlotsResponse;
import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.booking.BookingSlot;
import com.octo.booking_room.entity.booking.BookingStatus;
import com.octo.booking_room.entity.room.Room;
import com.octo.booking_room.entity.room.RoomType;
import com.octo.booking_room.repository.booking.BookingRepository;
import com.octo.booking_room.repository.room.RoomRepository;
import com.octo.booking_room.repository.room.RoomTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

  @Mock
  private RoomRepository roomRepository;

  @Mock
  private RoomTypeRepository roomTypeRepository;

  @Mock
  private BookingRepository bookingRepository;

  private RoomServiceImpl roomService;

  @BeforeEach
  void setUp() {
    roomService = new RoomServiceImpl(roomRepository, roomTypeRepository, bookingRepository);
  }

  @Test
  void shouldExcludeCancelledBookingsFromBookedSlots() {
    when(roomRepository.findById("room-1")).thenReturn(Optional.of(room()));
    when(bookingRepository.findByRoom_RoomIdAndDate("room-1", LocalDate.of(2026, 3, 30)))
        .thenReturn(List.of(
            booking("book-1", BookingStatus.BOOKED),
            booking("book-2", BookingStatus.CANCELLED)
        ));

    BookedSlotsResponse response = roomService.getBookedSlots("room-1", LocalDate.of(2026, 3, 30));

    assertThat(response.getBookedSlots()).hasSize(1);
    assertThat(response.getBookedSlots().get(0).getBookingId()).isEqualTo("book-1");
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

  private Booking booking(String bookingId, BookingStatus status) {
    BookingSlot slot = new BookingSlot();
    slot.setSlotId("slot-" + bookingId);
    slot.setStartHour(LocalDateTime.of(2026, 3, 30, 9, 0));
    slot.setEndHour(LocalDateTime.of(2026, 3, 30, 10, 0));

    Booking booking = new Booking();
    booking.setBookingId(bookingId);
    booking.setStatus(status);
    booking.setBookingSlots(List.of(slot));
    return booking;
  }
}
