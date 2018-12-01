package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RoomUserRepository")
public interface RoomUserReposity extends JpaRepository<RoomUser, Long> {
    List<RoomUser> findAll();
    
    List<RoomUser> findByUser(User user);
    
    List<RoomUser> findByUserIdAndRoomId(int userId, int roomId);
}