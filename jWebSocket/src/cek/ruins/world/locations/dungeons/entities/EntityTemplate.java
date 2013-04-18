package cek.ruins.world.locations.dungeons.entities;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class EntityTemplate {
	private String id;
	private Map<String, Document> components;
	
	public EntityTemplate() {
		this.id = "";
		this.components = new HashMap<String, Document>();
	}
	
	public void addComponent(String clazz, Element componentConfig) {
		Document config = DocumentHelper.createDocument(componentConfig.createCopy());
		this.components.put(clazz, config);
	}
	
	public void removeComponent(String clazz) {
		this.components.remove(clazz);
	}

	public String id() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Document> components() {
		return this.components;
	}
}
