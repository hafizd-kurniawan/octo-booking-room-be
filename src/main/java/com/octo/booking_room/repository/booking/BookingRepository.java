package com.octo.booking_room.repository.booking;

import com.octo.booking_room.entity.booking.Booking;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

  List<Booking> findByCustomer_CustomerId(String customerId);

  List<Booking> findByRoom_RoomIdAndDate(String roomId, LocalDate date);

  boolean existsByRoom_RoomId(String roomId);
}
