package cek.ruins.world.civilizations;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;

public class CivilizationTemplate implements Marshallable {
	private int index;
	private String species;
	private String iconsFolder;
	private Float growthRate;
	private List<String> preferredBiomes;
	
	public CivilizationTemplate() {
		this.index = -1;
		this.species = "";
		this.growthRate = 0.0f;
		this.preferredBiomes = new LinkedList<String>();
	}

	public int index() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String species() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public String iconsFolder() {
		return this.iconsFolder;
	}
	public void setIconsFolder(String iconsFolder) {
		this.iconsFolder = iconsFolder;
	}
	public Float growthRate() {
		return growthRate;
	}
	public void setGrowthRate(Float growthRate) {
		this.growthRate = growthRate;
	}
	
	public void addPreferredBiome(String biomeName) {
		this.preferredBiomes.add(biomeName);
	}
	public boolean isPreferredBiome(String biomeName) {
		return this.preferredBiomes.contains(biomeName);
	}
	
	@Override
	public UUID id() {
		return null;
	}
	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		
		builder.add("id", this.index);
		builder.add("species", this.species);
		builder.add("iconsFolder", this.iconsFolder);
		
		return builder.get();
	}
}
