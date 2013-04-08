package cek.ruins.map;

import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;

public class PoliticalRegion implements Marshallable {
	private UUID ownerCivilization;
	private int currentNumberOfLocations;
	private int maxNumberOfLocations;

	int index;

	public PoliticalRegion() {
		this.ownerCivilization = null;
		this.currentNumberOfLocations = 0;
		this.maxNumberOfLocations = 0;

		this.index = -1;
	}

	public PoliticalRegion(int index) {
		this();

		this.index = index;
	}

	public UUID ownerCivilization() {
		return this.ownerCivilization;
	}

	public void setOwnerCivilization(UUID civId) {
		this.ownerCivilization = civId;
	}

	public int index() {
		return this.index;
	}
	
	public int currentNumberOfLocations() {
		return this.currentNumberOfLocations;
	}
	
	public void setCurrentNumberOfLocations(int numberOfLocations) {
		this.currentNumberOfLocations = numberOfLocations;
	}
	
	public int maxNumberOfLocations() {
		return this.maxNumberOfLocations;
	}
	
	public int increaseCurrentNumberOfLocations() {
		this.currentNumberOfLocations++;
		return this.currentNumberOfLocations;
	}
	
	public int decreaseCurrentNumberOfLocations() {
		this.currentNumberOfLocations = (this.currentNumberOfLocations == 0)? 0 : this.currentNumberOfLocations--;
		return this.currentNumberOfLocations;
	}
	
	public void setMaxNumberOfLocations(int maxNumberOfLocations) {
		this.maxNumberOfLocations = maxNumberOfLocations;
	}
	
	@Override
	public UUID id() {
		return null;
	}

	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		builder.add("owner", this.ownerCivilization);

		return builder.get();
	}
}
