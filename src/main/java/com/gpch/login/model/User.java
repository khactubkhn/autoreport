package com.gpch.login.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;
import java.util.ArrayList;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -1068445791804691951L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private int id;
    
    @Column(name = "username")
    @NotEmpty(message = "*Please provide an username")
    private String username;
    
    @Column(name = "password")
    @Length(min = 5, message = "*Your password must have at least 5 characters")
    @NotEmpty(message = "*Please provide your password")
    private String password;
    
    @Column(name = "first_name")
    @NotEmpty(message = "*Please provide your first name")
    private String firstName;
    
    @Column(name = "last_name")
    @NotEmpty(message = "*Please provide your last name")
    private String lastName;
    
    @Column(name = "phone")
    @Length(min = 10, max = 11, message = "*Your phone must have at least 10 characters and less 11 characters")
    @NotEmpty(message = "*Please provide your phone")
    private String phone;
    
    @Column(name = "active")
    private int active;
    
    @Column(name = "created_by")
    private int createdBy;
    
    @Column(name = "created_dtg")
    private Timestamp createdDTG;
    
    @Column(name = "updated_by")
    private int updatedBy;
    
    @Column(name = "updated_dtg")
    private Timestamp updatedDTG;

    @ManyToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="created_by", referencedColumnName="user_id")
    private Set<Room> ownRooms;
    
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinColumn(name="user_id", referencedColumnName="user_id")
    private Set<RoomUser> memberRooms;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
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

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Room> getOwnRooms() {
		return ownRooms;
	}

	public void setOwnRooms(Set<Room> ownRooms) {
		this.ownRooms = ownRooms;
	}

	public Set<RoomUser> getMemberRooms() {
		return memberRooms;
	}

	public void setMemberRooms(Set<RoomUser> memberRooms) {
		this.memberRooms = memberRooms;
	}
	
	public List<GrantedAuthority> getAuthorities() {
	    List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	    for (Role role : roles) {
	    	
	      authorities.add(new SimpleGrantedAuthority(role.getRole()));
	    }
	    return authorities;
	  }
	
}
