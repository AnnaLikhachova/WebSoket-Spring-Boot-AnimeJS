package com.webchat.event;

import java.util.Date;

/**
 * 
 * @author Anna Likhachova
 */
public class UserLoggedInEvent {

	private String username;
	private Date time;

	public UserLoggedInEvent(String username) {
		this.username = username;
		time = new Date();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
