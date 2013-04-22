package cek.ruins.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.token.Token;

import cek.ruins.world.locations.Cave;
import cek.ruins.world.locations.Road;
import cek.ruins.world.locations.Settlement;

public class WorldEventsDispatcher extends EventsDispatcher {
	private List<WebSocketConnector> subscribers;

	public WorldEventsDispatcher() {
		this.subscribers = new ArrayList<WebSocketConnector>();
	}

	public void registerSubscriber(WebSocketConnector connector) {
		this.subscribers.add(connector);
	}

	public void unregisterSubscriber(WebSocketConnector connector) {
		this.subscribers.remove(connector);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void process(Event event, Token token) {
		switch (event.name) {
		case EventName.NEW_LOCATION_EVENT:
		{
			String locationType = (String) event.data.get("locationType");
			token.setString("locationType", locationType);

			if (locationType.equals("settlement")) {
				Settlement newSettlement = (Settlement) event.data.get("location");
				token.setString("location", newSettlement.toJSON().toString());
			}
			else if (locationType.equals("cave")) {
				Cave cave = (Cave) event.data.get("location");
				token.setString("location", cave.toJSON().toString());
			}

			break;
		}
		case EventName.UPDATE_LOCATIONS_EVENT:
		{
			token.setMap("settlements", (Map<String, Object>) event.data.get("settlements"));
			break;
		}
		case EventName.DELETE_LOCATION_EVENT:
		{
			String locationId = event.data.get("locationId").toString();
			String locationType = (String) event.data.get("locationType");

			token.setString("locationId", locationId);
			token.setString("locationType", locationType);
			break;
		}
		case EventName.DATE_CHANGE_EVENT:
		{
			token.setMap("date", event.data);
			break;
		}
		case EventName.UPDATE_REGIONS_EVENT:
		{
			token.setMap("political", (Map<Integer, Object>) event.data.get("political"));
			break;
		}
		case EventName.NEW_ROAD_EVENT:
		{
			Road road = (Road) event.data.get("road");
			token.setString("road", road.toJSON().toString());
			break;
		}
		}
	}
}
