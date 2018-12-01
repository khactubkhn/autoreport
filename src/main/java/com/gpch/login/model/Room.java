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
@Table(name = "room")
public class Room implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "room_id")
    private int id;
    
    @Column(name = "code")
    private String code;
    
    @Column(name = "name")
    @Length(min = 5, message = "*Your name must have at least 10 characters")
    @NotEmpty(message = "*Please provide room name")
    private String name;
    
    @Column(name = "description")
    @Length(min = 10, message = "*Your name must have at least 10 characters")
    @NotEmpty(message = "*Please provide room description")
    private String description;
    
    @Column(name = "max_user")
    private int maxUser;
    
    @Column(name = "number")
    private int number;
    
    @Column(name = "active")
    private int active;
    
    @Column(name = "deleted")
    private int deleted;
    
    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName="user_id")
    private User user;
    
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="room_id")
    private Set<RoomUser> memberRooms;
    
    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="room_id")
    private Set<RoomSpeaker> roomSpeakers;
    
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMaxUser() {
		return maxUser;
	}

	public void setMaxUser(int maxUser) {
		this.maxUser = maxUser;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<RoomUser> getMemberRooms() {
		return memberRooms;
	}

	public void setMemberRooms(Set<RoomUser> memberRooms) {
		this.memberRooms = memberRooms;
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

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public Set<RoomSpeaker> getRoomSpeakers() {
		return roomSpeakers;
	}

	public void setRoomSpeakers(Set<RoomSpeaker> roomSpeakers) {
		this.roomSpeakers = roomSpeakers;
	}
	
}
