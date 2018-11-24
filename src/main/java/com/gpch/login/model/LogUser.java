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
@Table(name = "log_user")
public class LogUser implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "log_user_id")
    private int id;
    
    @Column(name = "after_content")
    private String after;
    
    @Column(name = "before_content")
    private String before;
    
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

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
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
    
}
