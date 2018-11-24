package com.gpch.login.model;

import java.util.Map;

public class ChatMessage {
    private MessageType type;
    private Map<String, Object> data;

    public enum MessageType {
        CHAT,
        JOIN,
        NOTIFY,
        LEAVE
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
}
