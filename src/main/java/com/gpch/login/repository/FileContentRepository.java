package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.FileContent;
import com.gpch.login.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("FileContentRepository")
public interface FileContentRepository extends JpaRepository<FileContent, Integer> {
    List<FileContent> findByRoomId(int roomId);
}
