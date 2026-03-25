package com.octo.booking_room.repository.room;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.octo.booking_room.entity.room.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, String> {
    
    List<RoomType> findAll();

    Optional<RoomType> findByName(String name);
    boolean existsByNameIgnoreCase(String name);
}
