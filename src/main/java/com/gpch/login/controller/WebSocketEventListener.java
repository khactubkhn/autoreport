package com.gpch.login.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gpch.login.model.ChatMessage;
import com.gpch.login.model.User;
import com.gpch.login.service.RoomService;
import com.gpch.login.service.UserService;

import org.aspectj.weaver.ast.HasAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private RoomService roomService;
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
    	Message<byte[]> a = event.getMessage();
    	MessageHeaders b = a.getHeaders();
    	
    	logger.info("Received a new web socket connection test:: " + a.getPayload().length);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        User user = (User) headerAccessor.getSessionAttributes().get("user");
        if(user != null) {
            logger.info("User Disconnected : " + user.getUsername());
            
            List<Integer> roomIdsUpdate = roomService.DiscontentRemoveTranscriptEditing(user.getId());
            
            
            Map<String, Object> data = new HashMap<String, Object>();
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.PULL_TRANSCRIPT);
            chatMessage.setData(data);

            roomIdsUpdate.forEach(roomId -> {
            	messagingTemplate.convertAndSend("/topic/"+roomId, chatMessage);            	
            });
        }
    }
}
