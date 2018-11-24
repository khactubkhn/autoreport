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
@Table(name = "room_user")
public class RoomUser implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "room_user_id")
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(name = "room_user_role", joinColumns = @JoinColumn(name = "room_user_id"), inverseJoinColumns = @JoinColumn(name = "room_role_id"))
    private Set<RoomRole> roles;
    
    @Column(name = "deleted")
    private int deleted;
    
    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName="user_id")
    private User userCreated;
    
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

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<RoomRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<RoomRole> roles) {
		this.roles = roles;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	public User getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(User userCreated) {
		this.userCreated = userCreated;
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
    
}
