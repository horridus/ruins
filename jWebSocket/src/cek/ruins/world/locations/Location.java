package cek.ruins.world.locations;

import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;

public class Location implements Marshallable {
	protected UUID id;
	protected String name;
	protected int x, y;
	protected int regionIndex;

	public Location() {
		this.id = UUID.randomUUID();

		this.name = "";
		this.x = -1;
		this.y = -1;
		this.regionIndex = -1;
	}

	public Location(String name, int x, int y, int regionIndex) {
		this.id = UUID.randomUUID();

		this.name = name;
		this.x = x;
		this.y = y;
		this.regionIndex = regionIndex;
	}

	public int x() {
		return this.x;
	}
	public int y() {
		return this.y;
	}

	public int regionIndex() {
		return this.regionIndex;
	}

	public String name() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public UUID id() {
		return this.id;
	}

	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		builder.add("id", this.id().toString());
		builder.add("name", this.name);
		builder.add("x", this.x);
		builder.add("y", this.y);
		builder.add("regionIndex", this.regionIndex);

		return builder.get();
	}
}
