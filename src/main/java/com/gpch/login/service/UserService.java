package com.gpch.login.service;

import com.gpch.login.model.Role;
import com.gpch.login.model.RoomRole;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.model.Room;
import com.gpch.login.repository.RoleRepository;
import com.gpch.login.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("UserService")
public class UserService{
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        user.setCreatedDTG(new Timestamp(System.currentTimeMillis()));
        Role userRole = roleRepository.findByRole("CUSTOMER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }
    
    public User findByUsernameAndPassword(String username, String password) {
    	User user = userRepository.findByUsername(username);
    	if(user==null) return null;
    	Boolean check = bCryptPasswordEncoder.matches(password, user.getPassword());
    	if(check) {
    		return user;
    	} else {
    		return null;
    	}
    	
//    	String passwordEncoder = bCryptPasswordEncoder.encode(password);
//    	return userRepository.findByUsernameAndPassword(username, passwordEncoder);
    	
    }
    
    public User loadUserByUsername(String username) {
        
    	return userRepository.findByUsername(username);
    	
    }
    
    public List<Map<String, Object>> getRoomsJoined(String username) {
		User user = userRepository.findByUsername(username);
		List<Map<String, Object>> rooms = new ArrayList<Map<String, Object>>();
		if(user==null) {
			return null;
		} else {
			
			Set<RoomUser> roomsUser = user.getMemberRooms();
			
			if(roomsUser==null) {
				return rooms;
			}
			
			for(RoomUser roomUser: roomsUser) {
				if(roomUser.getDeleted() != 1) {
					Map<String, Object> room = new HashMap<String, Object>();
					Map<String, Object> own = new HashMap<String, Object>();
					List<Map<String, Object>> roles = new ArrayList<Map<String, Object>>();
					
					Room r = roomUser.getRoom();
					
					if(r.getDeleted()==1) {
						continue;
					}
					
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
					
					
					own.put("userId", roomUser.getRoom().getUser().getId());
					own.put("username", roomUser.getRoom().getUser().getUsername());
					own.put("firstName", roomUser.getRoom().getUser().getFirstName());
					own.put("lastName", roomUser.getRoom().getUser().getLastName());
					
					Set<RoomRole> rls = roomUser.getRoles();
					
					for(RoomRole rl: rls) {
						Map<String, Object> rlm = new HashMap<String, Object>();
						rlm.put("code", rl.getCode());
						rlm.put("name", rl.getName());
						roles.add(rlm);
					}
					
					room.put("own", own);
					
					room.put("roles", roles);
					
					rooms.add(room);
				}
			}
		}
		
		return rooms;
	}
    
    public List<Map<String, Object>> GetAllUser(String username){
    	User user = userRepository.findByUsername(username);
    	List<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
    	if(user == null) {
    		return null;
    	}else {
    		List<User> lstUser = new ArrayList<>();
    		lstUser = userRepository.findAll();
    		for (User item : lstUser) {
    			if(item.getUsername().equals(username)) 
    				continue;
				Map<String,Object> userMap = new HashMap<>();
				userMap.put("id", item.getId());
				userMap.put("username", item.getUsername());
				userMap.put("firstname", item.getFirstName());
				userMap.put("lastname", item.getLastName());
				userMap.put("phone", item.getPhone());
				users.add(userMap);
			}
    		return users;
    	}
    }
    
  
    

}