package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.Room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RoomRepository")
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAll();
    
    Room findById(int id);
}