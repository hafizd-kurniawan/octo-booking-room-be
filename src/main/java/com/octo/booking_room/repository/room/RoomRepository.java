package com.octo.booking_room.repository.room;

import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    List<Room> findAll();  
}
