package cek.ruins.world.locations.dungeons.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class Entity extends Observable {
	private String id;
	private String templateId;
	private List<EntityComponent> components;
	private Map<String, Object> attributes;
	
	public Entity() {
		this.setId("");
		this.attributes = new HashMap<String, Object>();
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
}
