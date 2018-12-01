package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.FileContent;
import com.gpch.login.model.FileContentMerge;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomTranscript;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RoomTranscriptRepository")
public interface RoomTranscriptRepository extends JpaRepository<RoomTranscript, Integer> {
	RoomTranscript findById(int id);
	List<RoomTranscript> findByRoomId(int roomId);
    List<RoomTranscript> findByRoomIdAndSpeakerIdAndEdited(int roomId, int speakerId, int edited);
    List<RoomTranscript> findByEditingByUserId(int findByEditingByUserId);
}
