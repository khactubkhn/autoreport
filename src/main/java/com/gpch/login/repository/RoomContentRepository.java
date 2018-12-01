package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.Room;
import com.gpch.login.model.RoomContent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RoomContentRepository")
public interface RoomContentRepository extends JpaRepository<RoomContent, Long> {
    List<RoomContent> findAll();
    
    RoomContent findById(int id);
    
    List<RoomContent> findByRoomId(int roomId);
}