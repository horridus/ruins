package cek.ruins.world.locations;

import java.util.UUID;

import cek.ruins.world.locations.settlements.SettlementDistrict;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class Settlement extends Location {
	public static int MAX_DISTRICTS_MAP_SIZE = 10; 
	private UUID ownerCivililizationId;
	
	private float population;
	private float growthRate;
	
	private int level;
	private float support;
	private float economy;
	
	private Table<Integer, Integer, SettlementDistrict> districts;

	public Settlement() {
		super();

		this.ownerCivililizationId = null;
		this.population = 0;
		this.growthRate = 0.0f;
		this.level = 0;
		this.support = 0.0f;
		this.economy = 0.0f;
		
		this.districts = HashBasedTable.create(Settlement.MAX_DISTRICTS_MAP_SIZE, Settlement.MAX_DISTRICTS_MAP_SIZE);
	}

	public Settlement(String name, int x, int y, int regionIndex, UUID ownerCivililizationId) {
		super(name, x, y, regionIndex);

		this.ownerCivililizationId = ownerCivililizationId;
		this.population = 0;
		this.growthRate = 0.0f;
		this.level = 0;
		this.support = 0.0f;
		this.economy = 0.0f;
		
		this.districts = HashBasedTable.create(Settlement.MAX_DISTRICTS_MAP_SIZE, Settlement.MAX_DISTRICTS_MAP_SIZE);
	}

	public UUID ownerCivilizationId() {
		return this.ownerCivililizationId;
	}

	public float population() {
		return this.population;
	}

	public void setPopulation(float population) {
		this.population = population;
	}

	public float growthRate() {
		return this.growthRate;
	}

	public void setGrowthRate(float growthRate) {
		this.growthRate = growthRate;
	}

	public int level() {
		return this.level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public float support() {
		return this.support;
	}
	
	public void setSupport(float support) {
		this.support = support;
	}
	
	public float economy() {
		return this.economy;
	}
	
	public void setEconomy(float economy) {
		this.economy = economy;
	}
	
	public void setDistrict(int x, int y, SettlementDistrict district) {
		this.districts.put(x, y, district);
	}
	
	public SettlementDistrict district(int x, int y) {
		return this.districts.get(x, y);
	}
	
	@Override
	public DBObject toJSON() {
		DBObject location = super.toJSON();

		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start(location.toMap());

		builder.add("ownerCivililizationId", this.ownerCivililizationId.toString());
		builder.add("population", this.population);
		builder.add("level", this.level);
		
		//builder.add("support", this.support);
		//builder.add("economy", this.economy);

		return builder.get();
	}
}
