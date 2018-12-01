package com.gpch.login.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room_content")
public class RoomContent implements Serializable, Comparable<RoomContent>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "room_content_id")
    private int id;
    
    @Column(name = "speaker_id")
    private int speakerId;
    
    @Column(name = "content")
    @NotEmpty(message = "*Please provide content message")
    private String content;
    
    @Column(name = "start")
    private Timestamp start;
    
    @Column(name = "end")
    private Timestamp end;
    
    @Column(name = "room_id")
    private int roomId;
    
    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName="user_id")
    private User user;
    
    @Column(name = "created_dtg")
    private Timestamp createdDTG;
    
    @Column(name = "updated_by")
    private int updatedBy;
    
    @Column(name = "updated_dtg")
    private Timestamp updatedDTG;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	

	public int getSpeakerId() {
		return speakerId;
	}

	public void setSpeakerId(int speakerId) {
		this.speakerId = speakerId;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getStart() {
		return start;
	}

	public void setStart(Timestamp start) {
		this.start = start;
	}

	public Timestamp getEnd() {
		return end;
	}

	public void setEnd(Timestamp end) {
		this.end = end;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Timestamp getCreatedDTG() {
		return createdDTG;
	}

	public void setCreatedDTG(Timestamp createdDTG) {
		this.createdDTG = createdDTG;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Timestamp getUpdatedDTG() {
		return updatedDTG;
	}

	public void setUpdatedDTG(Timestamp updatedDTG) {
		this.updatedDTG = updatedDTG;
	}

	@Override
	public int compareTo(RoomContent o) {
		if(this.speakerId == o.getSpeakerId()) {
			if(this.start.getTime() == o.getStart().getTime()) {
				return 0;
			}else if(this.start.getTime() > o.getStart().getTime()) {
				return 1;
			}else return -1;
		}else if(this.speakerId > o.getSpeakerId()) {
			return 1;
		}else 
			return -1;
	}
    
}
