package com.gpch.login.service;

import com.gpch.login.constant.RoomRoleConstant;
import com.gpch.login.model.EditTranscriptHistory;
import com.gpch.login.model.EditedTranscript;
import com.gpch.login.model.FileContent;
import com.gpch.login.model.FileSpeaker;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomCode;
import com.gpch.login.model.RoomContent;
import com.gpch.login.model.RoomContentMerge;
import com.gpch.login.model.RoomRole;
import com.gpch.login.model.RoomSpeaker;
import com.gpch.login.model.RoomTranscript;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.model.Room;
import com.gpch.login.repository.EditTranscriptHistoryRepository;
import com.gpch.login.repository.FileContentMergeRepository;
import com.gpch.login.repository.FileContentRepository;
import com.gpch.login.repository.FileSpeakerMergeRepository;
import com.gpch.login.repository.FileSpeakerRepository;
import com.gpch.login.repository.RoomCodeRepository;
import com.gpch.login.repository.RoomContentMergeRepository;
import com.gpch.login.repository.RoomContentRepository;
import com.gpch.login.repository.RoomRepository;
import com.gpch.login.repository.RoomRoleRepository;
import com.gpch.login.repository.RoomSpeakerRepository;
import com.gpch.login.repository.RoomTranscriptRepository;
import com.gpch.login.repository.RoomUserReposity;
import com.gpch.login.repository.UserRepository;
import com.gpch.login.utils.MergeFileExcelsUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;


import net.minidev.json.JSONArray;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import static org.hamcrest.CoreMatchers.nullValue;
import info.debatty.java.stringsimilarity.Damerau;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.NGram;


import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;

@Service("RoomService")
public class RoomService{
	
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
    private RoomContentRepository roomContentRepository;
	
	@Autowired
	private RoomSpeakerRepository roomSpeakerRepository;
	
	@Autowired
    private RoomContentMergeRepository roomContentMergeRepository;
	
	@Autowired
    private FileSpeakerRepository fileSpeakerRepository;
	
	@Autowired
    private FileContentMergeRepository fileContentMergeRepository;
	
	@Autowired
    private FileContentRepository  fileContentRepository;
	
	@Autowired
    private MergeFileExcelsUtil mergeFileExcelsUtil;
	
	@Autowired
	private RoomTranscriptRepository roomTranscriptRepository;
	
	@Autowired
	private EditTranscriptHistoryRepository editTranscriptHistoryRepository;
	
    @Autowired
    public RoomService(RoomRepository roomRepository,
    		RoomUserReposity roomUserReposity) {
        this.roomRepository = roomRepository;
        this.roomUserRepository = roomUserReposity;
    }
    
    public Boolean checkInRoom(int roomId, User user) {
    	List<RoomUser> roomUsers = roomUserRepository.findByUser(user);
    	//List<RoomUser> roomUsers1 = roomUserRepository.findByUserIdAndRoomId(user.getId(), roomId);
    	System.out.println("checkInRoom:: size1="+roomUsers.size());
    	
    	for(RoomUser roomUser: roomUsers) {
    		if(roomUser.getRoom().getDeleted() !=1 && roomUser.getDeleted()!= 1 && roomUser.getRoom().getId()==roomId) {
    			return true;
    		}
    	}
    	return false;
	}
    
    public List<Map<String, Object>> getMemberRoom(int room_id) {
    	Room room = roomRepository.findById(room_id);
    	
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
		if(room==null) {
			return null;
		} else {
			
			Set<RoomUser> roomsUser = room.getMemberRooms();
			if(roomsUser==null) {
				return r;
			}
			
			for(RoomUser roomUser: roomsUser) {
				if(roomUser.getDeleted() != 1) {
					if(roomUser.getRoom().getDeleted()==1) {
						continue;
					}
					
					Map<String, Object> m = new HashMap<String, Object>();
					List<Map<String, Object>> roles = new ArrayList<Map<String, Object>>();
					m.put("userId", roomUser.getUser().getId());
					m.put("username", roomUser.getUser().getUsername());
					m.put("firstName", roomUser.getUser().getFirstName());
					m.put("lastName", roomUser.getUser().getLastName());
					
					Set<RoomRole> rls = roomUser.getRoles();
					if(rls==null) {
						continue;
					}
					for(RoomRole rl: rls) {
						Map<String, Object> rlm = new HashMap<String, Object>();
						rlm.put("code", rl.getCode());
						rlm.put("name", rl.getName());
						roles.add(rlm);
					}
					
					
					m.put("roles", roles);
					
					r.add(m);
				}
			}
		}
		
		return r;
	}
    
    public List<Map<String, Object>> getRoomSpeaker(int room_id) {
    	Room room = roomRepository.findById(room_id);
    	
		List<Map<String, Object>> rps = new ArrayList<Map<String, Object>>();
		if(room==null||room.getDeleted()==1) {
			return null;
		} else {
			
			Set<RoomSpeaker> roomSpeakers = room.getRoomSpeakers();
			if(roomSpeakers==null) {
				return rps;
			}
			
			for(RoomSpeaker roomSpeaker: roomSpeakers) {
				
				Map<String, Object> rp = new HashMap<String, Object>();
				rp.put("id", roomSpeaker.getId());
				rp.put("firstName", roomSpeaker.getFirstName());
				rp.put("lastName", roomSpeaker.getLastName());
				rp.put("createdBy", roomSpeaker.getCreatedBy());
				rp.put("createdDTG", roomSpeaker.getCreatedDTG());
				
				rps.add(rp);
				
			}
		}
		
		return rps;
	}
    
    public List<Map<String, Object>> addRoomSpeaker(int roomId, int userId, List<Map<String, Object>> speakers) {
    	Room room = roomRepository.findById(roomId);
    	
		if(room==null||room.getDeleted()==1) {
			return null;
		} else {
			
			Set<RoomSpeaker> sps = new HashSet<RoomSpeaker>();
	    	
	    	for(Map<String, Object> speaker: speakers) {
	    		RoomSpeaker sp = new RoomSpeaker();
	    		String firstName = (String) speaker.get("firstName");
	    		String lastName = (String) speaker.get("lastName");
	    		if(firstName==null||lastName==null) {
	    			continue;
	    		}
	    		
	    		sp.setRoomId(room.getId());
	    		sp.setLastName(lastName);
	    		sp.setFirstName(firstName);
	    		sp.setCreatedBy(userId);
	    		sp.setCreatedDTG(new Timestamp(new Date().getTime()));
	    		sp.setUpdatedBy(userId);
	    		sp.setUpdatedDTG(new Timestamp(new Date().getTime()));
	    		sps.add(sp);
	    	}
	    	
	    	if(room.getRoomSpeakers()==null) {
	    		room.setRoomSpeakers(sps);
	    	} else {
	    		room.getRoomSpeakers().addAll(sps);
	    	}
	    	
	    	
	    	room = roomRepository.save(room);
		}
		
		return getRoomSpeaker(roomId);
	}
    
    public String createCodeRoom(int userId, int roomId, List<String> roles) {
    	final int EXPIRE_TIME = 86400000;
    	Room room = roomRepository.findById(roomId);
    	User user = userRepository.findById(userId);
    	
    	if(room==null||user==null) return null;
    	
    	Set<RoomRole> rolesBy = getRoleRoomByUser(roomId, userId);
		if(rolesBy==null) {
			return null;
		}
		
		
		for(RoomRole roleBy: rolesBy) {
			if(roleBy.getName().equals(RoomRoleConstant.ADD_MEMBER)) {
				RoomCode roomCode = new RoomCode();
		    	String code = (new GenerateCode()).nextString();
		    	roomCode.setCode(code);
		    	roomCode.setCreatedDTG(new Timestamp(new Date().getTime()));
		    	roomCode.setUpdatedDTG(new Timestamp(new Date().getTime()));
		    	roomCode.setRoom(room);
		    	roomCode.setUser(user);
		    	roomCode.setThruDate(new Timestamp(new Date(System.currentTimeMillis() + EXPIRE_TIME).getTime()));
		    	
		    	List<RoomRole> codeRoles = new ArrayList<RoomRole>();
		    	
		    	for(String role: roles) {
		    		RoomRole r = roomRoleRepository.findByName(role);
		    		if(r != null) {
		    			codeRoles.add(r);
		    		}
		    	}
		    	
		    	roomCode.setRoles(new HashSet<RoomRole>(codeRoles));
		    	
		    	roomCode = roomCodeRepository.save(roomCode);
		    	
		    	return  code;
			}
		}
    	return null;
    	
    }
    
    public RoomUser joinRoomByCode(int userId, String code) {
		User user = userRepository.findById(userId);
		RoomCode roomCode = roomCodeRepository.findByCode(code);
		
		if(user==null||roomCode==null) return null;
		
		RoomUser roomUser = new RoomUser();
		
		System.out.println("::roomCodeRole size: " + roomCode.getRoles().size());
		
		User userBy = roomCode.getUser();
		Room room = roomCode.getRoom();

		if(checkInRoom(room.getId(), user)) {
			return null;
		}
		
		if(room.getMaxUser() < room.getNumber() + 1) {
			return null;
		}
		
		if(roomCode.getThruDate().getTime() < new Date().getTime()) {
			return null;
		}
		
    	roomUser.setDeleted(0);
    	roomUser.setUser(user);
    	roomUser.setUserCreated(userBy);
    	roomUser.setCreatedDTG(new Timestamp(new Date().getTime()));
    	roomUser.setRoom(room);
    	roomUser.setUpdatedBy(user.getId());
    	roomUser.setUpdatedDTG(new Timestamp(new Date().getTime()));
    	
    	roomUser.setRoles(roomCode.getRoles());
    	
    	room.getMemberRooms().add(roomUser);
    	room = roomRepository.save(room);
    	
    	updateNumberMemberRoom(room.getId());
    	
    	return roomUser;
	}
    
    public Room createRoom(String name, String description, int maxUser, User user, List<Map<String, Object>> speakers) {
    	Room room = new Room();
    	room.setCode("00000");
    	room.setName(name);
    	room.setActive(1);
    	room.setDeleted(0);
    	room.setMaxUser(maxUser);
    	room.setNumber(1);
    	room.setDescription(description);
    	room.setUser(user);
    	room.setCreatedDTG(new Timestamp(new Date().getTime()));
    	room.setUpdatedDTG(new Timestamp(new Date().getTime()));
    	room.setUpdatedBy(user.getId());
    	
    	room = roomRepository.save(room);
    	
    	RoomUser roomUser = new RoomUser();
    	roomUser.setDeleted(0);
    	roomUser.setUser(user);
    	roomUser.setUserCreated(user);
    	roomUser.setCreatedDTG(new Timestamp(new Date().getTime()));
    	roomUser.setRoom(room);
    	roomUser.setUpdatedBy(user.getId());
    	roomUser.setUpdatedDTG(new Timestamp(new Date().getTime()));
    	
    	List<RoomRole> roomroles = new ArrayList<RoomRole>();
    	
    	roomroles.add(roomRoleRepository.findByName(RoomRoleConstant.ADD_MEMBER));
    	roomroles.add(roomRoleRepository.findByName(RoomRoleConstant.READ));
    	roomroles.add(roomRoleRepository.findByName(RoomRoleConstant.WRITE));
    	roomroles.add(roomRoleRepository.findByName(RoomRoleConstant.EXPORT));
    	roomroles.add(roomRoleRepository.findByName(RoomRoleConstant.DELETE_MEMBER));
    	
    	roomUser.setRoles(new HashSet<RoomRole>(roomroles));
    	
    	Set<RoomUser> members = new HashSet<RoomUser>();
    	members.add(roomUser);
    	room.setMemberRooms(members);
    	
    	Set<RoomSpeaker> sps = new HashSet<RoomSpeaker>();
    	
    	for(Map<String, Object> speaker: speakers) {
    		RoomSpeaker sp = new RoomSpeaker();
    		String firstName = (String) speaker.get("firstName");
    		String lastName = (String) speaker.get("lastName");
    		if(firstName==null||lastName==null) {
    			continue;
    		}
    		sp.setRoomId(room.getId());
    		sp.setLastName(lastName);
    		sp.setFirstName(firstName);
    		sp.setCreatedBy(user.getId());
    		sp.setCreatedDTG(new Timestamp(new Date().getTime()));
    		sp.setUpdatedBy(user.getId());
    		sp.setUpdatedDTG(new Timestamp(new Date().getTime()));
    		sps.add(sp);
    	}
    	
    	room.setRoomSpeakers(sps);
    	
    	//roomUser = roomUserRepository.save(roomUser);
    	room = roomRepository.save(room);
		
		return room;
	}
    
    public Map<String, Object> getRoom(int roomId) {
    		Map<String, Object> room = new HashMap<String, Object>();
    		Map<String, Object> own = new HashMap<String, Object>();
			List<Map<String, Object>> roles = new ArrayList<Map<String, Object>>();
			
			Room r = roomRepository.findById(roomId);
			if(r!= null && r.getActive()!=0&&r.getDeleted()!=1) {
				room.put("id", r.getId());
				room.put("code", r.getCode());
				room.put("name", r.getName());
				room.put("maxUser", r.getMaxUser());
				room.put("description", r.getDescription());
				room.put("number", r.getNumber());
				room.put("active", r.getActive());
				room.put("updatedBy", r.getUpdatedBy());
				room.put("updatedDTG", r.getUpdatedDTG());
				room.put("createdDTG", r.getCreatedDTG());
				
				own.put("userId", r.getUser().getId());
				own.put("username", r.getUser().getUsername());
				own.put("firstName", r.getUser().getFirstName());
				own.put("lastName", r.getUser().getLastName());
				
				
				
				List<Map<String, Object>> members = getMemberRoom(roomId);
				
				room.put("own", own);
				room.put("speakers", getRoomSpeaker(roomId));
				room.put("members", members);
				
			}
		
		return room;
	}
    
    public RoomUser addMemberRoom(int roomId, int userId, List<String> roles, int byUserId) {
		Room room = roomRepository.findById(roomId);
		
		Set<RoomRole> rolesBy = getRoleRoomByUser(roomId, byUserId);
		if(rolesBy==null) {
			return null;
		}
		
		
		for(RoomRole roleBy: rolesBy) {
			if(roleBy.getName().equals(RoomRoleConstant.ADD_MEMBER)) {
				RoomUser roomUser = new RoomUser();
				User user = userRepository.findById(userId);
				User userBy = userRepository.findById(byUserId);
				
				if(room.getMaxUser() < room.getNumber() + 1) {
					return null;
				}

				if(checkInRoom(roomId, user)) {
					return null;
				}
				
				if(user==null) {
					return null;
				}
				
		    	roomUser.setDeleted(0);
		    	roomUser.setUser(user);
		    	roomUser.setUserCreated(userBy);
		    	roomUser.setCreatedDTG(new Timestamp(new Date().getTime()));
		    	roomUser.setRoom(room);
		    	roomUser.setUpdatedBy(user.getId());
		    	roomUser.setUpdatedDTG(new Timestamp(new Date().getTime()));
		    	
		    	List<RoomRole> roomroles = new ArrayList<RoomRole>();
		    	
		    	for(String role: roles) {
		    		RoomRole r = roomRoleRepository.findByName(role);
		    		if(r != null) {
		    			roomroles.add(r);
		    		}
		    	}
		    	
		    	roomUser.setRoles(new HashSet<RoomRole>(roomroles));
		    	
		    	room.getMemberRooms().add(roomUser);
		    	room = roomRepository.save(room);
		    	
		    	updateNumberMemberRoom(roomId);
		    	
		    	return roomUser;
			}
		}
		
		return null;
	}
    
    public RoomUser removeMemberRoom(int roomId, int userId, int byUserId) {
		
		Set<RoomRole> rolesBy = getRoleRoomByUser(roomId, byUserId);
		if(rolesBy==null) {
			return null;
		}
		
		
		for(RoomRole roleBy: rolesBy) {
			if(roleBy.getName().equals(RoomRoleConstant.DELETE_MEMBER)) {
				
				User user = userRepository.findById(userId);
				User userBy = userRepository.findById(byUserId);
				

				if(!checkInRoom(roomId, user)||user==null||userBy==null) {
					return null;
				}
				
				Set<RoomUser> roomsJoined = user.getMemberRooms();
				
				for(RoomUser roomJoined: roomsJoined) {
					if(roomJoined.getDeleted()!= 1 && roomJoined.getRoom().getId()==roomId) {
						roomJoined.setDeleted(1);
						roomJoined.setUpdatedBy(byUserId);
						roomJoined.setUpdatedDTG(new Timestamp(new Date().getTime()));
						roomUserRepository.save(roomJoined);
						
						updateNumberMemberRoom(roomId);
						
						return roomJoined;
					}
				}
			}
		}
		
		return null;
	}
    
    public Room removeRoom(int roomId, int byUserId) {
		
		Room room = roomRepository.findById(roomId);
		User byUser = userRepository.findById(byUserId);
		if(room==null||room.getUser().getId()!=byUser.getId()) {
			return null;
		}
		if(room.getUser().getId()==byUserId) {
			room.setUpdatedBy(byUserId);
			room.setUpdatedDTG(new Timestamp(new Date().getTime()));
			
			room.setDeleted(1);
			
			roomRepository.save(room);			
		}
		
		return room;
	}
    
    public Room finishRoom(int roomId, int byUserId) {
		
		Room room = roomRepository.findById(roomId);
		User byUser = userRepository.findById(byUserId);
		if(room==null||room.getUser().getId()!=byUser.getId()) {
			return null;
		}
		
		if(room.getUser().getId()==byUserId) {
			room.setUpdatedBy(byUserId);
			room.setUpdatedDTG(new Timestamp(new Date().getTime()));
			
			room.setActive(0);
			
			roomRepository.save(room);
		}
		
		return room;
	}
    
    public List<Map<String, Object>> getReporters(int roomId, int userId) {
    	
    	
		Room room = roomRepository.findById(roomId);
		User user = userRepository.findById(userId);
		List<Map<String, Object>> reporters = new ArrayList<Map<String,Object>>();
		if(room==null||user==null) {
			return null;
		}
		
		List<User> allUser = userRepository.findAll();
		Set<RoomUser> roomUsers = room.getMemberRooms();
		if(roomUsers == null) {
			return null;
		}
		
		List<Integer> listUserInRoom = new ArrayList<Integer>();
		for(RoomUser ru: roomUsers) {
			listUserInRoom.add(ru.getUser().getId());
		}
		
		for(User u: allUser) {
			if(u.getActive()==1&&listUserInRoom.indexOf(u.getId()) == -1&&u.getId()!=userId) {
				Map<String, Object> us = new HashMap<String, Object>();
				
				us.put("userId", u.getId());
				us.put("firstName", u.getFirstName());
				us.put("lastName", u.getLastName());
				us.put("username", u.getUsername());
				us.put("createdDTG", u.getCreatedDTG());
				
				reporters.add(us);
			}
		}
		
		return reporters;
	}
    
public List<Map<String, Object>> getReporters(int userId) {
    	
		User user = userRepository.findById(userId);
		List<Map<String, Object>> reporters = new ArrayList<Map<String,Object>>();
		if(user==null) {
			return null;
		}
		
		List<User> allUser = userRepository.findAll();
		
		for(User u: allUser) {
			if(u.getActive()==1&&u.getId()!=userId) {
				Map<String, Object> us = new HashMap<String, Object>();
				
				us.put("userId", u.getId());
				us.put("firstName", u.getFirstName());
				us.put("lastName", u.getLastName());
				us.put("username", u.getUsername());
				us.put("createdDTG", u.getCreatedDTG());
				
				reporters.add(us);
			}
		}
		
		return reporters;
	}
    
    private void updateNumberMemberRoom(int roomId) {
    	Room room = roomRepository.findById(roomId);
    	room.setNumber(room.getMemberRooms().size()); 
    	roomRepository.save(room);
    }
    
    public Set<RoomRole> getRoleRoomByUser(int roomId, int userId) {
		Room room = roomRepository.findById(roomId);
		
		Set<RoomUser> members = room.getMemberRooms();
		for(RoomUser member: members) {
			if(member.getUser().getId()==userId) {
				return member.getRoles();
			}
		}
		return null;
	}
    
    public void updateRoomTranscript(int roomId, int userId) {
    	List<RoomContentMerge> stenographsM = roomContentMergeRepository.findByRoomId(roomId);
    	List<FileContent> filecontentsM = fileContentRepository.findByRoomId(roomId);
    	
    	List<Integer> speakersIndex = new ArrayList<Integer>();
    	List<Map<String, Object>> speakers = new ArrayList<Map<String,Object>>();
    	
    	stenographsM.forEach(stenographM -> {
    		Map<String, Object> speaker = new HashMap<String, Object>();
    		if(speakersIndex.indexOf(stenographM.getSpeakerId()) == -1) {
    			speakersIndex.add(stenographM.getSpeakerId());
    			speaker.put("id", stenographM.getSpeakerId());
    			speaker.put("stenographs", new ArrayList<Map<String, Object>>());
    			
    		} else {
    			System.out.println("aaaaaaa::: " + speakersIndex.indexOf(stenographM.getSpeakerId()));
    			speaker = speakers.get(speakersIndex.indexOf(stenographM.getSpeakerId()));
    			
    		}
    		
    		if(!speaker.containsKey("stenographs")) {
    			speaker.put("stenographs", new ArrayList<Map<String, Object>>());
    		}
    		
    		List<Map<String, Object>> stenographs = (List<Map<String, Object>>) speaker.get("stenographs");
    		Map<String, Object> stenograph = new HashMap<String, Object>();
    		stenograph.put("start", stenographM.getStart().getTime());
    		stenograph.put("end", stenographM.getEnd().getTime());
    		stenograph.put("content", stenographM.getContent());
    		
    		stenographs.add(stenograph);
    		speakers.add(speaker);
    	});
    	
    	filecontentsM.forEach(filecontentM -> {
    		Map<String, Object> speaker = new HashMap<String, Object>();
    		if(speakersIndex.indexOf(filecontentM.getSpeakerId()) == -1) {
    			speakersIndex.add(filecontentM.getSpeakerId());
    			speaker.put("id", filecontentM.getSpeakerId());
    			speaker.put("files", new ArrayList<Map<String, Object>>());
    			
    		} else {
    			speaker = speakers.get(speakersIndex.indexOf(filecontentM.getSpeakerId()));
    		}
    		
    		if(!speaker.containsKey("files")) {
    			speaker.put("files", new ArrayList<Map<String, Object>>());
    		}
    		
    		List<Map<String, Object>> files = (List<Map<String, Object>>) speaker.get("files");
    		Map<String, Object> file = new HashMap<String, Object>();
    		file.put("start", filecontentM.getStart().getTime());
    		file.put("end", filecontentM.getEnd().getTime());
    		file.put("content", filecontentM.getContent());
    		files.add(file);
    		speakers.add(speaker);
    	});
    	
    	speakers.forEach(speaker -> {
    		if(!speaker.containsKey("stenographs")) {
    			speaker.put("stenographs", new ArrayList<Map<String, Object>>());
    		}
    		
    		if(!speaker.containsKey("files")) {
    			speaker.put("files", new ArrayList<Map<String, Object>>());
    		}
    		
    		List<Map<String, Object>> transcripts = mergeStenographTranscript((List<Map<String, Object>>)speaker.get("stenographs"), (List<Map<String, Object>>)speaker.get("files"));
    		List<RoomTranscript> transcriptsEdited = roomTranscriptRepository.findByRoomIdAndSpeakerIdAndEdited(roomId, (int)speaker.get("id"), 1);
    		//remove transcript not edited
    		List<RoomTranscript> transcriptsNotEdited = roomTranscriptRepository.findByRoomIdAndSpeakerIdAndEdited(roomId, (int)speaker.get("id"), 0);
    		roomTranscriptRepository.deleteAll(transcriptsNotEdited);
    		
    		List<Map<String, Object>> transcriptUpdate = mergeTranscriptEdited(transcripts, transcriptsEdited);
    		
    		transcriptUpdate.forEach(transcript -> {
    			String content = (String) transcript.get("content");
    			if(content==null||content.isEmpty()||content.length() < 2) {
    				return;
    			}
    			if((int)transcript.get("id") == -1) {
    				RoomTranscript rt = new RoomTranscript();
    				rt.setStart(new Timestamp((long) transcript.get("start")));
    				rt.setEnd(new Timestamp((long) transcript.get("end")));
    				rt.setContent((String)transcript.get("content"));
    				rt.setCreatedBy(userId);
    				rt.setCreatedDTG(new Timestamp(new Date().getTime()));
    				rt.setUpdatedBy(userId);
    				rt.setUpdatedDTG(new Timestamp(new Date().getTime()));
    				rt.setRoomId(roomId);
    				rt.setSpeakerId((int)speaker.get("id"));
    				rt.setIndex(0);
    				rt.setEdited(0);
    				rt = roomTranscriptRepository.save(rt);
    				
    			} else {
    				RoomTranscript rt = roomTranscriptRepository.findById((int)transcript.get("id"));
    				
    				rt.setStart(new Timestamp((long) transcript.get("start")));
    				rt.setEnd(new Timestamp((long) transcript.get("end")));
    				rt.setContent((String)transcript.get("content"));
    				
    				rt.setUpdatedBy(userId);
    				rt.setUpdatedDTG(new Timestamp(new Date().getTime()));
    				
    				rt = roomTranscriptRepository.save(rt);
    			}
    		});
    	});
    	
    }
    
    public List<Map<String, Object>> getRoomTranscript(int roomId) {
    	List<Map<String, Object>> transcripts = new ArrayList<Map<String,Object>>();
    	List<RoomTranscript> roomTranscripts = roomTranscriptRepository.findByRoomId(roomId);
    	
    	roomTranscripts.forEach(roomTranscript -> {
    		Map<String, Object> transcript = new HashMap<String, Object>();
    		Map<String, Object> speaker = new HashMap<String, Object>();
    		Map<String, Object> user = new HashMap<String, Object>();
    		User u = userRepository.findById(roomTranscript.getUpdatedBy());
    		Map<String, Object> userEditting = new HashMap<String, Object>();
    		if(roomTranscript.getEditingByUserId()!=0) {
    			User userE = userRepository.findById(roomTranscript.getEditingByUserId());
    			userEditting.put("id", userE.getId());
        		userEditting.put("firstName", userE.getFirstName());
        		userEditting.put("lastName", userE.getLastName());
        		transcript.put("editingByUser", userEditting);
    		}
    		
    		user.put("id", u.getId());
    		user.put("firstName", u.getFirstName());
    		user.put("lastName", u.getLastName());
    		transcript.put("id", roomTranscript.getId());
    		transcript.put("start", roomTranscript.getStart());
    		transcript.put("end", roomTranscript.getEnd());
    		transcript.put("content", roomTranscript.getContent());
    		transcript.put("edited", roomTranscript.getEdited());
    		transcript.put("index", roomTranscript.getIndex());
    		
    		transcript.put("roomId", roomTranscript.getRoomId());
    		transcript.put("updateBy", user);
    		transcript.put("updatedDTG", roomTranscript.getUpdatedDTG());
    		
    		RoomSpeaker roomSpeaker = roomSpeakerRepository.findById(roomTranscript.getSpeakerId());
    		speaker.put("id", roomSpeaker.getId());
    		speaker.put("firstName", roomSpeaker.getFirstName());
    		speaker.put("lastName", roomSpeaker.getLastName());
    		
    		transcript.put("speaker", speaker);
    		transcripts.add(transcript);
    	});
    	
    	transcripts.sort((t1, t2) -> (int)(((Timestamp)t1.get("start")).getTime() - ((Timestamp)t2.get("start")).getTime()));
    	
    	return transcripts;
    }
    
    public Boolean setEditingRoomTranscript(int transcriptId, int userId) {
    	RoomTranscript roomTranscript = roomTranscriptRepository.findById(transcriptId);
    	if(roomTranscript.getEditingByUserId()==0) {
    		roomTranscript.setEditingByUserId(userId);
    		roomTranscript = roomTranscriptRepository.save(roomTranscript);
    		return true;
    	}
    	return false;
    }
    
    public Boolean removeEditingRoomTranscript(int transcriptId, int userId) {
    	RoomTranscript roomTranscript = roomTranscriptRepository.findById(transcriptId);
    	if(roomTranscript.getEditingByUserId()==userId) {
    		roomTranscript.setEditingByUserId(0);
    		roomTranscript = roomTranscriptRepository.save(roomTranscript);
    		return true;
    	}
    	return false;
    }
    
    public Boolean editTranscript(int transcriptId, String content, int userId) {
    	RoomTranscript roomTranscript = roomTranscriptRepository.findById(transcriptId);
    	
    	if(roomTranscript.getEditingByUserId()==userId) {
    		int index = roomTranscript.getIndex();
    		EditTranscriptHistory editTranscriptHistory = new EditTranscriptHistory();
    		editTranscriptHistory.setIndex(index);
    		editTranscriptHistory.setContent(roomTranscript.getContent());
    		editTranscriptHistory.setCreatedBy(roomTranscript.getCreatedBy());
    		editTranscriptHistory.setCreatedDTG(roomTranscript.getCreatedDTG());
    		editTranscriptHistory.setEnd(roomTranscript.getEnd());
    		editTranscriptHistory.setStart(roomTranscript.getStart());
    		editTranscriptHistory.setTranscriptId(roomTranscript.getId());
    		editTranscriptHistory.setUpdatedBy(roomTranscript.getUpdatedBy());
    		editTranscriptHistory.setUpdatedDTG(roomTranscript.getUpdatedDTG());
    		editTranscriptHistory.setRoomId(roomTranscript.getRoomId());
    		editTranscriptHistory.setSpeakerId(roomTranscript.getSpeakerId());
    		
    		editTranscriptHistory = editTranscriptHistoryRepository.save(editTranscriptHistory);
    		
    		roomTranscript.setContent(content);
    		roomTranscript.setUpdatedBy(userId);
    		roomTranscript.setUpdatedDTG(new Timestamp(new Date().getTime()));
    		roomTranscript.setEdited(1);
    		roomTranscript.setEditingByUserId(0);
    		
    		roomTranscript.setIndex(index+1);
    		roomTranscript = roomTranscriptRepository.save(roomTranscript);
    		
    		return true;
    		
    	}
    	return false;
    }
    
    public List<Map<String, Object>> getTranscriptHistory(int roomId, int transcriptId) {
    	List<Map<String, Object>> transcripts = new ArrayList<Map<String,Object>>();
    	List<EditTranscriptHistory> roomTranscripts = editTranscriptHistoryRepository.findByTranscriptId(transcriptId);
    	RoomTranscript roomTranscripted = roomTranscriptRepository.findById(transcriptId);
    	
    	roomTranscripts.forEach(roomTranscript -> {
    		Map<String, Object> transcript = new HashMap<String, Object>();
    		Map<String, Object> speaker = new HashMap<String, Object>();
    		Map<String, Object> user = new HashMap<String, Object>();
    		User u = userRepository.findById(roomTranscript.getUpdatedBy());
    		
    		user.put("id", u.getId());
    		user.put("firstName", u.getFirstName());
    		user.put("lastName", u.getLastName());
    		transcript.put("id", roomTranscript.getId());
    		transcript.put("start", roomTranscript.getStart());
    		transcript.put("end", roomTranscript.getEnd());
    		transcript.put("content", roomTranscript.getContent());
    		transcript.put("index", roomTranscript.getIndex());
    		
    		transcript.put("roomId", roomTranscript.getRoomId());
    		transcript.put("updateBy", user);
    		transcript.put("updatedDTG", roomTranscript.getUpdatedDTG());
    		
    		RoomSpeaker roomSpeaker = roomSpeakerRepository.findById(roomTranscript.getSpeakerId());
    		speaker.put("id", roomSpeaker.getId());
    		speaker.put("firstName", roomSpeaker.getFirstName());
    		speaker.put("lastName", roomSpeaker.getLastName());
    		
    		transcript.put("speaker", speaker);
    		
    		if(roomTranscripted.getIndex() != roomTranscript.getIndex()) {
    			transcripts.add(transcript);
    		}
    		
    		
    	});
    	
    	return transcripts;
    	
    }
    
    public List<Integer> DiscontentRemoveTranscriptEditing(int userId) {
    	List<RoomTranscript> transcriptsEditing = roomTranscriptRepository.findByEditingByUserId(userId);
    	List<Integer> roomIdsUpdate = new ArrayList<Integer>();
    	for(int i = 0; i < transcriptsEditing.size(); ++i) {
    		RoomTranscript transcript = transcriptsEditing.get(i);
    		transcript.setEditingByUserId(0);
    		transcript = roomTranscriptRepository.save(transcript);
    		if(roomIdsUpdate.indexOf(transcript.getRoomId()) == -1) {
    			roomIdsUpdate.add(transcript.getRoomId());
    		}
    	}
    	
    	return roomIdsUpdate;
    	
    }
    
    public Boolean jumpTranscript(int transcriptId, int index, int userId) {
    	RoomTranscript roomTranscript = roomTranscriptRepository.findById(transcriptId);
    	List<EditTranscriptHistory> editTranscriptHistory = editTranscriptHistoryRepository.findByTranscriptId(transcriptId);
    	Boolean update = false;
    	int oldIndex = roomTranscript.getIndex();
    	String contentOld = roomTranscript.getContent();
    	int updateBy = roomTranscript.getUpdatedBy();
    	Timestamp updateDTG = roomTranscript.getCreatedDTG();
    	
    	for(int i = 0; i < editTranscriptHistory.size(); ++i) {
    		EditTranscriptHistory transcript = editTranscriptHistory.get(i);
    		if(transcript.getIndex() == index) {
    			roomTranscript.setContent(transcript.getContent());
    			roomTranscript.setIndex(index);
    			roomTranscript.setUpdatedBy(userId);
    			roomTranscript.setUpdatedDTG(new Timestamp(new Date().getTime()));
    			roomTranscript = roomTranscriptRepository.save(roomTranscript);
    			update = true;
    			break;
    		}
    	}
    	
    	if(update && oldIndex == editTranscriptHistory.size()) {
    		EditTranscriptHistory editTranscriptHistoryNew = new EditTranscriptHistory();
    		editTranscriptHistoryNew.setIndex(oldIndex);
    		editTranscriptHistoryNew.setContent(contentOld);
    		editTranscriptHistoryNew.setCreatedBy(roomTranscript.getCreatedBy());
    		editTranscriptHistoryNew.setCreatedDTG(roomTranscript.getCreatedDTG());
    		editTranscriptHistoryNew.setEnd(roomTranscript.getEnd());
    		editTranscriptHistoryNew.setStart(roomTranscript.getStart());
    		editTranscriptHistoryNew.setTranscriptId(roomTranscript.getId());
    		editTranscriptHistoryNew.setUpdatedBy(updateBy);
    		editTranscriptHistoryNew.setUpdatedDTG(updateDTG);
    		editTranscriptHistoryNew.setRoomId(roomTranscript.getRoomId());
    		editTranscriptHistoryNew.setSpeakerId(roomTranscript.getSpeakerId());
    		
    		editTranscriptHistoryNew = editTranscriptHistoryRepository.save(editTranscriptHistoryNew);
    	}
    	
    	return update;
    }
    
    public void updateRoomContentMerge(int roomId, int userId) {
    	List<RoomContent> stenographsM = roomContentRepository.findByRoomId(roomId);
    	List<Map<String, Object>> speakers = new ArrayList<Map<String,Object>>();
    	List<Integer> speakersIndex = new ArrayList<Integer>();
    	stenographsM.forEach((stenographM) -> {
    		Map<String, Object> speaker;
    		if(speakersIndex.indexOf(stenographM.getSpeakerId()) == -1) {
    			speaker = new HashMap<String, Object>();
    			
    			List<Map<String, Object>> contents = new ArrayList<Map<String, Object>>();
    			
    			speaker.put("id", stenographM.getSpeakerId());
    			speaker.put("contents", contents);
    			speakersIndex.add(stenographM.getSpeakerId());
    			speakers.add(speaker);
    		}
    		
    		speaker = speakers.get(speakersIndex.indexOf(stenographM.getSpeakerId()));
    		List<Map<String, Object>> contents = (List<Map<String, Object>>) speaker.get("contents");
    		Map<String, Object> content = new HashMap<String, Object>();
    		content.put("id", stenographM.getId());
    		content.put("start", stenographM.getStart().getTime());
    		content.put("end", stenographM.getEnd().getTime());
    		content.put("content", stenographM.getContent());
    		
    		contents.add(content);
    		
    	});
    	
    	List<RoomContentMerge> rcsOld = roomContentMergeRepository.findByRoomId(roomId);
    	roomContentMergeRepository.deleteAll(rcsOld);
    	
    	speakers.forEach((speaker) -> {
    		List<Map<String, Object>> stenographs = mergeStenographOrFileWhat((List<Map<String, Object>>) speaker.get("contents"));
    		
    		speaker.put("stenographs", stenographs);
    		stenographs.forEach((stenograph) -> {
    			RoomContentMerge rcsNew = new RoomContentMerge(); 
    			
    			rcsNew.setContent((String)stenograph.get("content"));
    			rcsNew.setCreatedBy(userId);
    			rcsNew.setCreatedDTG(new Timestamp(new Date().getTime()));
    			rcsNew.setEnd(new Timestamp((long)stenograph.get("end")));
    			rcsNew.setStart(new Timestamp((long)stenograph.get("start")));
    			rcsNew.setUpdatedBy(userId);
    			rcsNew.setUpdatedDTG(new Timestamp(new Date().getTime()));
    			rcsNew.setRoomId(roomId);
    			rcsNew.setSpeakerId((int)speaker.get("id"));    
    			
    			rcsNew = roomContentMergeRepository.save(rcsNew); 
    		});
    	});
    }
    
    public void updateRoomFileContent(int roomId, int userId) {
    	
    	// 0 - "Start"
        // 1 - "End"
    	// 4 - "Speaker"
    	// 2 - "Content"
    	List<Vector<String>> fileContentMerge = mergeFileExcelsUtil.merge(roomId);
    	
    	//update table FileRoomContent
    	fileContentRepository.deleteAll(fileContentRepository.findByRoomId(roomId));
    	
    	fileContentMerge.forEach(file -> {
    		String fullName = file.get(4);
    		String firstName = "";
    		String lastName="";
    		
    		int spaceNameIndex = fullName.lastIndexOf(" ");
    		
    		if(spaceNameIndex == -1) {
    			lastName = fullName;
    		} else {
    			lastName = fullName.substring(spaceNameIndex + 1);
    			firstName = fullName.substring(0, spaceNameIndex);
    		}
    		
    		RoomSpeaker roomSpeaker = roomSpeakerRepository.findByFirstNameAndLastNameAndRoomId(firstName, lastName, roomId);
    		
    		if(roomSpeaker == null) {
    			roomSpeaker = new RoomSpeaker();
    			roomSpeaker.setFirstName(firstName);
    			roomSpeaker.setLastName(lastName);
    			roomSpeaker.setFrequency(-1);
    			roomSpeaker.setCreatedBy(userId);
    			roomSpeaker.setCreatedDTG(new Timestamp(new Date().getTime()));
    			roomSpeaker.setUpdatedBy(userId);
    			roomSpeaker.setUpdatedDTG(new Timestamp(new Date().getTime()));
    			roomSpeaker.setRoomId(roomId);
    			
    			roomSpeaker = roomSpeakerRepository.save(roomSpeaker);
    			
    		}
    		
    		FileContent newFileContent = new FileContent();
    		
    		newFileContent.setStart(new Timestamp(convertTimeStamp(file.get(0))));
    		newFileContent.setEnd(new Timestamp(convertTimeStamp(file.get(1))));
    		newFileContent.setContent(file.get(2));
    		newFileContent.setRoomId(roomId);
    		newFileContent.setSpeakerId(roomSpeaker.getId());
    		newFileContent.setCreatedBy(userId);
    		newFileContent.setCreatedDTG(new Timestamp(new Date().getTime()));
    		newFileContent.setUpdatedBy(userId);
    		newFileContent.setUpdatedDTG(new Timestamp(new Date().getTime()));
    		
    		newFileContent = fileContentRepository.save(newFileContent);
    	});
    }
    
    
    
    
    
    private final int latency = 100; //do tre tg 1s
    private final static double minDistance = 2;
    private final int minSegment = 200; //tg ngat doan 2s
    private final static String delimiters = "\\s+|,\\s*|\\.\\s*";
    
    
    public List<Map<String, Object>> mergeStenographOrFileWhat(List<Map<String, Object>> listTranscript) {
    	List<Map<String, Object>> r = new ArrayList<Map<String,Object>>();
    	listTranscript.sort((Map<String, Object> o1, Map<String, Object> o2)->(int) (long)o1.get("start")-(int) (long)o2.get("start"));
    	
    	Map<String, Object> temp = null;
    	for(int i = 0; i < listTranscript.size(); ++i) {
    		Map<String, Object> transcript = listTranscript.get(i);
    		
    		long s = (long) transcript.get("start");
    		long e = (long) transcript.get("end");
    		String c = (String) transcript.get("content");
    		
    		if(temp == null) {
    			temp = new HashMap<String, Object>(); 
				temp.put("start", s);
		    	temp.put("end", e);
		    	temp.put("content", c);
		    	continue;
    		}
    		

    		long start = (long) temp.get("start");
    		long end = (long) temp.get("end");
    		String content = (String) temp.get("content");

    		if(end > s - latency) {// s dan xen hoac long nhau
    			Map<String, Object> substrings = longestSubstring(content, c);
				Map<String, Object> object1 = (Map<String, Object>) substrings.get("object1");
				Map<String, Object> object2 = (Map<String, Object>) substrings.get("object2");
    			
				if(end > e - latency) { //2 doan long nhau
					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty()) {
						String value = content.substring(0, (int)object1.get("start")) + c + " " + content.substring((int)object1.get("end"));
						temp.put("content", value);	
					}
				} else {//2 doan dan xen
					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty() && object2.get("value") != null && !((String)object2.get("value")).isEmpty()) {

						String value = content + " " +  c.substring((int)object2.get("end"));
						temp.put("content", value);	
						
				    	temp.put("end", e);
					}
					
				}
    		}else {//k dan xen
				if(s - latency - end > minSegment) { // nam 2 doan
					r.add(temp);
					temp = new HashMap<String, Object>(); 
					temp.put("start", s);
			    	temp.put("end", e);
			    	temp.put("content", c);	
				} else {//gop 2 phan thanh 1 doan
					temp.put("end", e);
			    	temp.put("content", content + " " + c);
				}
			}
    	}
    	
    	r.add(temp);
    	
    	return r;
    	
	}
    
    
    public List<Map<String, Object>> mergeStenographTranscript(List<Map<String, Object>> listStenograph, List<Map<String, Object>> listTranscript) {
    	List<Map<String, Object>> r = new ArrayList<Map<String,Object>>();
    	listStenograph.sort((Map<String, Object> o1, Map<String, Object> o2)->(int) (long) o1.get("start")-(int) (long)o2.get("start"));
    	listTranscript.sort((Map<String, Object> o1, Map<String, Object> o2)->(int) (long)o1.get("start")-(int) (long)o2.get("start"));
    	
    	int i = 0, j = 0;
    	Map<String, Object> temp = null;
    	while(i < listStenograph.size() && j < listTranscript.size()) {
    		Map<String, Object> stenograph = listStenograph.get(i);
    		Map<String, Object> transcript = listTranscript.get(j);
    		String contentStenograph = (String)stenograph.get("content");
    		String contentTranscript = (String)transcript.get("content");
    		
    		
    		long start1 = (long) stenograph.get("start");
    		long end1 = (long) stenograph.get("end");
    		long start2 = (long) transcript.get("start");
    		long end2 = (long) transcript.get("end");
    		
    		long s = 0;
    		long e = 0;
    		String c = "";
    		
    		
    		
    		if(start1 < start2) {
    			s = start1;
    			e = end1;
    			c = contentStenograph;
    			i++;
    		} else {
    			s = start2;
    			e = end2;
    			c = contentTranscript;
    			j++;
    		}
    		

    		if(temp == null) {
    			temp = new HashMap<String, Object>(); 
				temp.put("start", s);
		    	temp.put("end", e);
		    	temp.put("content", c);
		    	continue;
    		}

    		long start = (long) temp.get("start");
    		long end = (long) temp.get("end");
    		String content = (String) temp.get("content");
    		
    		if(end > s - latency) {// s dan xen hoac long nhau
    			Map<String, Object> substrings = longestSubstring(content, c);
				Map<String, Object> object1 = (Map<String, Object>) substrings.get("object1");
				Map<String, Object> object2 = (Map<String, Object>) substrings.get("object2");
    			
				if(end > e - latency) { //2 doan long nhau
					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty()) {
						String value = content.substring(0, (int)object1.get("start")) + c + " " + content.substring((int)object1.get("end"));
						temp.put("content", value);	
					}
				} else {//2 doan dan xen
					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty() && object2.get("value") != null && !((String)object2.get("value")).isEmpty()) {

						String value = content + " " +  c.substring((int)object2.get("end"));
						temp.put("content", value);	
						
				    	temp.put("end", e);
					}
					
				}
    		}else {//k dan xen
				if(s - latency - end > minSegment) { // nam 2 doan
					r.add(temp);
					temp = new HashMap<String, Object>(); 
					temp.put("start", s);
			    	temp.put("end", e);
			    	temp.put("content", c);	
				} else {//gop 2 phan thanh 1 doan
					temp.put("end", e);
			    	temp.put("content", content + " " + c);
				}
			}
    	}
    	
    	if(temp == null) {
			temp = new HashMap<String, Object>(); 
			temp.put("start", 0L);
	    	temp.put("end", 0L);
	    	temp.put("content", "");
		}
    	
    	if(i < listStenograph.size()) {
    		for(int k = i; k < listStenograph.size(); ++k) {
    			Map<String, Object> stenograph = listStenograph.get(k);
    			long s = (long) stenograph.get("start");
        		long e = (long) stenograph.get("end");
        		String c = (String) stenograph.get("content");
    			
    			
    			long start = (long) temp.get("start");
        		long end = (long) temp.get("end");
        		String content = (String) temp.get("content");
        		
        		if(end > s - latency) {// s dan xen hoac long nhau
        			Map<String, Object> substrings = longestSubstring(content, c);
    				Map<String, Object> object1 = (Map<String, Object>) substrings.get("object1");
    				Map<String, Object> object2 = (Map<String, Object>) substrings.get("object2");
        			
    				if(end > e - latency) { //2 doan long nhau
    					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty()) {
    						String value = content.substring(0, (int)object1.get("start")) + c + content.substring((int)object1.get("end"));
    						temp.put("content", value);	
    					}
    				} else {//2 doan dan xen
    					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty() && object2.get("value") != null && !((String)object2.get("value")).isEmpty()) {

    						String value = content + c.substring((int)object2.get("end"));
    						temp.put("content", value);	
    						
    				    	temp.put("end", e);
    					}
    					
    				}
        		}else {//k dan xen
    				if(s - latency - end > minSegment) { // nam 2 doan
    					r.add(temp);
    					temp = new HashMap<String, Object>(); 
    					temp.put("start", s);
    			    	temp.put("end", e);
    			    	temp.put("content", c);	
    				} else {//gop 2 phan thanh 1 doan
    					temp.put("end", e);
    			    	temp.put("content", content + " " + c);
    				}
    			}
    			
    		}
    	}
    	
    	if(j < listTranscript.size()) {
    		for(int k = j; k < listTranscript.size(); ++k) {
    			Map<String, Object> transcript = listTranscript.get(k);
    			long s = (long) transcript.get("start");
        		long e = (long) transcript.get("end");
        		String c = (String) transcript.get("content");
    			
    			
    			long start = (long) temp.get("start");
        		long end = (long) temp.get("end");
        		String content = (String) temp.get("content");
        		
        		if(end > s - latency) {// s dan xen hoac long nhau
        			Map<String, Object> substrings = longestSubstring(content, c);
    				Map<String, Object> object1 = (Map<String, Object>) substrings.get("object1");
    				Map<String, Object> object2 = (Map<String, Object>) substrings.get("object2");
        			
    				if(end > e - latency) { //2 doan long nhau
    					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty()) {
    						String value = content.substring(0, (int)object1.get("start")) + c + content.substring((int)object1.get("end"));
    						temp.put("content", value);	
    					}
    				} else {//2 doan dan xen
    					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty() && object2.get("value") != null && !((String)object2.get("value")).isEmpty()) {

    						String value = content + c.substring((int)object2.get("end"));
    						temp.put("content", value);	
    						
    				    	temp.put("end", e);
    					}
    					
    				}
        		}else {//k dan xen
    				if(s - latency - end > minSegment) { // nam 2 doan
    					r.add(temp);
    					temp = new HashMap<String, Object>(); 
    					temp.put("start", s);
    			    	temp.put("end", e);
    			    	temp.put("content", c);	
    				} else {//gop 2 phan thanh 1 doan
    					temp.put("end", e);
    			    	temp.put("content", content + " " + c);
    				}
    			}
    		}
    	}
    	
    	r.add(temp);
    	
    	return r;
	}
    
    public List<Map<String, Object>> mergeTranscriptEdited(List<Map<String, Object>> transcriptsNew, List<RoomTranscript> transcriptsEdited) {
    	List<Map<String, Object>> r = new ArrayList<Map<String,Object>>();
    	transcriptsNew.sort((o1, o2)->(int) (long) o1.get("start")-(int) (long)o2.get("start"));
    	transcriptsEdited.sort((trans1, trans2) -> {return (int)(trans1.getStart().getTime() - trans2.getStart().getTime());});
    	
    	int i = 0, j = 0;
    	Map<String, Object> temp = null;
    	if(temp == null) {
			temp = new HashMap<String, Object>(); 
			temp.put("id", -1);
			temp.put("start", 0L);
	    	temp.put("end", 0L);
	    	temp.put("content", "");
		}
    	while(i < transcriptsNew.size() && j < transcriptsEdited.size()) {
    		Map<String, Object> transcriptNew  = transcriptsNew.get(i);
    		RoomTranscript transcriptEdited = transcriptsEdited.get(j);
    		
    		String contentNew = (String)transcriptNew.get("content");
    		String contentEdited = transcriptEdited.getContent();
    		
    		
    		long start1 = (long) transcriptNew.get("start");
    		long end1 = (long) transcriptNew.get("end");
    		long start2 = (long) transcriptEdited.getStart().getTime();
    		long end2 = (long) transcriptEdited.getEnd().getTime();
    		
    		long s = 0;
    		long e = 0;
    		String c = "";
    		int id = -1;
    		
    		
    		if(start1 < start2) {
    			s = start1;
    			e = end1;
    			c = contentNew;
    			i++;
    		} else {
    			id = transcriptEdited.getId();
    			s = start2;
    			e = end2;
    			c = contentEdited;
    			j++;
    		}
    		

    		if(temp == null) {
    			temp = new HashMap<String, Object>(); 
    			temp.put("id", id);
    			temp.put("start", s);
		    	temp.put("end", e);
		    	temp.put("content", c);
		    	continue;
    		}

    		long start = (long) temp.get("start");
    		long end = (long) temp.get("end");
    		String content = (String) temp.get("content");
    		
    		if(end > s - latency) {// s dan xen hoac long nhau
    			Map<String, Object> substrings = longestSubstring(content, c);
				if(substrings==null) {
					continue;
				}
    			Map<String, Object> object1 = (Map<String, Object>) substrings.get("object1");
				Map<String, Object> object2 = (Map<String, Object>) substrings.get("object2");
    			
				if(end > e - latency) { //2 doan long nhau
					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty()) {
						if(id !=-1&&(int)temp.get("id")!=-1) {
							r.add(temp);
							temp = new HashMap<String, Object>(); 
							temp.put("id", id);
							temp.put("start", s);
					    	temp.put("end", e);
					    	temp.put("content", c);	
						} else if(id !=-1) {
							String value = content.substring(0, (int)object1.get("start")) + c + " " + content.substring((int)object1.get("end"));
							temp.put("id", id);
							temp.put("content", value);	
						} else if((int)temp.get("id") !=-1) {
							
						} else {
							String value = content.substring(0, (int)object1.get("start")) + c + " " + content.substring((int)object1.get("end"));
							temp.put("content", value);	
						}
					}
				} else {//2 doan dan xen
					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty() && object2.get("value") != null && !((String)object2.get("value")).isEmpty()) {
						if(id !=-1&&(int)temp.get("id")!=-1) {
							r.add(temp);
							temp = new HashMap<String, Object>(); 
							temp.put("id", id);
							temp.put("start", s);
					    	temp.put("end", e);
					    	temp.put("content", c);	
						} else if(id !=-1) {
							
							String value = content.substring(0, (int)object2.get("start")) + " " +  c;
							temp.put("id", id);
							temp.put("content", value);	
							temp.put("end", e);
							
						} else if((int)temp.get("id") !=-1) {
							String value = content + " " +  c.substring((int)object2.get("end"));
							temp.put("content", value);	
							temp.put("end", e);
						} else {
							String value = content + " " +  c.substring((int)object2.get("end"));
							temp.put("content", value);	
							
					    	temp.put("end", e);
						}
						
					}
					
				}
    		}else {//k dan xen
				if(s - latency - end > minSegment) { // nam 2 doan
					r.add(temp);
					temp = new HashMap<String, Object>(); 
					temp.put("id", id);
					temp.put("start", s);
			    	temp.put("end", e);
			    	temp.put("content", c);	
				} else {//gop 2 phan thanh 1 doan
					if(id !=-1&&(int)temp.get("id")!=-1) {
						r.add(temp);
						temp = new HashMap<String, Object>(); 
						temp.put("id", id);
						temp.put("start", s);
				    	temp.put("end", e);
				    	temp.put("content", c);	
					} else if(id !=-1) {
						temp.put("id", id);
						temp.put("end", e);
				    	temp.put("content", content + " " + c);
						
					} else if((int)temp.get("id") !=-1) {
						temp.put("end", e);
				    	temp.put("content", content + " " + c);
					} else {
						temp.put("end", e);
				    	temp.put("content", content + " " + c);
					}
				}
			}
    	}
    	
    	if(i < transcriptsNew.size()) {
    		for(int k = i; k < transcriptsNew.size(); ++k) {
    			Map<String, Object> transcript = transcriptsNew.get(k);
    			int id = -1;
    			long s = (long) transcript.get("start");
        		long e = (long) transcript.get("end");
        		String c = (String) transcript.get("content");
    			
    			long start = (long) temp.get("start");
        		long end = (long) temp.get("end");
        		String content = (String) temp.get("content");
        		
        		if(end > s - latency) {// s dan xen hoac long nhau
        			if(c == null || c.isEmpty()) {
        				continue;
        			}
        			
        			if(content == null || content.isEmpty()) {
        				temp.put("id", id);
						temp.put("start", s);
				    	temp.put("end", e);
				    	temp.put("content", c);	
        				continue;
        			}
        			Map<String, Object> substrings = longestSubstring(content, c);
    				Map<String, Object> object1 = (Map<String, Object>) substrings.get("object1");
    				Map<String, Object> object2 = (Map<String, Object>) substrings.get("object2");
        			
    				if(end > e - latency) { //2 doan long nhau
    					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty()) {
    						if(id !=-1&&(int)temp.get("id")!=-1) {
    							r.add(temp);
    							temp = new HashMap<String, Object>(); 
    							temp.put("id", id);
    							temp.put("start", s);
    					    	temp.put("end", e);
    					    	temp.put("content", c);	
    						} else if(id !=-1) {
    							String value = content.substring(0, (int)object1.get("start")) + c + " " + content.substring((int)object1.get("end"));
    							temp.put("id", id);
    							temp.put("content", value);	
    						} else if((int)temp.get("id") !=-1) {
    							
    						} else {
    							String value = content.substring(0, (int)object1.get("start")) + c + " " + content.substring((int)object1.get("end"));
    							temp.put("content", value);	
    						}
    					}
    				} else {//2 doan dan xen
    					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty() && object2.get("value") != null && !((String)object2.get("value")).isEmpty()) {
    						if(id !=-1&&(int)temp.get("id")!=-1) {
    							r.add(temp);
    							temp = new HashMap<String, Object>(); 
    							temp.put("id", id);
    							temp.put("start", s);
    					    	temp.put("end", e);
    					    	temp.put("content", c);	
    						} else if(id !=-1) {
    							
    							String value = content.substring(0, (int)object2.get("start")) + " " +  c;
    							temp.put("id", id);
    							temp.put("content", value);	
    							temp.put("end", e);
    							
    						} else if((int)temp.get("id") !=-1) {
    							String value = content + " " +  c.substring((int)object2.get("end"));
    							temp.put("content", value);	
    							temp.put("end", e);
    						} else {
    							String value = content + " " +  c.substring((int)object2.get("end"));
    							temp.put("content", value);	
    							
    					    	temp.put("end", e);
    						}
    						
    					}
    					
    				}
        		}else {//k dan xen
    				if(s - latency - end > minSegment) { // nam 2 doan
    					r.add(temp);
    					temp = new HashMap<String, Object>(); 
    					temp.put("id", id);
    					temp.put("start", s);
    			    	temp.put("end", e);
    			    	temp.put("content", c);	
    				} else {//gop 2 phan thanh 1 doan
    					if(id !=-1&&(int)temp.get("id")!=-1) {
    						r.add(temp);
    						temp = new HashMap<String, Object>(); 
    						temp.put("id", id);
    						temp.put("start", s);
    				    	temp.put("end", e);
    				    	temp.put("content", c);	
    					} else if(id !=-1) {
    						temp.put("id", id);
    						temp.put("end", e);
    				    	temp.put("content", content + " " + c);
    						
    					} else if((int)temp.get("id") !=-1) {
    						temp.put("end", e);
    				    	temp.put("content", content + " " + c);
    					} else {
    						temp.put("end", e);
    				    	temp.put("content", content + " " + c);
    					}
    				}
    			}
    			
    		}
    	}
    	
    	if(j < transcriptsEdited.size()) {
    		for(int k = j; k < transcriptsEdited.size(); ++k) {
    			RoomTranscript transcript = transcriptsEdited.get(k);
    			int id = transcript.getId();
    			long s = transcript.getStart().getTime();
        		long e = transcript.getEnd().getTime();
        		String c = transcript.getContent();
    			
    			
    			long start = (long) temp.get("start");
        		long end = (long) temp.get("end");
        		String content = (String) temp.get("content");
        		
        		if(end > s - latency) {// s dan xen hoac long nhau
        			Map<String, Object> substrings = longestSubstring(content, c);
    				Map<String, Object> object1 = (Map<String, Object>) substrings.get("object1");
    				Map<String, Object> object2 = (Map<String, Object>) substrings.get("object2");
        			
    				if(end > e - latency) { //2 doan long nhau
    					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty()) {
    						if(id !=-1&&(int)temp.get("id")!=-1) {
    							r.add(temp);
    							temp = new HashMap<String, Object>(); 
    							temp.put("id", id);
    							temp.put("start", s);
    					    	temp.put("end", e);
    					    	temp.put("content", c);	
    						} else if(id !=-1) {
    							String value = content.substring(0, (int)object1.get("start")) + c + " " + content.substring((int)object1.get("end"));
    							temp.put("id", id);
    							temp.put("content", value);	
    						} else if((int)temp.get("id") !=-1) {
    							
    						} else {
    							String value = content.substring(0, (int)object1.get("start")) + c + " " + content.substring((int)object1.get("end"));
    							temp.put("content", value);	
    						}
    					}
    				} else {//2 doan dan xen
    					if(object1.get("value") != null && !((String)object1.get("value")).isEmpty() && object2.get("value") != null && !((String)object2.get("value")).isEmpty()) {
    						if(id !=-1&&(int)temp.get("id")!=-1) {
    							r.add(temp);
    							temp = new HashMap<String, Object>(); 
    							temp.put("id", id);
    							temp.put("start", s);
    					    	temp.put("end", e);
    					    	temp.put("content", c);	
    						} else if(id !=-1) {
    							
    							String value = content.substring(0, (int)object2.get("start")) + " " +  c;
    							temp.put("id", id);
    							temp.put("content", value);	
    							temp.put("end", e);
    							
    						} else if((int)temp.get("id") !=-1) {
    							String value = content + " " +  c.substring((int)object2.get("end"));
    							temp.put("content", value);	
    							temp.put("end", e);
    						} else {
    							String value = content + " " +  c.substring((int)object2.get("end"));
    							temp.put("content", value);	
    							
    					    	temp.put("end", e);
    						}
    						
    					}
    					
    				}
        		}else {//k dan xen
    				if(s - latency - end > minSegment) { // nam 2 doan
    					r.add(temp);
    					temp = new HashMap<String, Object>(); 
    					temp.put("id", id);
    					temp.put("start", s);
    			    	temp.put("end", e);
    			    	temp.put("content", c);	
    				} else {//gop 2 phan thanh 1 doan
    					if(id !=-1&&(int)temp.get("id")!=-1) {
    						r.add(temp);
    						temp = new HashMap<String, Object>(); 
    						temp.put("id", id);
    						temp.put("start", s);
    				    	temp.put("end", e);
    				    	temp.put("content", c);	
    					} else if(id !=-1) {
    						temp.put("id", id);
    						temp.put("end", e);
    				    	temp.put("content", content + " " + c);
    						
    					} else if((int)temp.get("id") !=-1) {
    						temp.put("end", e);
    				    	temp.put("content", content + " " + c);
    					} else {
    						temp.put("end", e);
    				    	temp.put("content", content + " " + c);
    					}
    				}
    			}
    		}
    	}
    	
    	r.add(temp);
    	
    	return r;
	}
   
    //Longest Common Substring Algorithm
    //https://karussell.wordpress.com/2011/04/14/longest-common-substring-algorithm-in-java/
    public static Map<String, Object> longestSubstring(String string1, String string2) {
    		Map<String, Object> r = new HashMap<String, Object>();
    	  String sb = "";
    	  if (string1 == null || string1.isEmpty() || string2 == null || string2.isEmpty())
    	    return null;

    	  // ignore case
    	  String str1 = string1.toLowerCase();
    	  String str2 = string2.toLowerCase();

    	  int start1 = 0, end1 = 0;
    	  int start2 = 0, end2 = 0;
    	  
    	  String[] list1 = str1.split(delimiters);
    	  int[] indexList1 = new int[list1.length];
    	  int temp = 0;
    	  for(int i = 0; i < list1.length; ++i) {
    		  indexList1[i] = str1.indexOf(list1[i], temp);
    		  temp = indexList1[i];
    	  }
    	  
    	  String[] list2 = str2.split(delimiters);
    	  int[] indexList2 = new int[list2.length];
    	  temp = 0;
    	  for(int i = 0; i < list2.length; ++i) {
    		  indexList2[i] = str2.indexOf(list2[i], temp);
    		  temp = indexList2[i];
    	  }
    	  
    	  // java initializes them already with 0
    	  int[][] num = new int[list1.length][list2.length];
    	  int maxlen = 0;
    	  int lastSubsBegin = 0;
    	  Damerau d = new Damerau();
    	  
    	  for (int i = 0; i < list1.length; i++) {
    	  for (int j = 0; j < list2.length; j++) {
    		
    	    if (d.distance(list1[i], list2[j]) <= minDistance) {
    	      if ((i == 0) || (j == 0))
    	         num[i][j] = 1;
    	      else
    	         num[i][j] = 1 + num[i - 1][j - 1];

    	      if (num[i][j] > maxlen) {
    	        maxlen = num[i][j];
    	        // generate substring from str1 => i
    	        int thisSubsBegin = i - num[i][j] + 1;
    	        if (lastSubsBegin == thisSubsBegin) {
    	           //if the current LCS is the same as the last time this block ran
    	        	if(i + 1 == list1.length) {
    	        		end1 = string1.length();
    	        	} else {
    	        		end1 = indexList1[i+1];
    	        		sb = string1.substring(start1, end1);
    	        	}
    	        	
    	        	if(j + 1 == list2.length) {
    	        		end2 = string2.length();
    	        	} else {
    	        		end2 = indexList2[j+1];
    	        	}
    	        	
    	        	
    	        } else {
    	           //this block resets the string builder if a different LCS is found
    	        	lastSubsBegin = thisSubsBegin;
    	        	
//    	        	start1 = indexList1[i];
//    	        	start2 = indexList2[j]; 
    	        	
    	        	start1 = indexList1[lastSubsBegin];
    	        	start2 = indexList2[lastSubsBegin]; 
    	        	
    	        	sb = string1.substring(indexList1[lastSubsBegin], indexList1[i+1]);
    	           
    	        	//sb.append(str1.substring(lastSubsBegin, i + 1));
    	        }
    	     }
    	  }
    	  }}
    	  
    	  Map<String, Object> rs1 = new HashMap<String, Object>();
    	  rs1.put("start", start1);
    	  rs1.put("end", end1);
    	  rs1.put("value", string1.substring(start1, end1));
    	  
    	  
    	  Map<String, Object> rs2 = new HashMap<String, Object>();
    	  rs2.put("start", start2);
    	  rs2.put("end", end2);
    	  rs2.put("value", string2.substring(start2, end2));
    	  
    	  r.put("object1", rs1);
    	  r.put("object2", rs2);
    	  return r;
    }
    
    public long convertTimeStamp(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        try {
            Date date = simpleDateFormat.parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1L;
    }
    	

}