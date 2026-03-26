package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.BookingFilter;
import com.octo.booking_room.dto.booking.BookingMonthlyTableResponse;
import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.booking.BookingStatus;
import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.entity.room.Room;
import com.octo.booking_room.entity.room.RoomType;
import com.octo.booking_room.repository.booking.BookingRepository;
import com.octo.booking_room.repository.customer.CustomerRepository;
import com.octo.booking_room.repository.room.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplMonthlyTableTest {

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
  void shouldBuildMonthlyTableGroupedByMonthRoomAndRoomType() {
    when(customerRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminCustomer()));
    when(bookingRepository.findAll()).thenReturn(List.of(
        booking("book-1", "rm-1", "Meeting Room A", "rt-1", "Meeting Room", LocalDate.of(2026, 3, 10), BookingStatus.BOOKED),
        booking("book-2", "rm-1", "Meeting Room A", "rt-1", "Meeting Room", LocalDate.of(2026, 3, 11), BookingStatus.BOOKED),
        booking("book-3", "rm-2", "Meeting Room B", "rt-1", "Meeting Room", LocalDate.of(2026, 4, 2), BookingStatus.CANCELLED)
    ));

    BookingFilter filter = new BookingFilter();
    BookingMonthlyTableResponse response = bookingService.getMonthlyBookingTable("admin@example.com", filter);

    assertThat(response.getMonths()).hasSize(2);
    assertThat(response.getMonths().get(0).getTotalBookings()).isEqualTo(2);
    assertThat(response.getMonths().get(0).getByRoom().get(0).getRoomId()).isEqualTo("rm-1");
    assertThat(response.getMonths().get(0).getByRoom().get(0).getCount()).isEqualTo(2);
    assertThat(response.getMonths().get(1).getByRoomType().get(0).getTypeId()).isEqualTo("rt-1");
  }

  @Test
  void shouldFilterMonthlyTableByStatus() {
    when(customerRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminCustomer()));
    when(bookingRepository.findAll()).thenReturn(List.of(
        booking("book-1", "rm-1", "Meeting Room A", "rt-1", "Meeting Room", LocalDate.of(2026, 3, 10), BookingStatus.BOOKED),
        booking("book-2", "rm-1", "Meeting Room A", "rt-1", "Meeting Room", LocalDate.of(2026, 3, 11), BookingStatus.CANCELLED)
    ));

    BookingFilter filter = new BookingFilter();
    filter.setStatus(BookingStatus.CANCELLED);

    BookingMonthlyTableResponse response = bookingService.getMonthlyBookingTable("admin@example.com", filter);

    assertThat(response.getMonths()).hasSize(1);
    assertThat(response.getMonths().get(0).getTotalBookings()).isEqualTo(1);
  }

  private Customer adminCustomer() {
    Customer customer = new Customer();
    customer.setCustomerId("cst-admin");
    customer.setEmail("admin@example.com");
    customer.setIsAdmin(true);
    return customer;
  }

  private Booking booking(String bookingId, String roomId, String roomName, String roomTypeId, String roomTypeName,
                          LocalDate date, BookingStatus status) {
    RoomType roomType = new RoomType();
    roomType.setTypeId(roomTypeId);
    roomType.setName(roomTypeName);

    Room room = new Room();
    room.setRoomId(roomId);
    room.setName(roomName);
    room.setRoomType(roomType);

    Customer customer = new Customer();
    customer.setCustomerId("cst-1");
    customer.setEmail("user@example.com");

    Booking booking = new Booking();
    booking.setBookingId(bookingId);
    booking.setDate(date);
    booking.setStatus(status);
    booking.setRoom(room);
    booking.setCustomer(customer);
    return booking;
  }
}
