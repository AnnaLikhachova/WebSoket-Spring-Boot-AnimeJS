package com.webchat.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filter for banning
 * 
 * @author Anna Likhachova
 */
public class BannedUserFilter {

	private Set<String> banned = new HashSet<>();

	public long getMessageToBan(String message) {
		return Arrays.stream(message.split(" "))
				.filter(word -> banned.contains(word))
				.count();
	}

	public String filter(String message) {
		return Arrays.stream(message.split(" "))
				.filter(word -> !banned.contains(word))
				.collect(Collectors.joining(" "));
	}

	public Set<String> getBannedUser() {
		return banned;
	}

	public void setBannedUser(Set<String> banned) {
		this.banned = banned;
	}
}
