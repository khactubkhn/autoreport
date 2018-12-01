package com.gpch.login.repository;

import java.util.List;

import com.gpch.login.model.RoomRole;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("RoomRoleRepository")
public interface RoomRoleRepository extends JpaRepository<RoomRole, Long> {
    List<RoomRole> findAll();
    
    RoomRole findByName(String name);
    
}