package com.webchat.model;

import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author Anna Likhachova
 */

@ConfigurationProperties(prefix = "chat")
public class ChatProperties {

	private int maxProfanityLevel;
	
	private Set<String> disallowedWords;
	
	private Destinations destinations;
	
	
	public int getMaxProfanityLevel() {
		return maxProfanityLevel;
	}

	public void setMaxProfanityLevel(int maxProfanityLevel) {
		this.maxProfanityLevel = maxProfanityLevel;
	}
	
	public Set<String> getDisallowedWords() {
		return disallowedWords;
	}

	public void setDisallowedWords(Set<String> disallowedWords) {
		this.disallowedWords = disallowedWords;
	}

	public Destinations getDestinations() {
		return destinations;
	}

	public void setDestinations(Destinations destinations) {
		this.destinations = destinations;
	}


	public static class Destinations {

	    @Value("${chat.destinations.login}")
		private String login;
        @Value("${chat.destinations.logout}")
		private String logout;

		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public String getLogout() {
			return logout;
		}

		public void setLogout(String logout) {
			this.logout = logout;
		}
	}
}
