package com.octo.booking_room.service.room;

import com.octo.booking_room.dto.room.BookedSlotDto;
import com.octo.booking_room.dto.room.BookedSlotsResponse;
import com.octo.booking_room.dto.room.RoomResponse;
import com.octo.booking_room.dto.room.RoomTypeDto;
import com.octo.booking_room.entity.room.Room;
import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.booking.BookingSlot;
import com.octo.booking_room.repository.booking.BookingRepository;
import com.octo.booking_room.repository.room.RoomRepository;
import com.octo.booking_room.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
  
    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream().map(room -> new RoomResponse(
            room.getRoomId(),
            room.getName(),
            room.getFloor(),
            new RoomTypeDto(
                room.getRoomType().getTypeId(),
                room.getRoomType().getName(),
                room.getRoomType().getCapacity()
            )
        )).collect(Collectors.toList());
    }

    @Override
    public BookedSlotsResponse getBookedSlots(String roomId, LocalDate date) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room with ID " + roomId + " not found"));

        List<Booking> bookings = bookingRepository.findByRoom_RoomIdAndDate(roomId, date);

        List<BookedSlotDto> slots = bookings.stream()
            .flatMap(booking -> booking.getBookingSlots().stream()
                .map(slot -> new BookedSlotDto(
                    booking.getBookingId(),
                    slot.getStartHour(),
                    slot.getEndHour(),
                    booking.getStatus().name().toLowerCase()
                ))
            ).collect(Collectors.toList());

        return new BookedSlotsResponse(roomId, date, slots);
    }
}