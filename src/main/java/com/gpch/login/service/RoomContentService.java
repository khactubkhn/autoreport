package com.gpch.login.service;

import com.gpch.login.constant.RoomRoleConstant;
import com.gpch.login.model.FileSave;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomCode;
import com.gpch.login.model.RoomContent;
import com.gpch.login.model.RoomContentReport;
import com.gpch.login.model.RoomRole;
import com.gpch.login.model.RoomSpeaker;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.model.Room;
import com.gpch.login.repository.RoomCodeRepository;
import com.gpch.login.repository.RoomContentReportRepository;
import com.gpch.login.repository.RoomContentRepository;
import com.gpch.login.repository.RoomRepository;
import com.gpch.login.repository.RoomRoleRepository;
import com.gpch.login.repository.RoomSpeakerRepository;
import com.gpch.login.repository.RoomUserReposity;
import com.gpch.login.repository.UserRepository;

import net.minidev.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

@Service("RoomContentService")
public class RoomContentService{
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private RoomRepository roomRepository;
	
	@Autowired
    private RoomContentRepository roomContentRepository;
    
	@Autowired
    private RoomContentReportRepository roomContentReportRepository;
	
	@Autowired
    private RoomUserReposity roomUserRepository;
	
	@Autowired
    private RoomRoleRepository roomRoleRepository;
    
	@Autowired
    private RoomCodeRepository roomCodeRepository;
	
	@Autowired
    private RoomSpeakerRepository roomSpeakerRepository;
	
	@Autowired
    private RoomService roomService;
	
	@Autowired
    private FileService fileService;
    
    
    public Map<String, Object> writeRoomContent(User user, int roomId, int speakerId, String content, long startTime, long endTime) {
    	
    	Room room = roomRepository.findById(roomId);
    	
    	if(room==null) return null;
    	
    	Set<RoomRole> rolesBy = roomService.getRoleRoomByUser(roomId, user.getId());
		if(rolesBy==null) {
			return null;
		}
		
		
		for(RoomRole roleBy: rolesBy) {
			if(roleBy.getName().equals(RoomRoleConstant.WRITE)) {
				RoomContent roomContent = new RoomContent();
				
				//RoomContentReport roomContentReport = new RoomContentReport(); 
		    	
				roomContent.setContent(content);
				roomContent.setCreatedDTG(new Timestamp(new Date().getTime()));
				roomContent.setEnd(new Timestamp(endTime));
				roomContent.setStart(new Timestamp(startTime));
				roomContent.setRoomId(room.getId());
				roomContent.setSpeakerId(speakerId);
				roomContent.setUser(user);
				roomContent.setUpdatedBy(user.getId());
				roomContent.setUpdatedDTG(new Timestamp(new Date().getTime()));
		    	
				roomContent = roomContentRepository.save(roomContent);
				
//				roomContentReport.setContent(content);
//				roomContentReport.setCreatedDTG(new Timestamp(new Date().getTime()));
//				roomContentReport.setEnd(new Timestamp(endTime));
//				roomContentReport.setStart(new Timestamp(startTime));
//				roomContentReport.setRoomId(room.getId());
//				roomContentReport.setSpeakerId(speakerId);
//				roomContentReport.setUser(user);
//				roomContentReport.setUpdatedBy(user.getId());
//				roomContentReport.setUpdatedDTG(new Timestamp(new Date().getTime()));
//		    	
//				roomContentReport = roomContentReportRepository.save(roomContentReport);
				
				
				return getRoomContent(roomContent.getId());
			}
		}
    	return null;
    	
    }
    
    private Map<String, Object> getRoomContent(int roomContentId) {
		RoomContent roomContent = roomContentRepository.findById(roomContentId);
		if(roomContent == null) {
			return null;
		}
		
		User user = roomContent.getUser();
		RoomSpeaker roomSpeaker = roomSpeakerRepository.findById(roomContent.getSpeakerId());
		
		Map<String, Object> rc = new HashMap<String, Object>();
		Map<String, Object> speaker = new HashMap<String, Object>();
		Map<String, Object> reporter = new HashMap<String, Object>();
		
		reporter.put("userId", user.getId());
		reporter.put("firstName", user.getFirstName());
		reporter.put("lastName", user.getLastName());
		reporter.put("userName", user.getUsername());
		
		speaker.put("speakerId", roomSpeaker.getId());
		speaker.put("firstName", roomSpeaker.getFirstName());
		speaker.put("lastName", roomSpeaker.getLastName());
		
		
		rc.put("speaker", speaker);
		rc.put("content", roomContent.getContent());
		rc.put("startTime", roomContent.getStart());
		rc.put("endTime", roomContent.getEnd());
		rc.put("created", roomContent.getCreatedDTG());
		rc.put("reporter", reporter);
		
		
		return rc;
		
	}
    
    public List<Map<String, Object>> getListRoomContent(int roomId, User user) {
    	List<Map<String, Object>> contents = new ArrayList<Map<String,Object>>();
    	if(roomService.checkInRoom(roomId, user)){
    		List<RoomContent> list = roomContentRepository.findByRoomId(roomId);
    		for(RoomContent roomContent: list) {
    			RoomSpeaker roomSpeaker = roomSpeakerRepository.findById(roomContent.getSpeakerId());
    			
    			Map<String, Object> rc = new HashMap<String, Object>();
    			Map<String, Object> speaker = new HashMap<String, Object>();
    			Map<String, Object> reporter = new HashMap<String, Object>();
    			
    			reporter.put("userId", roomContent.getUser().getId());
    			reporter.put("firstName", roomContent.getUser().getFirstName());
    			reporter.put("lastName", roomContent.getUser().getLastName());
    			reporter.put("username", roomContent.getUser().getUsername());
    			
    			speaker.put("speakerId", roomSpeaker.getId());
    			speaker.put("firstName", roomSpeaker.getFirstName());
    			speaker.put("lastName", roomSpeaker.getLastName());
    			
    			
    			rc.put("speaker", speaker);
    			rc.put("content", roomContent.getContent());
    			rc.put("startTime", roomContent.getStart());
    			rc.put("endTime", roomContent.getEnd());
    			rc.put("created", roomContent.getCreatedDTG());
    			rc.put("reporter", reporter);
    			
    			contents.add(rc);
    		}
    		
    		List<FileSave> fileSaves = fileService.getFileRoomId(roomId);
    		
    		for(FileSave filesave: fileSaves) {
    			User u = userRepository.findById(filesave.getCreatedBy());
    			
    			Map<String, Object> rc = new HashMap<String, Object>();
    			Map<String, Object> reporter = new HashMap<String, Object>();
    			
    			reporter.put("userId", u.getId());
    			reporter.put("firstName", u.getFirstName());
    			reporter.put("lastName", u.getLastName());
    			reporter.put("username", u.getUsername());
    			
    			rc.put("speaker", null);
    			rc.put("content", filesave.getName());
    			rc.put("startTime", null);
    			rc.put("endTime", null);
    			rc.put("created", filesave.getCreatedDTG());
    			rc.put("reporter", reporter);
    			
    			contents.add(rc);
    		}
    		contents.sort((Map<String, Object> n1, Map<String, Object> n2)->(int)((Timestamp)n1.get("created")).getTime()-(int)((Timestamp)n2.get("created")).getTime());
    		
    	}
    	return contents;
    }

}