package cek.ruins.world.locations;

import java.util.List;
import java.util.Random;

import cek.ruins.Point;
import cek.ruins.bookofnames.BookOfNames;
import cek.ruins.map.Map;
import cek.ruins.world.civilizations.Civilization;
import cek.ruins.world.locations.settlements.Architect;

public class LocationsBuilder {
	private Map map;
	private Random generator;
	private BookOfNames bookOfNames;

	public LocationsBuilder(Map map, Random generator, BookOfNames bookOfNames) {
		this.map = map;
		this.generator = generator;
		this.bookOfNames = bookOfNames;
	}

	public Settlement buildSettlement(Civilization civilization, int regionIdx, float startingPopulation, Integer x, Integer y, Architect architect) throws Exception {
		String name = this.bookOfNames.generate("settlement");

		Settlement settlement = new Settlement(name, (int)x, (int)y, regionIdx, civilization.id());

		settlement.setPopulation(startingPopulation);
		settlement.setGrowthRate(civilization.growthRate());
		
		//keep track of number of locations in region
		this.map.politicalRegions().get(regionIdx).increaseCurrentNumberOfLocations();
		
		//build first district in settlement
		settlement = architect.initSettlement(settlement, civilization, this.generator);

		return settlement;
	}

	public Settlement buildSettlement(Civilization civilization, int regionIdx, float startingPopulation, Architect architect) throws Exception {
		String name = this.bookOfNames.generate("settlement");

		//TODO trovare un modo per calcolare la posizione in modo interessante all'interno della regione
		List<Point> coords = this.map.getCoordsFromRegionIndex(regionIdx);
		Point selectedCoords = coords.get(this.generator.nextInt(coords.size()));

		Settlement settlement = new Settlement(name, (int)selectedCoords.x, (int)selectedCoords.y, regionIdx, civilization.id());

		settlement.setPopulation(startingPopulation);
		settlement.setGrowthRate(civilization.growthRate());
		
		//keep track of number of locations in region
		this.map.politicalRegions().get(regionIdx).increaseCurrentNumberOfLocations();
		
		//build first district in settlement
		settlement = architect.initSettlement(settlement, civilization, this.generator);

		return settlement;
	}
	
	public void destroySettlement(Settlement settlement) {
		this.map.politicalRegions().get(settlement.regionIndex()).decreaseCurrentNumberOfLocations();
	}

	public Cave buildCave(int regionIdx) {
		String name = "cave"; //TODO fare  grammatica

		//TODO trovare un modo per calcolare la posizione in modo interessante all'interno della regione
		List<Point> coords = this.map.getCoordsFromRegionIndex(regionIdx);
		Point selectedCoords = coords.get(this.generator.nextInt(coords.size()));
		
		//keep track of number of locations in region
		this.map.politicalRegions().get(regionIdx).increaseCurrentNumberOfLocations();

		return new Cave(name, (int)selectedCoords.x, (int)selectedCoords.y, regionIdx);
	}
}
