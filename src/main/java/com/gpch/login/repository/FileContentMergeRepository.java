package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.FileContent;
import com.gpch.login.model.FileContentMerge;
import com.gpch.login.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("FileContentMergeRepository")
public interface FileContentMergeRepository extends JpaRepository<FileContentMerge, Integer> {
    List<FileContentMerge> findByRoomId(int roomId);
}
