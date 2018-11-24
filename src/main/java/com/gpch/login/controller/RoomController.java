package com.gpch.login.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.gpch.login.model.Room;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.service.JwtService;
import com.gpch.login.service.RoomContentService;
import com.gpch.login.service.RoomService;
import com.gpch.login.service.UserService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/room")
public class RoomController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private RoomService roomService;
    
    @Autowired
    private RoomContentService roomContentService;
    
    @RequestMapping(value = "/joined", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> getRoomJoined(HttpServletRequest request) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	List<Map<String, Object>> rooms = userService.getRoomsJoined(user.getUsername());
	
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", rooms);
        
        return result;
    }
    
    @RequestMapping(value = "/members/{roomId}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> getRoomMembers(@PathVariable int roomId, HttpServletRequest request) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	List<Map<String, Object>> members = null;
    	
    	if(roomService.checkInRoom(roomId, user)) {
    		members = roomService.getMemberRoom(roomId);
    	}
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", members);
        
        return result;
    }
    
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> createRoom(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> room = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("name")||!payload.containsKey("description")||!payload.containsKey("maxUser")||!payload.containsKey("speakers")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	
    	String name = (String) payload.get("name");
    	String description = (String) payload.get("description");
    	int maxUser = (int) payload.get("maxUser");
    	List<Map<String, Object>> speakers = (List<Map<String, Object>>) payload.get("speakers");
    	
    	Room r = roomService.createRoom(name, description, maxUser, user, speakers);
    	
    	room = roomService.getRoom(r.getId());
    	
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", room);
        
        return result;
    }
    
    @RequestMapping(value = "/createCode", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> createCode(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
    	Map<String, Object> code = new HashMap<String, Object>();
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("roomId")||!payload.containsKey("roles")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	
    	int roomId = (int) payload.get("roomId");
    	List<String> roles = (List<String>) payload.get("roles");
    	String codeS = roomService.createCodeRoom(user.getId(), roomId, roles);
    	code.put("code", codeS);
    	result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", code);
        
        return result;
    }
    
    @RequestMapping(value = "/joinByCode", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> joinByCode(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
    	Map<String, Object> room = new HashMap<String, Object>();
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("code")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	
    	String code = (String) payload.get("code");
    	RoomUser ru = roomService.joinRoomByCode(user.getId(), code);
    	if(ru == null) {
    		result.put("code", 1);
    		result.put("message", "Code expired");
            
            return result;
    	}
    	
    	room = roomService.getRoom(ru.getRoom().getId());
    	result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", room);
        
        return result;
    }
    
    @RequestMapping(value = "/{roomId}", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> getRoom(@PathVariable int roomId, HttpServletRequest request) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> room = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(roomService.checkInRoom(roomId, user)) {
    		room = roomService.getRoom(roomId);    		
    	}
    	
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", room);
        
        return result;
    }
    
    @RequestMapping(value = "/add-members", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> addMembers(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String, Object> room = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("members")||!payload.containsKey("roomId")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	int roomId = Integer.valueOf((String) payload.get("roomId"));
    	List<Object> members = (List<Object>) payload.get("members");
    	
    	for(int i = 0; i < members.size(); ++i) {
    		Map<String, Object> member = (Map<String, Object>) members.get(i);
    		
    		List<String> roles = (List<String>) member.get("roles");
    		int usrId = Integer.valueOf((String) member.get("userId"));
    		roomService.addMemberRoom(roomId, usrId, roles, user.getId());
    	}
    	
    	
    	List<Map<String, Object>> listMember = null;
    	
    	if(roomService.checkInRoom(roomId, user)) {
    		listMember = roomService.getMemberRoom(roomId);
    	}
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", listMember);
        
        return result;
    	
    	
    }
    
    @RequestMapping(value = "/remove-members", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> removeMembers(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("members")||!payload.containsKey("roomId")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	
    	int roomId = (int) payload.get("roomId");
    	List<Object> members = (List<Object>) payload.get("members");
    	
    	for(int i = 0; i < members.size(); ++i) {
    		
    		roomService.removeMemberRoom(roomId, (int)members.get(i), user.getId());
    	}
    	
    	
    	List<Map<String, Object>> listMember = null;
    	
    	if(roomService.checkInRoom(roomId, user)) {
    		listMember = roomService.getMemberRoom(roomId);
    	}
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", listMember);
        
        return result;
    	
    }
    
    @RequestMapping(value = "/remove-room", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> removeRoom(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("roomId")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	
    	int roomId = (int) payload.get("roomId");
    	
    	Room room = roomService.removeRoom(roomId, user.getId());
    	
    	if(room ==null) {
    		if(!payload.containsKey("roomId")) {
        		result.put("code", 1);
        		result.put("message", "Not accesss room");
                
                return result;
        	}
    	}
    	Map<String, Object> r = new HashMap<String, Object>();
    	
    	r = roomService.getRoom(roomId);   
		result.put("data", r);
		
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
        
        return result;
    	
    }
    
    @RequestMapping(value = "/finish-room", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> finishRoom(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("roomId")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	
    	int roomId = (int) payload.get("roomId");
    	
    	Room room = roomService.finishRoom(roomId, user.getId());
    	
    	if(room ==null) {
    		if(!payload.containsKey("roomId")) {
        		result.put("code", 1);
        		result.put("message", "Not accesss room");
                
                return result;
        	}
    	}
    	Map<String, Object> r = new HashMap<String, Object>();
    	
    	r = roomService.getRoom(roomId);
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", r);
        
        return result;
    	
    }
    
    @RequestMapping(value = "/add-speakers", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> addSpeakers(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	if(!payload.containsKey("roomId")||!payload.containsKey("speakers")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	
    	int roomId = (int) payload.get("roomId");
    	List<Map<String, Object>> speakers = (List<Map<String, Object>>) payload.get("speakers");
    	
    	List<Map<String, Object>> roomSpeakers = roomService.addRoomSpeaker(roomId, user.getId(), speakers);
    	
        result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", roomSpeakers);
        
        return result;
    	
    }
    
    @RequestMapping(value = "/get-reporters", method = RequestMethod.GET, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> getReporters(HttpServletRequest request) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	String roomId = request.getParameter("roomId");
    	
    	if(roomId==null) {
    		result.put("code", 0);
    		result.put("message", HttpStatus.OK.name());
    		result.put("data", roomService.getReporters(user.getId()));
            return result;
    	} else {
    		result.put("code", 0);
    		result.put("message", HttpStatus.OK.name());
    		result.put("data", roomService.getReporters1(Integer.valueOf(roomId), user.getId()));
            return result;
    	}
    	
    }
    
    @RequestMapping(value = "/get-room-content", method = RequestMethod.POST, produces = { "application/json", "application/xml" })
    public @ResponseBody Map<String, ? extends Object> getRoomContent(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        
    	Map<String, Object> result = new HashMap<String, Object>();
    	User user = (User) request.getAttribute("user");
    	
    	
    	
    	if(!payload.containsKey("roomId")) {
    		result.put("code", 1);
    		result.put("message", "Parameter not validate");
            
            return result;
    	}
    	
    	int roomId = (int) payload.get("roomId");
    	
    	List<Map<String, Object>> contents = roomContentService.getListRoomContent(roomId, user);
    	
    	result.put("code", 0);
		result.put("message", HttpStatus.OK.name());
		result.put("data", contents);
        return result;
    	
    }
    
    


}
