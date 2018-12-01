package com.gpch.login.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.gpch.login.model.ChatMessage;
import com.gpch.login.model.ChatMessage.MessageType;
import com.gpch.login.model.FileSave;
import com.gpch.login.model.RoomUser;
import com.gpch.login.model.User;
import com.gpch.login.service.FileService;
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

	@Autowired
    FileService fileService;
	
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
    				roomService.updateRoomContentMerge(roomId, user.getId());
    				roomService.updateRoomTranscript(roomId, user.getId());
    			
    				message  = new ChatMessage();
    				message.setType(MessageType.PULL_TRANSCRIPT);
    				this.template.convertAndSend("/topic/"+(int)data.get("roomId"), message);
    			}
    			
    		}
    	}
    	
    	return chatMessage;
    }
    
    @MessageMapping("/chat.sendFile")
//  @SendTo("/topic/hello")
  public ChatMessage sendFile(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
  	User user = (User) headerAccessor.getSessionAttributes().get("user");
  	
  	if(user != null) {
  		Map<String, Object> data = chatMessage.getData();
  		
  		int roomId = (int) data.get("roomId");
  		if(roomService.checkInRoom(roomId, user)) {
  			if(data.containsKey("fileSaveId")) {
  				int fileSaveId = (int) data.get("fileSaveId");
  				Map<String, Object> dataMessage = new HashMap<String, Object>();
  				Map<String, Object> newFile1 = new HashMap<String, Object>();
  				Map<String, Object> user1 = new HashMap<String, Object>();
  				User u = userService.findUserByUsername(user.getUsername());
  				
  				FileSave newFile = fileService.getFileSaveById(fileSaveId);
  				newFile1.put("id", newFile.getId());
  				newFile1.put("name", newFile.getName());
  				newFile1.put("CreatedDTG", newFile.getCreatedDTG());
  				
  				user1.put("userId", u.getId());
  				user1.put("firstName", u.getFirstName());
  				user1.put("lastName", u.getLastName());
  				user1.put("username", u.getUsername());
  				
  				dataMessage.put("newFile", newFile1);
  				dataMessage.put("user", user1);
  				
  				ChatMessage message = new ChatMessage();
	  	    		  message.setType(MessageType.ADD_FILE);
	  	    		message.setData(dataMessage);
  				this.template.convertAndSend("/topic/"+roomId, message);
  				roomService.updateRoomFileContent(roomId, user.getId());
				roomService.updateRoomTranscript(roomId, user.getId());
				
				message  = new ChatMessage();
				message.setType(MessageType.PULL_TRANSCRIPT);
				this.template.convertAndSend("/topic/"+(int)data.get("roomId"), message);
				
				message  = new ChatMessage();
				message.setType(MessageType.PULL_SPEAKER);
				this.template.convertAndSend("/topic/"+(int)data.get("roomId"), message);
  			
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
    
    @MessageMapping("/chat.notify")
  public ChatMessage notifyRoom(@Payload ChatMessage chatMessage,
                             SimpMessageHeaderAccessor headerAccessor, StompHeaderAccessor stompHeaderAccessor) {
      // Add username in web socket session
  	Map<String, Object> data = chatMessage.getData();
  	
  	      
  	User user = (User) headerAccessor.getSessionAttributes().get("user");
  	      ChatMessage message = new ChatMessage();
  	      Boolean rs = false;
  	      if (user != null) {
  	    	  headerAccessor.getSessionAttributes().put("user", user);
  	    	  
  	    	  switch (chatMessage.getType().toString()) {
				case "EDITING" :
					rs = roomService.setEditingRoomTranscript((int)data.get("transcriptId"), user.getId());
					if(rs) {
						message.setType(MessageType.PULL_TRANSCRIPT);
					}
					
					break;
				case "REMOVE_EDITING" :
					rs = roomService.removeEditingRoomTranscript((int)data.get("transcriptId"), user.getId());
					if(rs) {
						message.setType(MessageType.PULL_TRANSCRIPT);
					}
					
					break;
				case "PULL_SPEAKER" :
					message.setType(MessageType.PULL_SPEAKER);
					
					break;
					
				case "EDIT" :
			    	
			    	rs = roomService.editTranscript((int) data.get("transcriptId"), (String) data.get("content"), user.getId());
			    	if(rs) {
			    		roomService.removeEditingRoomTranscript((int)data.get("transcriptId"), user.getId());
			    		message.setType(MessageType.PULL_TRANSCRIPT);
			    	}
					
					break;
				case "JUMP_TRANSCRIPT" :
			    	
			    	rs = roomService.jumpTranscript((int) data.get("transcriptId"), (int) data.get("index"), user.getId());
			    	if(rs) {
			    		roomService.removeEditingRoomTranscript((int)data.get("transcriptId"), user.getId());
			    		message.setType(MessageType.PULL_TRANSCRIPT);
			    	}
					
					break;
	
				default:
					break;
				}
  	    	  
  	    	this.template.convertAndSend("/topic/"+(int)data.get("roomId"), message);
  	      }
  	    
      return chatMessage;
  }

}