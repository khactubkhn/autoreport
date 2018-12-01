package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.FileContent;
import com.gpch.login.model.FileSpeakerMerge;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomContentMerge;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("FileSpeakerMergeRepository")
public interface FileSpeakerMergeRepository extends JpaRepository<FileSpeakerMerge, Integer> {
    List<FileSpeakerMerge> findByRoomId(int roomId);
}
