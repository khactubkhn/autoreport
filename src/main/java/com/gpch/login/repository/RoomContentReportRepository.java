package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.Room;
import com.gpch.login.model.RoomContent;
import com.gpch.login.model.RoomContentReport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RoomContentReportRepository")
public interface RoomContentReportRepository extends JpaRepository<RoomContentReport, Long> {
    List<RoomContentReport> findAll();
    
    RoomContentReport findById(int id);
    
    List<RoomContentReport> findByRoomId(int roomId);
}