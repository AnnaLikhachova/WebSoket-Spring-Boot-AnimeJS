package com.webchat.model;

import com.webchat.exception.BannedUsersException;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Anna Likhachova
 */
public class BannedUser {

	private final static long MAX_VALUE = 100;

	private long maxLevel = MAX_VALUE;
	
	private AtomicLong level = new AtomicLong();
	
	public BannedUser() {}
	
	public BannedUser(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	
	public void increment(long partial) {
		if(level.intValue() + partial >= maxLevel) {
			level.set(maxLevel);
			throw new BannedUsersException("You are banned");
		}

		level.addAndGet(partial);
	}
}
