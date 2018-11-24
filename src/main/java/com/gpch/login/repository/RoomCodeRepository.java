package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.Role;
import com.gpch.login.model.RoomCode;
import com.gpch.login.model.RoomSpeaker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RoleCodeRepository")
public interface RoomCodeRepository extends JpaRepository<RoomCode, Integer> {
    RoomCode findByCode(String code);
}
