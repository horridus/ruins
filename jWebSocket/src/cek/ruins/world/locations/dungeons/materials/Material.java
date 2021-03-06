package cek.ruins.world.locations.dungeons.materials;

import org.mozilla.javascript.ScriptableObject;

public class Material extends ScriptableObject {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private int x,y;
	private String name;
	private boolean isPassable;
	
	public Material(String id, int x, int y) {
		this();
		
		this.id = id;
		this.x = x;
		this.y = y;
	}

	public String id() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int x() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int y() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Material) {
			Material material = (Material) obj;
			return this.id.equals(material.id);
		}
		else
			return false;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isPassable() {
		return this.isPassable;
	}
	
	public void setPassable(boolean passable) {
		this.isPassable = passable;
	}

	/* javascript side methods */
	
	@Override
	public String getClassName() {
		return Material.class.getSimpleName();
	}
	
	public Material() {
		this.id = "";
		this.x = this.y = 0;
		this.name = "";
		this.isPassable = false;
	}
	
	public void jsConstructor(String id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
}
