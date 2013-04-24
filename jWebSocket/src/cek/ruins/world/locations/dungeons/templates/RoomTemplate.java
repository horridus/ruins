package cek.ruins.world.locations.dungeons.templates;

import org.mozilla.javascript.Script;

public class RoomTemplate {
	protected String id;
	protected Script roomGenerator;
	
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
}
