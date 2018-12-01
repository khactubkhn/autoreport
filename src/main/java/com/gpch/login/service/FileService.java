package com.gpch.login.service;

import com.gpch.login.constant.RoomRoleConstant;
import com.gpch.login.model.FileSave;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomCode;
import com.gpch.login.model.RoomRole;
import com.gpch.login.model.RoomSpeaker;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.model.Room;
import com.gpch.login.repository.FileSaveRepository;
import com.gpch.login.repository.RoomCodeRepository;
import com.gpch.login.repository.RoomRepository;
import com.gpch.login.repository.RoomRoleRepository;
import com.gpch.login.repository.RoomUserReposity;
import com.gpch.login.repository.UserRepository;

import net.minidev.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static org.hamcrest.CoreMatchers.nullValue;
import info.debatty.java.stringsimilarity.Damerau;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("FileService")
public class FileService{
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private RoomRepository roomRepository;
    
	@Autowired
    private RoomUserReposity roomUserRepository;
	
	@Autowired
    private RoomRoleRepository roomRoleRepository;
    
	@Autowired
    private RoomCodeRepository roomCodeRepository;
	
	@Autowired
    private FileSaveRepository fileSaveRepository;
    
	public FileSave getFileSaveById(int id) {
		return fileSaveRepository.findById(id);
	}
	
	public List<FileSave> getFileRoomId(int roomId) {
		return fileSaveRepository.findByRoomId(roomId);
	}
	
	public FileSave saveFileSaveById(int userId, int roomId, String path, String name) {
		FileSave file = new FileSave();
		file.setCreatedBy(userId);
		file.setCreatedDTG(new Timestamp(new Date().getTime()));
		file.setName(name);
		file.setPath(path);
		file.setRoomId(roomId);
		file.setUpdatedBy(userId);
		file.setUpdatedDTG(new Timestamp(new Date().getTime()));
		
		file = fileSaveRepository.save(file);
		
		return file;
	}

}