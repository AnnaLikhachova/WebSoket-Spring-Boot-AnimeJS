package com.webchat.event;

/**
 * 
 * @author Anna Likhachova
 */
public class UserLoggedOutEvent {
	
	private String username;

	public UserLoggedOutEvent(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
