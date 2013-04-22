package cek.ruins.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

import cek.ruins.configuration.Configuration;

public abstract class EventsDispatcher {
	private List<WebSocketConnector> subscribers;

	public EventsDispatcher() {
		this.subscribers = new ArrayList<WebSocketConnector>();
	}

	public void registerSubscriber(WebSocketConnector connector) {
		this.subscribers.add(connector);
	}

	public void unregisterSubscriber(WebSocketConnector connector) {
		this.subscribers.remove(connector);
	}
	
	protected abstract void process(Event event, Token token);
	
	public void publish(Event event) {
		Token token = TokenFactory.createToken(event.namespace, "event");
		token.setInteger("name", event.name);
		
		process(event, token);

		Iterator<WebSocketConnector> subrscribersIt = this.subscribers.iterator();
		while (subrscribersIt.hasNext()) {
			WebSocketConnector subscriber = subrscribersIt.next();
			TokenServer server = (TokenServer) subscriber.getEngine().getServers().get(Configuration.TOKEN_SERVER_ID);

			server.sendToken(subscriber, token);
		}
	}
}
