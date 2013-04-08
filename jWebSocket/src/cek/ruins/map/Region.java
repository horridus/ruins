package cek.ruins.map;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;
import cek.ruins.world.environment.Biome;

public class Region implements Marshallable {
	boolean water;
	boolean ocean;
	boolean coast;
	double moisture;
	double elevation;
	double temperature;
	Biome biome;
	List<String> principalResources;

	int index;

	public Region() {
		this.water = false;
		this.ocean = false;
		this.coast = false;
		this.elevation = 0.0;
		this.temperature = 0.0;
		this.biome = Biome.grassland;
		this.principalResources = new ArrayList<String>();

		this.index = -1;
	}

	public Region(int index) {
		this();

		this.index = index;
	}

	public boolean isWater() {
		return this.water;
	}

	public boolean isOcean() {
		return this.ocean;
	}

	public boolean isCoast() {
		return this.coast;
	}

	public double moisture() {
		return this.moisture;
	}

	public double temperature() {
		return this.temperature;
	}

	public Biome biome() {
		return this.biome;
	}

	public double elevation() {
		return this.elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	public void addPrincipalResource(String resourceId) {
		this.principalResources.add(resourceId);
	}

	public List<String> principalResources() {
		return this.principalResources;
	}

	public int index() {
		return this.index;
	}

	@Override
	public UUID id() {
		return null;
	}

	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		builder.add("biome", this.biome.toString());
		builder.add("water", this.water);
		builder.add("ocean", this.ocean);
		builder.add("coast", this.coast);
		builder.add("moisture", this.moisture);
		builder.add("elevation", this.elevation);
		builder.add("temperature", this.temperature);

		BasicDBList jsonList = new BasicDBList();
		jsonList.addAll(this.principalResources);
		builder.add("principalResources", jsonList);

		builder.add("index", this.index);

		return builder.get();
	}
}
