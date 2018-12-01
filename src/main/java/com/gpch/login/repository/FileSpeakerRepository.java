package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.FileContent;
import com.gpch.login.model.FileSpeaker;
import com.gpch.login.model.FileSpeakerMerge;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomContentMerge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("FileSpeakerRepository")
public interface FileSpeakerRepository extends JpaRepository<FileSpeaker, Integer> {
    List<FileSpeaker> findByRoomId(int roomId);
}
