package cek.ruins.world.civilizations;

import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;

public class Civilization implements Marshallable {
	public static final String NAMES_GRAMMAR = "civilization";

	private UUID id;
	private String species;
	private String name;
	private Float growthRate;
	private Coat coat;
	
	public Civilization(CivilizationTemplate template) {
		this.id = UUID.randomUUID();
		this.species = template.species();
		this.name = "";
		this.growthRate = template.growthRate();
	}

	public Float growthRate() {
		return growthRate;
	}
	public void setGrowthRate(Float growthRate) {
		this.growthRate = growthRate;
	}
	public String species() {
		return species;
	}
	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Coat coat() {
		return this.coat;
	}
	
	public void setCoat(Coat coat) {
		this.coat = coat;
	}

	@Override
	public UUID id() {
		return this.id;
	}

	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		builder.add("id", this.id.toString());
		builder.add("species", this.species);
		builder.add("name", this.name);
		builder.add("coat", this.coat.toJSON());

		return builder.get();
	}

}
