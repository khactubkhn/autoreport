package com.gpch.login.service;

import com.gpch.login.constant.RoomRoleConstant;
import com.gpch.login.model.Role;
import com.gpch.login.model.RoomCode;
import com.gpch.login.model.RoomRole;
import com.gpch.login.model.RoomSpeaker;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.model.Room;
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
			if(r!= null &&r.getDeleted()!=1) {
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
			if(u.getActive()==1&&listUserInRoom.indexOf(u.getId()) != -1&&u.getId()!=userId) {
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
    
   
public List<Map<String, Object>> getReporters1(int roomId, int userId) {
    	
    	
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

}