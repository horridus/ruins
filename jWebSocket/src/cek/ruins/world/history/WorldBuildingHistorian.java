package cek.ruins.world.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cek.ruins.events.Event;
import cek.ruins.events.EventName;
import cek.ruins.map.PoliticalRegion;
import cek.ruins.map.Region;
import cek.ruins.map.RegionCorner;
import cek.ruins.map.voronoi.VoronoiSite;
import cek.ruins.world.civilizations.Civilization;
import cek.ruins.world.environment.Resource;
import cek.ruins.world.environment.Resources;
import cek.ruins.world.locations.Cave;
import cek.ruins.world.locations.LocationsBuilder;
import cek.ruins.world.locations.Settlement;

public class WorldBuildingHistorian extends Historian {
	Map<UUID, Map<String, Object>> settlementsUpdates;
	Map<Integer, Map<String, Object>> politicalRegionsUpdates;

	//utility variables
	private LocationsBuilder locationsBuilder;
	//private RoadsBuilder roadsBuilder;

	private List<Settlement> newSettlements;
	private List<Settlement> deletedSettlements;

	public WorldBuildingHistorian() {
		this.settlementsUpdates = new HashMap<UUID, Map<String,Object>>();
		this.politicalRegionsUpdates = new HashMap<Integer, Map<String,Object>>();

		this.newSettlements = new LinkedList<Settlement>();
		this.deletedSettlements = new LinkedList<Settlement>();
	}

	@Override
	public void setDirector(HistoriansDirector director) {
		super.setDirector(director);

		this.locationsBuilder = new LocationsBuilder(this.director.map, this.director.generator, this.director.bookOfNames);
		//this.roadsBuilder = new RoadsBuilder(this.director.map);
	}

	@Override
	public void advanceDay() {

	}

	@Override
	public void advanceMonth() {

	}

	@Override
	public void advanceSeason() throws Exception {
		//this is used to temporarely store newly created settlements as we don't want to "grow" them on
		//creation. (And we should use a ConcurrentHashMap to do that, too).
		this.newSettlements.clear();
		this.deletedSettlements.clear();

		for (Map.Entry<UUID, Settlement> entry : this.director.locations.settlements().entrySet()) {
			Settlement settlement = entry.getValue();
			Civilization ownerCivilization = this.director.existingCivilizations.civilization(settlement.ownerCivilizationId());

			float environmentalResistance = computeEnvironmentalResistance(settlement);
			growSettlement(settlement, environmentalResistance);

			//check if migration is needed
			if (!this.deletedSettlements.contains(settlement) && this.director.architect.isMigrationNeeded(settlement, ownerCivilization, environmentalResistance)) {
				migrate(settlement);
			}

			updateSettlementLevel(settlement, ownerCivilization);
		}

		//add to locations newly created settlements
		Iterator<Settlement> settlementsIt = this.newSettlements.iterator();
		while (settlementsIt.hasNext()) {
			this.director.locations.addSettlement(settlementsIt.next());
		}

		//remove destroyed or abandoned settlements
		settlementsIt = this.deletedSettlements.iterator();
		while (settlementsIt.hasNext()) {
			this.director.locations.removeSettlement(settlementsIt.next());
		}

		Event event = new Event(HistoriansDirector.NS, EventName.UPDATE_LOCATIONS_EVENT);
		event.addData("settlements", this.settlementsUpdates);
		this.director.dispatch(event);

		event = new Event(HistoriansDirector.NS, EventName.UPDATE_REGIONS_EVENT);
		event.addData("political", this.politicalRegionsUpdates);
		this.director.dispatch(event);

		this.settlementsUpdates = new HashMap<UUID, Map<String,Object>>();
		this.politicalRegionsUpdates = new HashMap<Integer, Map<String,Object>>();
	}

	@Override
	public void advanceYear() {
		/*
		//TEMP///////////////////////////////////////
		if (this.director.locations.settlements().size() > 2) {
			int start = this.director.generator.nextInt(director.locations.settlements().size());
			int end = this.director.generator.nextInt(director.locations.settlements().size());

			Settlement origin = (Settlement) this.director.locations.settlements().values().toArray()[start];
			Settlement destination = (Settlement) this.director.locations.settlements().values().toArray()[end];

			Road road = this.roadsBuilder.buildNewRoad(origin, destination);

			//FIXME
			if (road.path() != null) {
				Event event = new Event(HistoriansDirector.NS, EventName.NEW_ROAD_EVENT);
				event.addData("road", road);
				this.director.dispatch(event);
			}
		}
		/////////////////////////////////////////////
		*/
	}

	private void growSettlement(Settlement settlement, float environmentalResistance) throws Exception {
		Civilization ownerCivilization = this.director.existingCivilizations.civilization(settlement.ownerCivilizationId());
		float currentPopulation = this.director.architect.growSettlement(settlement, ownerCivilization, environmentalResistance);

		if (currentPopulation < 2) {
			//settlement is abandoned.
			this.deletedSettlements.add(settlement);
			LocationsBuilder locationsBuilder = new LocationsBuilder(this.director.map, this.director.generator, this.director.bookOfNames);
			locationsBuilder.destroySettlement(settlement);
			
			this.director.writeHistory(ownerCivilization.name() + "'s settlement of " + settlement.name() + " has been abandoned");

			Event event = new Event(HistoriansDirector.NS, EventName.DELETE_LOCATION_EVENT);
			event.addData("locationId", settlement.id());
			event.addData("locationType", "settlement");

			//if settlement was the last one owned by civilization controlling the region, remove region's owner
			boolean regionHasNoOwner = true;
			for (Map.Entry<UUID, Settlement> entry : this.director.locations.settelementsInRegion(settlement.regionIndex()).entrySet()) {
				Settlement selectedSettlement = entry.getValue(); 
				if (selectedSettlement.ownerCivilizationId().equals(ownerCivilization.id()) && !selectedSettlement.id().equals(settlement.id())) {
					regionHasNoOwner = false;
					break;
				}
			}
			
			if (regionHasNoOwner) {
				java.util.Map<String,Object> regionUpdate = this.getPoliticalRegionUpdate(settlement.regionIndex());
				regionUpdate.put("owner", "");
			}
			
			//check if it was last settlement of a civilization.
			boolean civilizationNeedDeletion = true;
			for (Map.Entry<UUID, Settlement> entry : this.director.locations.settlements().entrySet()) {
				if (entry.getValue().ownerCivilizationId().equals(ownerCivilization.id())) {
					civilizationNeedDeletion = false;
					break;
				}
			}
			
			if (civilizationNeedDeletion) {
				this.director.existingCivilizations.destroyCivilization(ownerCivilization);
				this.director.writeHistory(ownerCivilization.name() + " has become extint.");
			}

			this.director.dispatch(event);
		}
		else {
			//update settlement's update
			Map<String, Object> update = getSettlementUpdate(settlement.id());
			update.put("population", settlement.population());
		}
	}

	private float computeEnvironmentalResistance(Settlement settlement) {
		Region region = this.director.map.regions().get(settlement.regionIndex());

		//apply biome type modifiers
		float environmentalResistance = new Float(((Integer) this.director.biomes.biomeAttributes(region.biome()).get("envresistance")));

		//check if region has river
		VoronoiSite site = this.director.map.regionsGraph().sites().get(settlement.regionIndex());
		for (int i = 0; i < site.corners.size(); i++) {
			RegionCorner rCorner = this.director.map.regionsCorners().get(site.corners.get(i));
			if (rCorner.isRiver())
				environmentalResistance += 10;
		}

		Iterator<String> principalResourcesIt = region.principalResources().iterator();
		while (principalResourcesIt.hasNext()) {
			String resourceId = principalResourcesIt.next();
			Resource resource = this.director.resources.getResource(resourceId);

			//now check effects related to settlements growth
			Map<String, Object> growthEffectArs = resource.effects(Resources.Names.GROWTH);
			if (growthEffectArs != null) {
				Integer percentage = (Integer) growthEffectArs.get("percentage");
				environmentalResistance = environmentalResistance + environmentalResistance * percentage / 100.0f;
			}
		}

		return environmentalResistance;
	}

	private int selectReagionForMigration(Settlement settlement) {
		//find region that aren't water
		List<Integer> nonWaterNeighbors = this.director.map.getNonWaterNeighbors(settlement.regionIndex());
		
		if (nonWaterNeighbors.size() > 0) {
			return nonWaterNeighbors.get(this.director.generator.nextInt(nonWaterNeighbors.size()));
		}
		else {
			//this is island!
			return -1;
		}
	}
	
	private void migrate(Settlement settlement) throws Exception {
		//TEMP////////////
		Civilization civilization = this.director.existingCivilizations.civilization(settlement.ownerCivilizationId());
		int migrationSize = (int) Math.floor(settlement.population() * 0.5f * this.director.generator.nextFloat() + 2);

		float currentPopulation = settlement.population() - migrationSize;
		settlement.setPopulation(currentPopulation);

		//update settlement's update
		Map<String, Object> update = getSettlementUpdate(settlement.id());
		update.put("population", settlement.population());

		//this list is used to track regions already visited by migrants
		List<Integer> visited = new ArrayList<Integer>();

		int selectedRegionIdx = this.selectReagionForMigration(settlement);
		if (selectedRegionIdx != -1) {
			Region selectedRegion = this.director.map.regions().get(selectedRegionIdx);

			visited.add(selectedRegionIdx);

			boolean done = false;
			while (!done) {
				//check for migration failure
				int migrationFailure = (Integer) this.director.biomes.biomeAttributes(selectedRegion.biome()).get("migrationfail");
				if (this.director.generator.nextInt(100) < migrationFailure) {

					this.director.writeHistory(civilization.name() + "'s settlers disappeared in " + selectedRegion.biome());
					done = true;
				}
				else {
					//count deaths...
					int deaths = this.director.generator.nextInt(migrationSize);
					migrationSize = migrationSize - deaths;

					if (migrationSize < 2) {
						//not enough settlers
						this.director.writeHistory(civilization.name() + "'s settlers perished in " + selectedRegion.biome());
						done = true;
					}
					else {
						PoliticalRegion politicalRegion = this.director.map.politicalRegions().get(selectedRegionIdx);
						Map<UUID, Settlement> existingSettlements = this.director.locations.settelementsInRegion(selectedRegionIdx);

						//if there's no owner, then the first civilization to arrive get the land
						if (politicalRegion.ownerCivilization() == null ) {
							Settlement newSettlement = locationsBuilder.buildSettlement(civilization, selectedRegionIdx, migrationSize, this.director.architect);
							this.newSettlements.add(newSettlement);

							this.director.writeHistory(migrationSize + " " + civilization.name() + "'s settlers founded settlement of " + newSettlement.name() +
													   " at " + newSettlement.x() + "," + newSettlement.y());

							Event event = new Event(HistoriansDirector.NS, EventName.NEW_LOCATION_EVENT);
							event.addData("location", newSettlement);
							event.addData("locationType", "settlement");

							this.director.dispatch(event);

							politicalRegion.setOwnerCivilization(civilization.id());

							java.util.Map<String,Object> regionUpdate = this.getPoliticalRegionUpdate(selectedRegionIdx);
							regionUpdate.put("owner", politicalRegion.ownerCivilization());

							done = true;
						}
						//check if region is owned and by who.
						else if (politicalRegion.ownerCivilization().equals(civilization.id())) {
							int roll = this.director.generator.nextInt(4);

							switch (roll) {
							case 0:
								//add to existing settlement
								for (Map.Entry<UUID, Settlement> settlementEntry : existingSettlements.entrySet()) {
									//TODO which settlement?
									if (settlementEntry.getValue().ownerCivilizationId().equals(civilization.id())) {
										settlementEntry.getValue().setPopulation(migrationSize);

										this.director.writeHistory(settlementEntry.getValue().name() + " welcomes " + migrationSize + " " + civilization.name() + "'s settlers ");

										done = true;
										break;
									}
								}
								break;
							case 1:
								//if there is space, built a new settlement
								int maxSettlements = politicalRegion.maxNumberOfLocations() / 5; //TEMP one fifth of total available locations space in region
								if (existingSettlements.size() < maxSettlements && politicalRegion.currentNumberOfLocations() < politicalRegion.maxNumberOfLocations()) { 
									
									LocationsBuilder locationsBuilder = new LocationsBuilder(this.director.map, this.director.generator, this.director.bookOfNames);
									Settlement newSettlement = locationsBuilder.buildSettlement(civilization, selectedRegionIdx, migrationSize, this.director.architect);
									this.newSettlements.add(newSettlement);
									
									this.director.writeHistory(migrationSize + " " + civilization.name() + "'s settlers founded settlement of " + newSettlement.name() +
															   " at " + newSettlement.x() + "," + newSettlement.y());

									Event event = new Event(HistoriansDirector.NS, EventName.NEW_LOCATION_EVENT);
									event.addData("location", newSettlement);
									event.addData("locationType", "settlement");

									this.director.dispatch(event);

									politicalRegion.setOwnerCivilization(civilization.id());

									java.util.Map<String,Object> regionUpdate = this.getPoliticalRegionUpdate(selectedRegionIdx);
									regionUpdate.put("owner", politicalRegion.ownerCivilization());

									done = true;
								}
								break;
							case 2:
								//go on?
								List<Integer> nonWaterNeighbors = this.director.map.getNonWaterNeighbors(selectedRegionIdx);
								selectedRegionIdx = nonWaterNeighbors.get(this.director.generator.nextInt(nonWaterNeighbors.size()));
								selectedRegion = this.director.map.regions().get(selectedRegionIdx);
								break;
							case 3:
								//event?
								if (migrationEvent(selectedRegionIdx, settlement, civilization)) {
									//go on
									nonWaterNeighbors = this.director.map.getNonWaterNeighbors(selectedRegionIdx);
									selectedRegionIdx = nonWaterNeighbors.get(this.director.generator.nextInt(nonWaterNeighbors.size()));
									selectedRegion = this.director.map.regions().get(selectedRegionIdx);
								}

								break;
							}

						}
						else {
							//FIXME battle?
							for (Map.Entry<UUID, Settlement> settlementEntry : this.director.locations.settelementsInRegion(selectedRegionIdx).entrySet()) {
								if (!settlementEntry.getKey().equals(civilization.id())) {
									this.director.writeHistory("Battle between " + settlementEntry.getValue().population() + " [race?] from " + settlementEntry.getValue().name() + " and " + migrationSize + " " + civilization.name() + "'s settlers ");
								}
							}

							done = true;
						}
					}
				}
			}
		}
		else {
			//this is island!
		}
		//////////////////
	}

	private void updateSettlementLevel(Settlement settlement, Civilization ownerCivilization) throws Exception {
		//check if settlement category changes
		boolean hasChanged = this.director.architect.updateSettlementLevel(settlement, ownerCivilization);
		
		if (hasChanged) {
			
			switch (settlement.level()) {
			case 0: //camp
				this.director.writeHistory(settlement.name() + "has become a camp.");
				break;

			case 1: //small hamlet
				this.director.writeHistory(settlement.name() + "has become a small hamlet.");
				break;
			}
			
			//update settlement's update
			Map<String, Object> update = getSettlementUpdate(settlement.id());
			update.put("level", settlement.level());
		}
	}

	private boolean migrationEvent(int regionIdx, Settlement settlement, Civilization civilization) {
		LocationsBuilder locationsBuilder = new LocationsBuilder(this.director.map, this.director.generator, this.director.bookOfNames);

		boolean outcome = true;
		int roll = this.director.generator.nextInt(5);

		PoliticalRegion politicalRegion = this.director.map.politicalRegions().get(regionIdx);
		
		switch (roll) {
		case 0: //natural caves found
			if (politicalRegion.currentNumberOfLocations() < politicalRegion.maxNumberOfLocations()) {
				Cave cave = locationsBuilder.buildCave(regionIdx);
				this.director.locations.addCave(cave);
	
				this.director.writeHistory("Travelers from " + settlement.name() + " discovered a natural cave at " + cave.x() + "," + cave.y());
	
				Event event = new Event(HistoriansDirector.NS, EventName.NEW_LOCATION_EVENT);
				event.addData("location", cave);
				event.addData("locationType", "cave");
	
				this.director.dispatch(event);
	
				outcome = true;
			}
			break;
		case 1: //temple built
			break;
		case 2: //tomb built
			break;
		case 3: //lumberjack's camp built
			break;
		case 4: //inn built
			break;
		case 5:
			break;
		}

		return outcome;
	}

	private Map<String, Object> getSettlementUpdate(UUID id) {
		Map<String, Object> update = this.settlementsUpdates.get(id);

		if (update == null) {
			update = new HashMap<String, Object>();
			this.settlementsUpdates.put(id, update);
		}

		return update;
	}

	private Map<String, Object> getPoliticalRegionUpdate(Integer regionIndex) {
		Map<String, Object> update = this.politicalRegionsUpdates.get(regionIndex);

		if (update == null) {
			update = new HashMap<String, Object>();
			this.politicalRegionsUpdates.put(regionIndex, update);
		}

		return update;
	}
}
