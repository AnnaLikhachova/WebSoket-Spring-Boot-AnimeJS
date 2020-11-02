package com.webchat.exception;

/**
 * 
 * @author Anna Likhachova
 */

public class BannedUsersException extends RuntimeException {

	public BannedUsersException(String message) {
		super(message);
	}
}
