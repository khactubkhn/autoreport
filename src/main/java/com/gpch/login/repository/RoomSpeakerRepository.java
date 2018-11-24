package com.gpch.login.repository;

import com.gpch.login.model.Role;
import com.gpch.login.model.RoomSpeaker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RoomSpeakerRepository")
public interface RoomSpeakerRepository extends JpaRepository<RoomSpeaker, Integer> {
    RoomSpeaker findById(int id);
}
