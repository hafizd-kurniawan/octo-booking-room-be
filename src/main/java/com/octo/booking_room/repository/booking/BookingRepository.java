package com.octo.booking_room.repository.booking;

import com.octo.booking_room.entity.booking.Booking;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

  List<Booking> findByCustomer_CustomerId(String customerId);

  List<Booking> findByRoom_RoomIdAndDate(String roomId, LocalDate date);

  boolean existsByRoom_RoomId(String roomId);

  @EntityGraph(attributePaths = { "customer", "room", "room.roomType", "bookingSlots" })
  @Query("select distinct b from Booking b")
  List<Booking> findAllForExport();
}
