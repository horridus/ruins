package cek.ruins.world.locations.dungeons.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import cek.ruins.world.locations.dungeons.entities.components.ComponentMessageId;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class ObservableEntity extends Observable {
	private String id;
	private String templateId;
	private List<EntityComponent> components;
	private Map<String, Object> attributes;
	
	public ObservableEntity() {
		this.setId("");
		this.attributes = new HashMap<String, Object>();
		this.components = new ArrayList<EntityComponent>();
	}
	
	public String id() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String templateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	public void addComponent(EntityComponent component) {
		this.components.add(component);
		this.addObserver(component);
	}
	
	public DBObject statusToJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		
		builder.add("id", this.id);
		
		for (EntityComponent component : this.components) {
			component.statusToJSON(builder);
		}
		
		return builder.get();
	}
	
	public Object attribute(String key) {
		return this.attributes.get(key);
	}
	
	public void setAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}
	
	public boolean hasAttribute(String key) {
		return this.attributes.containsKey(key);
	}
	
	public void processMessage(ComponentMessageId messageId, Map<String, Object> args) {
		args.put("_messageid_", messageId); //FIXME si può fare meglio
		this.setChanged();
		this.notifyObservers(args);
	}
}
