package org.spring.websocket.endpoint;


import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

/**
 * Endpoint to expose WebSocket stats
 *
 */
@Endpoint(id = "websocketstats")
public class WebSocketStatsEndpoint {

	private WebSocketMessageBrokerStats webSocketMessageBrokerStats;
	
	public WebSocketStatsEndpoint(WebSocketMessageBrokerStats webSocketMessageBrokerStats) {
		this.webSocketMessageBrokerStats = webSocketMessageBrokerStats;
	}

	@ReadOperation
	public WebSocketMessageBrokerStats stats() {
		return webSocketMessageBrokerStats;
	}
}
