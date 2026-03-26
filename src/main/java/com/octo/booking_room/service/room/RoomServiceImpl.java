package com.octo.booking_room.service.room;

import com.octo.booking_room.dto.room.BookedSlotDto;
import com.octo.booking_room.dto.room.BookedSlotsResponse;
import com.octo.booking_room.dto.room.CreateRoomRequest;
import com.octo.booking_room.dto.room.RoomResponse;
import com.octo.booking_room.dto.room.RoomTypeDto;
import com.octo.booking_room.entity.booking.Booking;
import com.octo.booking_room.entity.room.Room;
import com.octo.booking_room.entity.room.RoomType;
import com.octo.booking_room.exception.BadRequestException;
import com.octo.booking_room.exception.ResourceNotFoundException;
import com.octo.booking_room.repository.booking.BookingRepository;
import com.octo.booking_room.repository.room.RoomRepository;
import com.octo.booking_room.repository.room.RoomTypeRepository;
import com.octo.booking_room.utils.IdGenerator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository, RoomTypeRepository roomTypeRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    public BookedSlotsResponse getBookedSlots(String roomId, LocalDate date) {
        Room room = findRoomById(roomId);

        List<Booking> bookings = bookingRepository.findByRoom_RoomIdAndDate(roomId, date);

        List<BookedSlotDto> slots = bookings.stream()
            .flatMap(booking -> booking.getBookingSlots().stream()
                .map(slot -> new BookedSlotDto(
                    booking.getBookingId(),
                    slot.getStartHour(),
                    slot.getEndHour(),
                    booking.getStatus().name().toLowerCase()
                )))
            .collect(Collectors.toList());

        return new BookedSlotsResponse(room.getRoomId(), date, slots);
    }

    @Override
    public RoomResponse getRoomById(String roomId) {
        return toResponse(findRoomById(roomId));
    }

    @Override
    public RoomResponse createRoom(CreateRoomRequest request) {
        validateRequest(request);

        if (roomRepository.existsByNameIgnoreCaseAndFloor(request.getName(), request.getFloor())) {
            throw new BadRequestException("Room with name " + request.getName() + " on floor " + request.getFloor() + " already exists");
        }

        RoomType roomType = findRoomTypeById(request.getTypeId());

        Room room = new Room(
            IdGenerator.generateId("room-"),
            request.getName(),
            request.getFloor(),
            roomType,
            new ArrayList<>()
        );

        roomRepository.save(room);
        return toResponse(room);
    }

    @Override
    public RoomResponse updateRoom(String roomId, CreateRoomRequest request) {
        validateRequest(request);

        Room room = findRoomById(roomId);
        boolean duplicateRoom = roomRepository.existsByNameIgnoreCaseAndFloor(request.getName(), request.getFloor());
        boolean sameIdentity = room.getName().equalsIgnoreCase(request.getName()) && room.getFloor().equals(request.getFloor());

        if (duplicateRoom && !sameIdentity) {
            throw new BadRequestException("Room with name " + request.getName() + " on floor " + request.getFloor() + " already exists");
        }

        room.setName(request.getName());
        room.setFloor(request.getFloor());
        room.setRoomType(findRoomTypeById(request.getTypeId()));

        roomRepository.save(room);
        return toResponse(room);
    }

    @Override
    public void deleteRoom(String roomId) {
        Room room = findRoomById(roomId);

        if (bookingRepository.existsByRoom_RoomId(roomId)) {
            throw new BadRequestException("Room " + roomId + " cannot be deleted because it already has bookings");
        }

        roomRepository.delete(room);
    }

    private Room findRoomById(String roomId) {
        return roomRepository.findById(roomId)
            .orElseThrow(() -> new ResourceNotFoundException("Room with ID " + roomId + " not found"));
    }

    private RoomType findRoomTypeById(String typeId) {
        return roomTypeRepository.findById(typeId)
            .orElseThrow(() -> new ResourceNotFoundException("Room type with ID " + typeId + " not found"));
    }

    private RoomResponse toResponse(Room room) {
        return new RoomResponse(
            room.getRoomId(),
            room.getName(),
            room.getFloor(),
            new RoomTypeDto(
                room.getRoomType().getTypeId(),
                room.getRoomType().getName(),
                room.getRoomType().getCapacity()
            )
        );
    }

    private void validateRequest(CreateRoomRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Room name is required");
        }

        if (request.getFloor() == null) {
            throw new BadRequestException("Room floor is required");
        }

        if (request.getTypeId() == null || request.getTypeId().isBlank()) {
            throw new BadRequestException("Room type_id is required");
        }
    }
}
