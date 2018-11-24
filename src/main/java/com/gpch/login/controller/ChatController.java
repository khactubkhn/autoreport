package com.gpch.login.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gpch.login.model.ChatMessage;
import com.gpch.login.model.ChatMessage.MessageType;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.service.JwtService;
import com.gpch.login.service.RoomContentService;
import com.gpch.login.service.RoomService;
import com.gpch.login.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class ChatController {
	@Autowired
    private UserService userService;
	
	@Autowired
    private RoomService roomService;
	
	@Autowired
	private RoomContentService roomContentService;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private SimpMessagingTemplate template;

	
    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/hello")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    	User user = (User) headerAccessor.getSessionAttributes().get("user");
    	
    	if(user != null) {
    		Map<String, Object> data = chatMessage.getData();
    		
    		int roomId = (int) data.get("roomId");
    		if(roomService.checkInRoom(roomId, user)) {
    			if(data.containsKey("speakerId")&&data.containsKey("content")&&data.containsKey("startTime")&&
    					data.containsKey("endTime")) {
    				
    				Map<String, Object> dataMessage = roomContentService.writeRoomContent(user, roomId, (int)data.get("speakerId"), (String)data.get("content"), (long)data.get("startTime"), (long)data.get("endTime"));
    				
    				ChatMessage message = new ChatMessage();
	  	    		  message.setType(MessageType.CHAT);
	  	    		message.setData(dataMessage);
    				this.template.convertAndSend("/topic/"+roomId, message);
    			}
    			
    		}
    	}
    	
    	return chatMessage;
    }

    @MessageMapping("/chat.addUser")
//    @SendTo("/topic/hello")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor, StompHeaderAccessor stompHeaderAccessor) {
        // Add username in web socket session
    	Map<String, Object> data = chatMessage.getData();
    	String authorization = (String) data.get("authorization");

    	 if (jwtService.validateTokenLogin(authorization)) {
    	      String username = jwtService.getUsernameFromToken(authorization);
    	      User user = userService.loadUserByUsername(username);
    	      if (user != null) {
    	    	  headerAccessor.getSessionAttributes().put("user", user);
    	    	  Set<RoomUser> roomUsers = user.getMemberRooms();
    	    	  for(RoomUser ru: roomUsers) {
    	    		  headerAccessor.setSubscriptionId("/topic/"+String.valueOf(ru.getRoom().getId()));
    	    		  Map<String, Object> dataMessage = new HashMap<String, Object>();
    	    		  Map<String, Object> infor = new HashMap<String, Object>();
    	    		  ChatMessage message = new ChatMessage();
    	    		  message.setType(MessageType.NOTIFY);
    	    		  dataMessage.put("type", "JOINED");
    	    		  infor.put("userId", user.getId());
    	    		  infor.put("firstName", user.getFirstName());
    	    		  infor.put("lastName", user.getLastName());
    	    		  infor.put("username", user.getUsername());
    	    		  infor.put("time", new Timestamp(new Date().getTime()));
    	    		  dataMessage.put("information", infor);
    	    		  message.setData(dataMessage);
    	    		  this.template.convertAndSend("/topic/"+ru.getRoom().getId(), message);
    	    	  }
    	      }
    	    }
        return chatMessage;
    }

}
