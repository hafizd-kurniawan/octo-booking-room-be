package com.octo.booking_room.repository.room;

import com.octo.booking_room.entity.room.Room;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    List<Room> findAll();
    boolean existsByNameIgnoreCaseAndFloor(String name, Integer floor);
    boolean existsByRoomType_TypeId(String typeId);
}
