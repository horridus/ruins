package cek.ruins.world.locations.dungeons.templates;

import java.util.List;
import java.util.Map;

import org.mozilla.javascript.Script;

import cek.ruins.Point;

public class RoomTemplate {
	protected String id;
	protected Script roomGenerator;
	protected List<Character> map;
	protected Map<Character, String> roomMaterials;
	protected List<Point> exits;
	
	public RoomTemplate() {
		this.id = "";
		this.roomGenerator = null;
		this.map = null;
		this.roomMaterials = null;
		this.exits = null;
	}
	
	public String id() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Script roomGenerator() {
		return roomGenerator;
	}
	public void setRoomGenerator(Script roomGenerator) {
		this.roomGenerator = roomGenerator;
	}
	public void setMap(List<Character> map, Map<Character, String> roomMaterials, List<Point> exits) {
		this.map = map;
		this.roomMaterials = roomMaterials;
		this.exits = exits;
	}
	public List<Character> map() {
		return this.map;
	}
	public Map<Character, String> roomMaterials() {
		return this.roomMaterials;
	}
	public List<Point> exits() {
		return this.exits;
	}
}
