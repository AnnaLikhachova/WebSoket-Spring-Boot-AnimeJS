package org.spring.websocket.endpoint;

import java.util.List;

import org.spring.websocket.autoconfigure.WebSocketTrace;
import org.spring.websocket.repository.WebSocketTraceRepository;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.util.Assert;

/**
 * {@link Endpoint} to expose WebSocket traces
 *
 */
@Endpoint(id = "websockettrace")
public class WebSocketTraceEndpoint {

	private final WebSocketTraceRepository repository;

	public WebSocketTraceEndpoint(WebSocketTraceRepository repository) {
		Assert.notNull(repository, "Repository must not be null");
		this.repository = repository;
	}
	
	@ReadOperation
	public WebSocketTraceDescriptor traces() {
		return new WebSocketTraceDescriptor(this.repository.findAll());
	}

	/**
	 * A description of an application's {@link WebSocketTrace} entries. Primarily intended for
	 * serialization to JSON.
	 */
	static final class WebSocketTraceDescriptor {

		private final List<WebSocketTrace> traces;

		private WebSocketTraceDescriptor(List<WebSocketTrace> traces) {
			this.traces = traces;
		}

		public List<WebSocketTrace> getTraces() {
			return this.traces;
		}
	}
}
