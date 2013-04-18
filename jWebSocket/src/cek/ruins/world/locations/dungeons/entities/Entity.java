package cek.ruins.world.locations.dungeons.entities;

import java.util.List;
import java.util.Observable;

public class Entity extends Observable {
	private String id;
	private String templateId;
	private List<EntityComponent> components;
	
	public Entity() {
		this.setId("");
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
	}
}
