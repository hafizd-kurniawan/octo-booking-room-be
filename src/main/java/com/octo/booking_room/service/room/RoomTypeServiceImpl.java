package com.octo.booking_room.service.room;

import com.octo.booking_room.dto.room.CreateRoomTypeRequest;
import com.octo.booking_room.dto.room.RoomTypeDto;
import com.octo.booking_room.entity.room.RoomType;
import com.octo.booking_room.exception.BadRequestException;
import com.octo.booking_room.exception.ResourceNotFoundException;
import com.octo.booking_room.repository.room.RoomRepository;
import com.octo.booking_room.repository.room.RoomTypeRepository;
import com.octo.booking_room.utils.IdGenerator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;

    public RoomTypeServiceImpl(RoomTypeRepository roomTypeRepository, RoomRepository roomRepository) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public RoomTypeDto createRoomType(CreateRoomTypeRequest request) {
        validateRequest(request);

        if (roomTypeRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Room type with name " + request.getName() + " already exists");
        }

        RoomType roomType = new RoomType();
        roomType.setTypeId(IdGenerator.generateId("roomtype-"));
        roomType.setName(request.getName());
        roomType.setCapacity(request.getCapacity());

        roomTypeRepository.save(roomType);
        return toDto(roomType);
    }

    @Override
    public List<RoomTypeDto> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public RoomTypeDto getRoomTypeById(String typeId) {
        return toDto(findRoomTypeById(typeId));
    }

    @Override
    public RoomTypeDto updateRoomType(String typeId, CreateRoomTypeRequest request) {
        validateRequest(request);

        RoomType roomType = findRoomTypeById(typeId);
        boolean nameChanged = !roomType.getName().equalsIgnoreCase(request.getName());

        if (nameChanged && roomTypeRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Room type with name " + request.getName() + " already exists");
        }

        roomType.setName(request.getName());
        roomType.setCapacity(request.getCapacity());
        roomTypeRepository.save(roomType);

        return toDto(roomType);
    }

    @Override
    public void deleteRoomType(String typeId) {
        RoomType roomType = findRoomTypeById(typeId);

        if (roomRepository.existsByRoomType_TypeId(typeId)) {
            throw new BadRequestException("Room type " + typeId + " cannot be deleted because it is still used by rooms");
        }

        roomTypeRepository.delete(roomType);
    }

    private RoomType findRoomTypeById(String typeId) {
        return roomTypeRepository.findById(typeId)
            .orElseThrow(() -> new ResourceNotFoundException("Room type with ID " + typeId + " not found"));
    }

    private RoomTypeDto toDto(RoomType roomType) {
        return new RoomTypeDto(
            roomType.getTypeId(),
            roomType.getName(),
            roomType.getCapacity()
        );
    }

    private void validateRequest(CreateRoomTypeRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Room type name is required");
        }

        if (request.getCapacity() == null || request.getCapacity() <= 0) {
            throw new BadRequestException("Room type capacity must be greater than 0");
        }
    }
}
