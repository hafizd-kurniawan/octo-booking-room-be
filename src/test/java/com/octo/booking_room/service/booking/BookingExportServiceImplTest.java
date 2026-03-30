package com.octo.booking_room.service.booking;

import com.octo.booking_room.dto.booking.BookingFilter;
import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.booking.BookingSlot;
import com.octo.booking_room.entity.booking.BookingStatus;
import com.octo.booking_room.entity.customer.Customer;
import com.octo.booking_room.entity.room.Room;
import com.octo.booking_room.entity.room.RoomType;
import com.octo.booking_room.repository.booking.BookingRepository;
import com.octo.booking_room.repository.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingExportServiceImplTest {

  @Mock
  private BookingRepository bookingRepository;

  @Mock
  private CustomerRepository customerRepository;

  private BookingExportServiceImpl bookingExportService;

  @BeforeEach
  void setUp() {
    bookingExportService = new BookingExportServiceImpl(bookingRepository, customerRepository);
  }

  @Test
  void shouldExportExcelForAdmin() {
    when(customerRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminCustomer()));
    when(bookingRepository.findAllForExport()).thenReturn(List.of(sampleBooking()));

    byte[] result = bookingExportService.exportBookingsToExcel("admin@example.com", new BookingFilter());

    assertThat(result).isNotEmpty();
  }

  @Test
  void shouldExportPdfForAdmin() {
    when(customerRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminCustomer()));
    when(bookingRepository.findAllForExport()).thenReturn(List.of(sampleBooking()));

    byte[] result = bookingExportService.exportBookingsToPdf("admin@example.com", new BookingFilter());

    assertThat(result).isNotEmpty();
    assertThat(new String(result, 0, 4)).isEqualTo("%PDF");
  }

  @Test
  void shouldRejectNonAdminExport() {
    Customer nonAdmin = adminCustomer();
    nonAdmin.setIsAdmin(false);

    when(customerRepository.findByEmail("user@example.com")).thenReturn(Optional.of(nonAdmin));

    assertThatThrownBy(() -> bookingExportService.exportBookingsToExcel("user@example.com", new BookingFilter()))
        .isInstanceOf(AccessDeniedException.class);
  }

  private Customer adminCustomer() {
    Customer customer = new Customer();
    customer.setCustomerId("cst-admin");
    customer.setName("Admin");
    customer.setEmail("admin@example.com");
    customer.setIsAdmin(true);
    return customer;
  }

  private Booking sampleBooking() {
    Customer customer = new Customer();
    customer.setCustomerId("cst-1");
    customer.setName("Hafiz");
    customer.setEmail("hafiz@example.com");

    RoomType roomType = new RoomType();
    roomType.setTypeId("rt-1");
    roomType.setName("Meeting Room");

    Room room = new Room();
    room.setRoomId("rm-1");
    room.setName("Meeting Room A");
    room.setFloor(3);
    room.setRoomType(roomType);

    BookingSlot slot = new BookingSlot();
    slot.setSlotId("slot-1");
    slot.setStartHour(LocalDateTime.of(2026, 3, 25, 9, 0));
    slot.setEndHour(LocalDateTime.of(2026, 3, 25, 10, 0));

    Booking booking = new Booking();
    booking.setBookingId("book-1");
    booking.setCustomer(customer);
    booking.setRoom(room);
    booking.setDate(LocalDate.of(2026, 3, 25));
    booking.setStatus(BookingStatus.BOOKED);
    booking.setBookingSlots(List.of(slot));
    return booking;
  }
}
