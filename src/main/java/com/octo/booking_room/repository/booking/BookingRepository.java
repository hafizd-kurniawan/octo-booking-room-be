package com.octo.booking_room.repository.booking;

import com.octo.booking_room.entity.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    
    List<Booking> findByCustomer_CustomerId(String customerId);
    List<Booking> findByRoom_RoomIdAndDate(String roomId, LocalDate date);
}
