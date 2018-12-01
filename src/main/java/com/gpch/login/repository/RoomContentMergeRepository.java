package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.FileContent;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomContentMerge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RoomContentMergeRepository")
public interface RoomContentMergeRepository extends JpaRepository<RoomContentMerge, Integer> {
    List<RoomContentMerge> findByRoomId(int roomId);
}
