package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.FileSave;
import com.gpch.login.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("FileSaveRepository")
public interface FileSaveRepository extends JpaRepository<FileSave, Integer> {
	FileSave findById(int id);
	
	List<FileSave> findByRoomId(int roomId);
}
