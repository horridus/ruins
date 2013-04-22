package cek.ruins.world.locations.dungeons;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cek.ruins.events.Event;
import cek.ruins.events.EventsDispatcher;
import cek.ruins.events.WorldEventsDispatcher;
import cek.ruins.world.locations.dungeons.entities.Entity;

public class DungeonMaster {
	private List<EventsDispatcher> dispatchers;
	private Map<String, Entity> bredEntities;
	
	public DungeonMaster() {
		this.bredEntities = new HashMap<String, Entity>();
	}
	
	public void addEntity(Entity entity) {
		//insert new entity in existing entities map
		this.bredEntities.put(entity.id(), entity);
	}
	
	public void deleteEntity(String id) {
		this.bredEntities.remove(id);
	}
	
	public void dispatch(Event event) {
		Iterator<EventsDispatcher> dispatchersIt = this.dispatchers.iterator();
		while (dispatchersIt.hasNext()) {
			dispatchersIt.next().publish(event);
		}
	}

	public void registerDispatcher(WorldEventsDispatcher dispatcher) {
		this.dispatchers.add(dispatcher);
	}
	
	public void clearDispatchers() {
		this.dispatchers.clear();
	}
}
