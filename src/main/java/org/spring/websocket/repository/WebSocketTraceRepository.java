package org.spring.websocket.repository;

import java.util.List;

import org.spring.websocket.autoconfigure.WebSocketTrace;
import org.springframework.boot.actuate.trace.http.HttpTrace;

/**
 * A repository for {@link WebSocketTrace}s.
 *
 */
public interface WebSocketTraceRepository {
	/**
	 * Find all {@link HttpTrace} objects contained in the repository.
	 * @return the results
	 */
	List<WebSocketTrace> findAll();

	/**
	 * Adds a trace to the repository.
	 * @param trace the trace to add
	 */
	void add(WebSocketTrace trace);
}