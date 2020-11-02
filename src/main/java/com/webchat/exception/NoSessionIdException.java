package com.webchat.exception;

/**
 * 
 * @author Anna Likhachova
 */

public class NoSessionIdException extends RuntimeException {

	public NoSessionIdException(String message) {
		super(message);
	}
}
