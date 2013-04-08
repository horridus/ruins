package cek.ruins.world.environment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.mongodb.DBObject;

import cek.ruins.Marshallable;

public class Resource implements Marshallable {
	private String resourceId;
	private String name;
	private Map<String, Map<String, Object>> effects;

	public Resource() {
		this.resourceId = "";
		this.name = "";
		this.effects = new HashMap<String, Map<String,Object>>();
	}

	public String resourceId() {
		return this.resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String name() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, Map<String, Object>> effects() {
		return effects;
	}
	public Map<String, Object> effects(String effect) {
		return effects.get(effect);
	}
	public void setEffects(Map<String, Map<String, Object>> effects) {
		this.effects = effects;
	}

	@Override
	public UUID id() {
		return null;
	}

	@Override
	public DBObject toJSON() {
		return null;
	}
}
