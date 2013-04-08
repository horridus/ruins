package cek.ruins.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;
import cek.ruins.Point;
import cek.ruins.map.voronoi.Voronoi;
import cek.ruins.map.voronoi.VoronoiCorner;
import cek.ruins.map.voronoi.VoronoiEdge;
import cek.ruins.map.voronoi.VoronoiGraph;
import cek.ruins.map.voronoi.VoronoiSite;
import cek.ruins.utils.ImprovedNoise;
import cek.ruins.utils.NoiseGenerator;
import cek.ruins.world.environment.Biome;
import cek.ruins.world.environment.Climates;
import cek.ruins.world.environment.Resources;

public class Map implements Marshallable {
	private UUID id;

	private double minDistanceBetweenSites;
	private int numLloydIteraction;
	private int marginWidth;
	private double waterThreshold;
	private double noiseSamplingFrequency;
	private double radialIslandsCoefficient;
	private double radialLengthCoefficient;
	private String landGenerator;
	private int numMountainsRanges;
	private int mountainsRangesLength;
	private double maxmimumMoisture;
	private double moistureCoefficient;
	private double moistureDispersionCoefficient;
	private int numRivers;
	private Climates climate;

	//celsius
	private double baseTemperature;
	private double thermicalExcursionA, thermicalExcursionB;
	private double lapseRate = 1.94;
	//meters
	private double lapseAmplitude = 304;
	private double maxRegionAltitude = 3000;

	private double highmountainslimit;
	private double mountainslimit;

	private int size;
	private VoronoiGraph regionsGraph;
	private List<Region> regions;
	private List<RegionCorner> regionsCorners;
	private List<RegionEdge> regionsEdges;
	private List<PoliticalRegion> politicalRegions;
	private List<MacroRegion> macroRegions;

	private int[] coordsToRegionsMap;
	private java.util.Map<Integer, List<Point>> regionsToCoordsMap;

	public Map(UUID id, Climates climate, double baseTemperature, double thermicalExcursionA, double thermicalExcursionB) {
		this.id = id;

		this.minDistanceBetweenSites = 1.0;
		this.numLloydIteraction = 2;
		this.marginWidth = 5;
		this.waterThreshold = 0.2;
		this.noiseSamplingFrequency = 0.01;
		this.radialIslandsCoefficient = 1.2;
		this.radialLengthCoefficient = 0.8;
		this.numMountainsRanges = 7;
		this.mountainsRangesLength = 20;
		this.maxmimumMoisture = 0.3;
		this.moistureCoefficient = 0.2;
		this.moistureDispersionCoefficient = 0.9;
		this.numRivers = 20;
		this.climate = climate;
		this.baseTemperature = baseTemperature;
		this.thermicalExcursionA = thermicalExcursionA;
		this.thermicalExcursionB = thermicalExcursionB;
		this.highmountainslimit = 0.85;
		this.mountainslimit = 0.7;

		//this.landGenerator = "radial";
		this.landGenerator = "circularIsland";
		//this.landGenerator = "noisy";

		this.regionsGraph = new VoronoiGraph();
		this.regions = new Vector<Region>();
		this.regionsCorners = new Vector<RegionCorner>();
		this.regionsEdges = new Vector<RegionEdge>();
		this.politicalRegions = new Vector<PoliticalRegion>();
		this.macroRegions = new Vector<MacroRegion>();

		this.regionsToCoordsMap = new HashMap<Integer, List<Point>>();
	}

	public int size() {
		return this.size;
	}

	public List<Region> regions() {
		return this.regions;
	}

	public VoronoiGraph regionsGraph() {
		return this.regionsGraph;
	}

	public List<RegionEdge> regionsEdges() {
		return this.regionsEdges;
	}

	public List<RegionCorner> regionsCorners() {
		return this.regionsCorners;
	}

	public List<PoliticalRegion> politicalRegions() {
		return this.politicalRegions;
	}
	
	public List<MacroRegion> macroRegions() {
		return this.macroRegions;
	}

	public void generate(Integer size, Integer numSites, Random generator, Resources resources) {
		this.size = size;
		Voronoi voronoi = new Voronoi(minDistanceBetweenSites, size, numLloydIteraction, regionsGraph);

		for (int i = 0; i < numSites; i++) {
			int x = this.marginWidth + generator.nextInt(this.size - this.marginWidth);
			int y = this.marginWidth + generator.nextInt(this.size - this.marginWidth);

			voronoi.addPotentialSite(x, y);
		}

		voronoi.generate();

		double landSeed = generator.nextDouble();
		int bumps = generator.nextInt(5) + 1;
		double startAngle = 2.0*Math.PI*generator.nextDouble();
		double dipAngle = 2.0*Math.PI*generator.nextDouble();
		double dipWidth = 0.2 + generator.nextDouble() * 0.5;

		double centerX = (generator.nextDouble() * this.size) - this.size/2.0;
		double centerY = (generator.nextDouble() * this.size) - this.size/2.0;

		//create regions from graph sites
		Iterator<VoronoiCorner> cornersIt = this.regionsGraph.corners().iterator();
		while (cornersIt.hasNext()) {
			VoronoiCorner corner = cornersIt.next();

			RegionCorner regionCorner = new RegionCorner(this.regionsCorners.size());

			double seed = (double)generator.nextInt() * generator.nextDouble();
			if (this.landGenerator.equals("radial")) {
				regionCorner.water = !radialLand(corner.point.x, corner.point.y, seed, bumps, startAngle, dipAngle, dipWidth, centerX, centerY);
			}
			else if (this.landGenerator.equals("circularIsland")) {
				regionCorner.water = !circularIsland(corner.point.x, corner.point.y, landSeed * seed);
			}
			else if (this.landGenerator.equals("noisy")) {
				regionCorner.water = false;
			}

			this.regionsCorners.add(regionCorner);
		}

		for (int i = 0; i < this.regionsGraph.edges().size(); i++) {
			RegionEdge regionEdge = new RegionEdge(this.regionsEdges.size());
			this.regionsEdges.add(regionEdge);
		}

		for (int i = 0; i < this.regionsGraph.sites().size(); i++) {
			Region region = new Region(this.regions.size());
			this.regions.add(region);
		}

		for (int i = 0; i < this.regionsGraph.sites().size(); i++) {
			PoliticalRegion politicalRegion = new PoliticalRegion(this.regions.size());
			this.politicalRegions.add(politicalRegion);
		}

		//determine the elevations at regions corners
		//assignCornerElevation();

		//determine polygon and corner type: ocean, coast, land.
        assignOceanCoastAndLand();

        //create mountainous regions and determine the elevations at Voronoi corners
        createMountainsRanges(this.numMountainsRanges, this.mountainsRangesLength, generator);

        //redistribute the elevations at regions corners and centers
        redistributeElevationsToCornersAndRegions();
        //redistributeElevations(landCorners());
        //assignRegionElevations();

		//compute rivers paths and moisture
        calculateDownslopes();
        calculateUpslopes();
		calculateWatersheds();
		createRivers(this.numRivers, generator);

		//determine moisture at corners, starting at rivers
        //and lakes, but not oceans. Then redistribute
        //moisture to cover the entire range evenly from 0.0
        //to 1.0. Then assign polygon moisture as the average
        //of the corner moisture.
        assignCornerMoisture();
        assignRegionsMoisture();

        //determine temperatures based on elevation and noise
        assignCornerTemperature(generator);
        assignRegionsTemperature();

        //detemine biomes
        assignCornerBiomes();
        assignRegionsBiomes();
        assignRegionsResources(resources, generator);

        createMacroRegions();
	}

	/**
	 * Find region under given point.
	 * @param x
	 * @param y
	 * @return a region index.
	 */
	public int getRegionIndexFromCoords(int x, int y) {
		if (x < 0 || x >= this.size || y < 0 || y >= this.size)
			return -1;

		return this.coordsToRegionsMap[x + y*this.size];
	}

	/**
	 * Get a list of all points inside a region.
	 * @param regionIndex
	 * @return List of Points.
	 */
	public List<Point> getCoordsFromRegionIndex(int regionIndex) {
		return this.regionsToCoordsMap.get(regionIndex);
	}

	/**
	 * Find all non water regions around selected region.
	 * @param regionIndex
	 * @return List of regions indices.
	 */
	public List<Integer> getNonWaterNeighbors(Integer regionIndex) {
		List<Integer> nonWaterNeighbors = new LinkedList<Integer>();

		Iterator<Integer> neighborsIt = this.regionsGraph.sites().get(regionIndex).neighbors.iterator();
		while (neighborsIt.hasNext()) {
			Integer neighborIndex = neighborsIt.next();
			if (!this.regions.get(neighborIndex).isWater())
				nonWaterNeighbors.add(neighborIndex);
		}

		return nonWaterNeighbors;
	}
	
	public void processCoordsToRegionsMap(int[] crMap) {
		this.setCoordsToRegionsMap(crMap);
		this.setRegionsToCoordsMap(crMap);
		this.computeRegionsAreas();
	}

	//TODO devo poter settare un altezza in modo da fare colline o montagne basse, ecc.
	//in modo da poter creare un mondo più o meno alto.
	private void createMountainsRanges(int numRanges, int rangeLength, Random generator) {
		Vector<Region> queue = new Vector<Region>();

		Iterator<Region> regionssIt = this.regions.iterator();
		while (regionssIt.hasNext()) {
			Region region = regionssIt.next();
			if (!region.water)
				queue.add(region);
		}

		for (int i = 0; i < numRanges; i++) {
			int startIdx = generator.nextInt(queue.size());
			Region region = queue.get(startIdx);
			VoronoiSite site = this.regionsGraph.sites().get(region.index);

			for (int n = 0; n < rangeLength; n++) {
				region.elevation = 1.0;
				Vector<Region> nextRegions = new Vector<Region>();
				for (int u = 0; u < site.neighbors.size(); u++) {
					Region nRegion = this.regions.get(site.neighbors.get(u));
					if (nRegion.elevation < 1.0) {
						nextRegions.add(nRegion);
					}
				}

				if (nextRegions.size() > 0) {
					region = nextRegions.get(generator.nextInt(nextRegions.size()));
					site = this.regionsGraph.sites().get(region.index);
				}
				else
					break;
			}
		}
	}

	private void assignCornerBiomes() {
		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();
		while (cornersIt.hasNext()) {
			RegionCorner rCorner = cornersIt.next();
			assignCornerElevationBiomes(rCorner);
			assignCornerClimateBiomes(rCorner);
		}
	}

	private void assignCornerElevationBiomes(RegionCorner corner) {
		if (corner.ocean)
			corner.biome = Biome.ocean;
		else if (corner.water)
			corner.biome = Biome.lake;
		else if (corner.elevation >= this.highmountainslimit)
			corner.biome = Biome.high_mountains;
		else if (corner.elevation >= this.mountainslimit)
			corner.biome = Biome.mountains;
		else
			corner.biome = Biome.grassland;
	}

	private void assignCornerClimateBiomes(RegionCorner corner) {
		if (corner.biome == Biome.grassland) {
			if (corner.coast) {
				corner.biome = Biome.coast;
			}
			else {
				switch (this.climate) {
				case dry:
					dryClimate(corner);
					break;
				case temperate:
					temperateClimate(corner);
					break;
				case tropical:
					tropicalClimate(corner);
					break;
				case continental:
					continentalClimate(corner);
					break;
				case polar:
					polarClimate(corner);
					break;
				}
			}
		}
		else if ( corner.biome == Biome.lake && corner.temperature <= 0.0) {
			corner.biome = Biome.ice;
		}
	}

	private void dryClimate(RegionCorner corner) {
		if (corner.temperature <= 0.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.tundra;
			else
				corner.biome = Biome.tundra;
		}
		else if (corner.temperature <= 10.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.badlands;
			else
				corner.biome = Biome.badlands;
		}
		else if (corner.temperature <= 20.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.grassland;
			else
				corner.biome = Biome.grassland;
		}
		else if (corner.temperature <= 30.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.savanna;
			else
				corner.biome = Biome.savanna;
		}
		else {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.badlands;
			else
				corner.biome = Biome.badlands;
		}
	}

	private void temperateClimate(RegionCorner corner) {
		if (corner.temperature <= 0.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.taiga;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.taiga;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.taiga;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.glacier;
			else
				corner.biome = Biome.glacier;
		}
		else if (corner.temperature <= 10.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.coniferous_forest;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.coniferous_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.coniferous_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.marsh;
			else
				corner.biome = Biome.marsh;
		}
		else if (corner.temperature <= 20.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.swamp;
			else
				corner.biome = Biome.swamp;
		}
		else if (corner.temperature <= 30.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.swamp;
			else
				corner.biome = Biome.swamp;
		}
		else {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.shrubland;
			else
				corner.biome = Biome.shrubland;
		}
	}

	private void tropicalClimate(RegionCorner corner) {
		if (corner.temperature <= 0.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.glacier;
			else
				corner.biome = Biome.glacier;
		}
		else if (corner.temperature <= 10.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.swamp;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.swamp;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.swamp;
			else
				corner.biome = Biome.swamp;
		}
		else if (corner.temperature <= 20.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.rain_forest;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.rain_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.rain_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.rain_forest;
			else
				corner.biome = Biome.swamp;
		}
		else if (corner.temperature <= 30.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.rain_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.rain_forest;
			else
				corner.biome = Biome.swamp;
		}
		else {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.rain_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.rain_forest;
			else
				corner.biome = Biome.swamp;
		}
	}

	private void continentalClimate(RegionCorner corner) {
		if (corner.temperature <= 0.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.taiga;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.taiga;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.taiga;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.glacier;
			else
				corner.biome = Biome.glacier;
		}
		else if (corner.temperature <= 10.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.coniferous_forest;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.coniferous_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.coniferous_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.marsh;
			else
				corner.biome = Biome.marsh;
		}
		else if (corner.temperature <= 20.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.swamp;
			else
				corner.biome = Biome.swamp;
		}
		else if (corner.temperature <= 30.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.swamp;
			else
				corner.biome = Biome.swamp;
		}
		else {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.shrubland;
			else
				corner.biome = Biome.shrubland;
		}
	}

	private void polarClimate(RegionCorner corner) {
		if (corner.temperature <= 0.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.tundra;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.taiga;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.taiga;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.taiga;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.glacier;
			else
				corner.biome = Biome.glacier;
		}
		else if (corner.temperature <= 10.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.coniferous_forest;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.coniferous_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.coniferous_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.marsh;
			else
				corner.biome = Biome.marsh;
		}
		else if (corner.temperature <= 20.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.swamp;
			else
				corner.biome = Biome.swamp;
		}
		else if (corner.temperature <= 30.0) {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.grassland;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.deciduous_forest;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.swamp;
			else
				corner.biome = Biome.swamp;
		}
		else {
			if (corner.moisture <= 0.1)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.2)
				corner.biome = Biome.sand_desert;
			else if (corner.moisture <= 0.3)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.4)
				corner.biome = Biome.rock_desert;
			else if (corner.moisture <= 0.5)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.6)
				corner.biome = Biome.badlands;
			else if (corner.moisture <= 0.7)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.8)
				corner.biome = Biome.shrubland;
			else if (corner.moisture <= 0.9)
				corner.biome = Biome.shrubland;
			else
				corner.biome = Biome.shrubland;
		}
	}

	private void assignRegionsBiomes() {
		Iterator<Region> regionsIt = this.regions.iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = this.regionsGraph.sites().get(region.index);

			//if is ocean or lake, we already know and
			//later computation will select wrong regional biome
			if (region.ocean) {
				region.biome = Biome.ocean;
				continue;
			}

			if (region.water) {
				region.biome = Biome.lake;
				continue;
			}

			int count = 0;
			java.util.Map<Biome, Integer> cornersBiomes = new HashMap<Biome, Integer>();

			for (Integer cornerIdx : site.corners) {
				RegionCorner corner = this.regionsCorners.get(cornerIdx);
				count = 0;
				if (cornersBiomes.containsKey(corner.biome))
					count = cornersBiomes.get(corner.biome);

				count++;
				cornersBiomes.put(corner.biome, count);
			}

			count = 0;
			Biome selectedBiome = Biome.grassland;
			for (java.util.Map.Entry<Biome, Integer> entry : cornersBiomes.entrySet()) {
				if (entry.getKey() != Biome.lake && entry.getKey() != Biome.ocean) { //we already took care of lake, and we don't want islands to become lakes
					if (Math.max(count, entry.getValue()) == entry.getValue()) {
						count = entry.getValue();
						selectedBiome = entry.getKey();
					}
				}
			}

			region.biome = selectedBiome;
		}
	}

	//Calculate temperature from temperature mean and a noise map.
	//Elevation will reduce temperature;
	private void assignCornerTemperature(Random generator) {
		NoiseGenerator nGenerator = new NoiseGenerator();
		double interval = this.thermicalExcursionB - this.thermicalExcursionA;

		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();
		while (cornersIt.hasNext()) {
			RegionCorner rCorner = cornersIt.next();
			VoronoiCorner vCorner = this.regionsGraph.corners().get(rCorner.index);

			double temperature = baseTemperature + this.thermicalExcursionA + (nGenerator.noise01(vCorner.point.x * 0.01, vCorner.point.y * 0.01, generator.nextDouble()) * interval);

			//elevation reduction
			temperature = temperature - ((rCorner.elevation * this.maxRegionAltitude) / this.lapseAmplitude)  * this.lapseRate;

			rCorner.temperature = temperature;
		}
	}

	// Polygon moisture is the average of the moisture at corners
	private void assignRegionsTemperature() {
		Iterator<Region> regionsIt = this.regions.iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = this.regionsGraph.sites().get(region.index);

			for (Integer cornerIdx : site.corners) {
				RegionCorner corner = this.regionsCorners.get(cornerIdx);
				region.temperature += corner.temperature;
			}

			region.temperature /= site.corners.size();
		}
	}

	//Calculate moisture. Freshwater sources spread moisture: rivers
    //and lakes (not oceans). Saltwater sources have moisture but do
    //not spread it (we set it at the end, after propagation).
	private void assignCornerMoisture() {
		LinkedList<RegionCorner> queue = new LinkedList<RegionCorner>();

		//fresh water
		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();
		while (cornersIt.hasNext()) {
			RegionCorner rCorner = cornersIt.next();

			if ((rCorner.isWater() || rCorner.isRiver()) && !rCorner.isOcean()) {
				rCorner.moisture = (rCorner.isRiver())? Math.min(this.maxmimumMoisture, (this.moistureCoefficient * rCorner.river)) : 1.0;
				queue.add(rCorner);
			}
			else
				rCorner.moisture = 0.0;
		}

		while (!queue.isEmpty()) {
			RegionCorner rCorner = queue.poll();
			VoronoiCorner vCorner = this.regionsGraph.corners().get(rCorner.index);

			for (Integer cornerIdx : vCorner.adjacent) {
				RegionCorner adjCorner = this.regionsCorners.get(cornerIdx);

				double newMoisture = rCorner.moisture * this.moistureDispersionCoefficient;
				if (newMoisture > adjCorner.moisture) {
					adjCorner.moisture = newMoisture;
					queue.add(adjCorner);
				}
			}
		}

		//salt water
		cornersIt = this.regionsCorners.iterator();
		while (cornersIt.hasNext()) {
			RegionCorner rCorner = cornersIt.next();

			if (rCorner.ocean || rCorner.coast)
				rCorner.moisture = 1.0;
		}
	}

	// Polygon moisture is the average of the moisture at corners
	private void assignRegionsMoisture() {
		Iterator<Region> regionsIt = this.regions.iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = this.regionsGraph.sites().get(region.index);

			for (Integer cornerIdx : site.corners) {
				RegionCorner corner = this.regionsCorners.get(cornerIdx);
				region.moisture += corner.moisture;
			}

			region.moisture /= site.corners.size();
		}
	}

	private void assignOceanCoastAndLand() {
		LinkedList<Integer> queue = new LinkedList<Integer>();

		Iterator<Region> regionsIt = this.regions.iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = this.regionsGraph.sites().get(region.index);

			int numWater = 0;

			for (Integer cornerIdx : site.corners) {
				RegionCorner rCorner = this.regionsCorners.get(cornerIdx);
				VoronoiCorner vCorner = this.regionsGraph.corners().get(cornerIdx);

				if (vCorner.border && rCorner.water) {
					region.ocean = true;
					queue.push(region.index);
				}

				if (rCorner.water)
					numWater++;
			}

			region.water = (region.ocean || numWater > site.corners.size() * this.waterThreshold);
		}

		while (!queue.isEmpty()) {
			int siteIdx = queue.poll();
			VoronoiSite site = this.regionsGraph.sites().get(siteIdx);

			for (Integer regionIdx : site.neighbors) {
				Region region = this.regions.get(regionIdx);
				if (region.water && !region.ocean) {
					region.ocean = true;
					queue.add(regionIdx);
				}
			}
		}

		regionsIt = this.regions.iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = this.regionsGraph.sites().get(region.index);

			int numLand = 0;
			int numOcean = 0;

			for (Integer regionIdx: site.neighbors) {
				Region nRegion = this.regions.get(regionIdx);
				if (nRegion.ocean)
					numOcean++;

				if (!nRegion.water)
					numLand++;
			}

			region.coast = (numOcean > 0 && numLand > 0);
		}

		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();
		while (cornersIt.hasNext()) {
			RegionCorner rCorner = cornersIt.next();
			VoronoiCorner vCorner = this.regionsGraph.corners().get(rCorner.index);

			int numLand = 0;
			int numOcean = 0;

			for (Integer regionIdx : vCorner.touches) {
				Region nRegion = this.regions.get(regionIdx);

				if (nRegion.ocean)
					numOcean++;

				if (!nRegion.water)
					numLand++;
			}

			rCorner.ocean = (numOcean == vCorner.touches.size());
			rCorner.coast = (numOcean > 0) && (numLand > 0);
			rCorner.water = ((numLand != vCorner.touches.size()) && !rCorner.coast);
		}
	}

	private void calculateDownslopes() {
		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();

		while (cornersIt.hasNext()) {
			RegionCorner rCorner = cornersIt.next();
			VoronoiCorner vCorner = this.regionsGraph.corners().get(rCorner.index);

			RegionCorner downslopeCorner = rCorner;

			for (Integer cornerIdx : vCorner.adjacent) {
				RegionCorner nextCorner = this.regionsCorners.get(cornerIdx);
				if (nextCorner.elevation < downslopeCorner.elevation)
					downslopeCorner = nextCorner;
			}

			//downslope
			rCorner.downslope = downslopeCorner.index;
		}
	}

	private void calculateUpslopes() {
		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();

		while (cornersIt.hasNext()) {
			RegionCorner rCorner = cornersIt.next();
			VoronoiCorner vCorner = this.regionsGraph.corners().get(rCorner.index);

			for (Integer cornerIdx : vCorner.adjacent) {
				RegionCorner nextCorner = this.regionsCorners.get(cornerIdx);
				if (nextCorner.elevation >= rCorner.elevation)
					rCorner.upslopes.add(nextCorner.index);
			}
		}
	}

	private void calculateWatersheds() {
		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();
		while (cornersIt.hasNext()) {
			RegionCorner rCorner = cornersIt.next();
			if (!rCorner.ocean && !rCorner.coast) {
				rCorner.watershed = rCorner.downslope;
			}
		}

		//follow the downslope pointers to the coast.
		for (int i = 0; i < this.regionsCorners.size(); i++) {
			boolean changed = false;
			cornersIt = this.regionsCorners.iterator();
			while (cornersIt.hasNext()) {
				RegionCorner rCorner = cornersIt.next();
				if (!rCorner.ocean && !rCorner.coast && !this.regionsCorners.get(rCorner.watershed).coast) {
					int watershedIdx = this.regionsCorners.get(rCorner.downslope).watershed;
					RegionCorner wsCorner = this.regionsCorners.get(watershedIdx);
					if (!wsCorner.ocean)
						rCorner.watershed = watershedIdx;

					changed = true;
				}
			}

			if (!changed)
				break;
		}
	}

	private void createRivers(int wantedNumRivers, Random generator) {
		Vector<RegionCorner> nonCoastalCorners = new Vector<RegionCorner>();

		//find available starting points - select non-coastal corners
		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();
		while (cornersIt.hasNext()) {
			RegionCorner corner = cornersIt.next();
			if (!corner.isCoast() && !corner.isWater())
				nonCoastalCorners.add(corner);
		}

		Set<Integer> debug = new HashSet<Integer>();
		for (int n = 0; n < wantedNumRivers; n++) {
			LinkedList<RegionCorner> river = new LinkedList<RegionCorner>();

			if (nonCoastalCorners.size() == 0)
				break;

			int selectedCorner = generator.nextInt(nonCoastalCorners.size());
			RegionCorner rCorner = nonCoastalCorners.get(selectedCorner);
			VoronoiCorner vCorner = this.regionsGraph.corners().get(rCorner.index);
			int lastCornerIdx = rCorner.index;
			nonCoastalCorners.remove(selectedCorner);

			while (true) {
				//add to final river corners list
				river.add(rCorner);

				if (rCorner.isCoast() || rCorner.isRiver()) {
					break;
				}

				int nextCorner = rCorner.downslope;
				//you can't go anywhere else
				if (rCorner.downslope == rCorner.index) {
					double currentElevation = Double.MAX_VALUE;
					for (Integer adjIdx : vCorner.adjacent) {
						RegionCorner adjCorner = this.regionsCorners.get(adjIdx);
						if (adjCorner.elevation < currentElevation && adjIdx != lastCornerIdx && river.contains(adjCorner)) {
							currentElevation = adjCorner.elevation;
							nextCorner = adjIdx;
						}
					}

					if (nextCorner == rCorner.downslope) {
						do {
							nextCorner = vCorner.adjacent.get(generator.nextInt(vCorner.adjacent.size()));
						} while (nextCorner == lastCornerIdx);
					}

					this.regionsCorners.get(nextCorner).elevation = river.peekLast().elevation;
				}

				lastCornerIdx = rCorner.index;
				rCorner = this.regionsCorners.get(nextCorner);
				vCorner = this.regionsGraph.corners().get(rCorner.index);

				if (river.contains(rCorner)) {
					break;
				}
			}

			//check if created river is good else throw it away and restart
			if (river.isEmpty()) {
				n--;
				continue;
			}

			if (!river.peekFirst().isCoast() && !river.peekLast().isCoast()) {
				n--;
				continue;
			}

			if (river.size() < 6) {
				n--;
				continue;
			}

			while (river.size() > 1) {
				RegionCorner riverCorner = river.poll();
				VoronoiCorner vRiverCorner = this.regionsGraph.corners().get(riverCorner.index);

				RegionCorner nextRiverCorner = river.peek();

				riverCorner.river += 1.0;
				nextRiverCorner.river += 1.0;

				Integer edgeIdx = this.regionsGraph.lookupEdgeFromCorner(vRiverCorner, nextRiverCorner.index, riverCorner.index);

				//mark selected edge as river
				this.regionsEdges.get(edgeIdx).river += 1.0;

				debug.add(edgeIdx);
			}
		}
	}

	private void redistributeElevationsToCornersAndRegions() {
		LinkedList<RegionCorner> queue = new LinkedList<RegionCorner>();
		java.util.Map<Integer, Double> distances = new HashMap<Integer, Double>();

		Iterator<Region> regionsIt = this.regions.iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = this.regionsGraph.sites().get(region.index);

			//reset water regions to 0 elevation
			if (region.water) {
				region.elevation = 0.0;
				for (Integer cornerIdx : site.corners) {
					RegionCorner corner = this.regionsCorners.get(cornerIdx);
					corner.elevation = region.elevation;
				}
			}

			if ((1.0 - region.elevation) < .0000001) {
				for (Integer cornerIdx : site.corners) {
					RegionCorner corner = this.regionsCorners.get(cornerIdx);
					corner.elevation = region.elevation;
					queue.add(corner);
					distances.put(cornerIdx, 1.0);
				}
			}
		}

		double maxDistance = 0.0;
		while (!queue.isEmpty()) {
			RegionCorner rCorner = queue.poll();
			VoronoiCorner vCorner = this.regionsGraph.corners().get(rCorner.index);

			double distance = distances.get(rCorner.index) + 1.0;

			for (Integer cornerIdx : vCorner.adjacent) {
				RegionCorner adjCorner = this.regionsCorners.get(cornerIdx);

				//is a land corner
				if (!adjCorner.ocean && !adjCorner.water) {
					if (distances.containsKey(adjCorner.index)) {
						double oldDistance = distances.get(adjCorner.index);
						if (oldDistance < distance)
							distances.put(adjCorner.index, distance);
					}
					else {
						queue.add(adjCorner);
						distances.put(adjCorner.index, distance);
					}

					maxDistance = Math.max(maxDistance, distance);

					}
				else if (!adjCorner.ocean && adjCorner.water) {
					distances.put(adjCorner.index, distances.get(rCorner.index));
				}
			}
		}

		for (Entry<Integer, Double> entry : distances.entrySet()) {
			RegionCorner rCorner = this.regionsCorners.get(entry.getKey());
			rCorner.elevation = 1.0 - entry.getValue()/maxDistance;
		}

		LinkedList<Region> lakes = new LinkedList<Region>();

		regionsIt = this.regions.iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = this.regionsGraph.sites().get(region.index);

			//compute mean elevation for region and skip oceanic and lakes regions
			if (!region.water) {
				for (Integer cornerIdx : site.corners) {
					RegionCorner corner = this.regionsCorners.get(cornerIdx);
					region.elevation += corner.elevation;
				}

				region.elevation /= site.corners.size();

				/* TODO?
				if (region.elevation < this.minimumLandHeight)
					region.elevation = this.minimumLandHeight;
				*/

			}

			//compute minimum corner's elevation (skips 0.0 elevation anyway) and enqueue lakes regions
			if (region.water && !region.ocean) {

				double newElevation = 100000;

				for (Integer cornerIdx : site.corners) {
					RegionCorner corner = this.regionsCorners.get(cornerIdx);
					if (corner.elevation != 0.0 && corner.elevation < newElevation)
						newElevation = corner.elevation;
				}
				region.elevation = newElevation;
				lakes.add(region);
			}
		}

		//now flatten lakes elevation to internal computed minimum
		while (!lakes.isEmpty()) {
			Region lake = lakes.poll();
			VoronoiSite site = this.regionsGraph.sites().get(lake.index);

			for (Integer lakeIdx: site.neighbors) {
				Region adjLake = this.regions.get(lakeIdx);
				if (adjLake.elevation >= lake.elevation) {
					adjLake.elevation = lake.elevation;
					VoronoiSite adjSite = this.regionsGraph.sites().get(lakeIdx);

					//set all sourrounding corners to this elevation
					for (Integer cornerIdx : adjSite.corners) {
						this.regionsCorners.get(cornerIdx).elevation = lake.elevation;
					}
				}
				else {
					if (!lakes.contains(adjLake))
						lakes.add(adjLake);

					break;
				}
			}

		}
	}

	private void assignRegionsResources(Resources resources, Random generator) {
		Iterator<Region> regionsIt = this.regions.iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();

			java.util.Map<String, Integer> biomeResources = resources.getBiomeResources(region.biome);
			 Iterator<Entry<String, Integer>> biomeResourcesIt = biomeResources.entrySet().iterator();
			 while (biomeResourcesIt.hasNext()) {
				 Entry<String, Integer> resourceEntry = biomeResourcesIt.next();
				 Integer presence = resourceEntry.getValue();
				 if (generator.nextInt(100) < presence) {
					 region.addPrincipalResource(resourceEntry.getKey());
				 }
			 }
		}
	}

	private void createMacroRegions() {
		Set<Integer> availableRegions = new HashSet<Integer>();
		Set<Integer> availableRegionsControlSet = new HashSet<Integer>();
		for (int i = 0; i < this.regions.size(); i++) {
			availableRegions.add(i);
			availableRegionsControlSet.add(i);
		}

		Iterator<Integer> availableIt = availableRegions.iterator();
		while (availableIt.hasNext()) {
			int regionIdx = availableIt.next();
			if (availableRegionsControlSet.contains(regionIdx)) {
				//create a new macro region and add same biome regions to it
				MacroRegion macroRegion = new MacroRegion(this.macroRegions.size());
				this.macroRegions.add(macroRegion);
				
				Region region = this.regions.get(regionIdx);
				availableRegionsControlSet.remove(regionIdx);
				macroRegion.regionsIdxs.add(regionIdx);

				fillMacroRegion(availableRegionsControlSet, availableRegions, region, macroRegion);
			}
		}

		assignMacroRegionsBorders();
	}

	private void fillMacroRegion(Set<Integer> availableRegionsControlSet, Set<Integer> availableRegions, Region startingRegion, MacroRegion macroRegion) {
		List<Region> toDoRegions = new Vector<Region>();

		VoronoiSite site = this.regionsGraph.sites().get(startingRegion.index());
		Iterator<Integer> neighborsIt = site.neighbors.iterator();
		while (neighborsIt.hasNext()) {
			int regionIdx = neighborsIt.next();
			Region region = this.regions.get(regionIdx);
			if (region.biome.equals(startingRegion.biome) && availableRegionsControlSet.contains(regionIdx)) {
				availableRegionsControlSet.remove(regionIdx);
				toDoRegions.add(region);
				macroRegion.regionsIdxs.add(regionIdx);
			}
		}

		Iterator<Region> regionsIt = toDoRegions.iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			fillMacroRegion(availableRegionsControlSet, availableRegions, region, macroRegion);
		}
	}

	private void assignMacroRegionsBorders() {
		Iterator<MacroRegion> macroRegionsIt = this.macroRegions.iterator();
		while (macroRegionsIt.hasNext()) {
			MacroRegion macroRegion = macroRegionsIt.next();
			Iterator<Integer> bordersIt = listAllMacroRegionEdges(macroRegion).iterator();
			while (bordersIt.hasNext()) {
				int edgeIdx = bordersIt.next();
				VoronoiEdge vEdge = this.regionsGraph.edges().get(edgeIdx);
				if (!macroRegion.regionsIdxs.contains(vEdge.site0) || !macroRegion.regionsIdxs.contains(vEdge.site1))
					this.regionsEdges.get(edgeIdx).border = true;
			}

		}
	}

	private Set<Integer> listAllMacroRegionEdges(MacroRegion macroRegion) {
		Set<Integer> edgesIdxs = new HashSet<Integer>();

		Iterator<Integer> regionsIdxsIt = macroRegion.regionsIdxs.iterator();
		while (regionsIdxsIt.hasNext()) {
			VoronoiSite site = this.regionsGraph.sites().get(regionsIdxsIt.next());
			Iterator<Integer> bordersIt = site.borders.iterator();
			while (bordersIt.hasNext())
				edgesIdxs.add(bordersIt.next());
		}

		return edgesIdxs;
	}
	
	private void setRegionsToCoordsMap(int[] map) {
		for (int y = 0; y < this.size; y++) {
			for (int x = 0; x < this.size; x++) {
				Integer regionIndex = map[x + y*this.size];

				List<Point> coords = this.regionsToCoordsMap.get(regionIndex);
				if (coords == null) {
					coords = new ArrayList<Point>();
					this.regionsToCoordsMap.put(regionIndex, coords);
				}

				coords.add(new Point(x, y));
			}
		}
	}
	
	private void computeRegionsAreas() {
		for (java.util.Map.Entry<Integer, List<Point>> entry : this.regionsToCoordsMap.entrySet()) {
			int index = entry.getKey();
			
			if (index < this.regionsGraph.sites().size() && index >= 0) { //FIXME in regionsToCoordsMap there are regions borders too
				VoronoiSite site = this.regionsGraph.sites().get(index);
				site.area = entry.getValue().size();
				
				PoliticalRegion pRegion = this.politicalRegions().get(index);
				pRegion.setMaxNumberOfLocations(site.area / 16); //FIXME 16 è un valore arbitrario per indicare lo spazio medio occupato da una locazione
			}
		}
	}
	
	private void setCoordsToRegionsMap(int[] map) {
		this.coordsToRegionsMap = map;
	}

	private boolean radialLand(double x, double y, double seed, int bumps, double startAngle, double dipAngle, double dipWidth, double centerX, double centerY) {
		Point p = new Point(x + centerX, y + centerY);
		double angle = Math.atan2(p.y, p.x);
		double diag = Math.sqrt(Math.pow(this.size + centerX, 2) + Math.pow(this.size + centerY, 2));
		double length =  this.radialLengthCoefficient * (Math.max(Math.abs(p.x/this.size/diag), Math.abs(p.y/diag)) + p.magnitudo()/diag);

		double r1 = 0.5 + 0.40*Math.sin(startAngle + bumps*angle + Math.cos((bumps+3)*angle));
		double r2 = 0.7 - 0.20*Math.sin(startAngle + bumps*angle - Math.sin((bumps+2)*angle));

		 if (Math.abs(angle - dipAngle) < dipWidth
	          || Math.abs(angle - dipAngle + 2*Math.PI) < dipWidth
	          || Math.abs(angle - dipAngle - 2*Math.PI) < dipWidth) {
			 r1 = r2 = 0.2;
	     }

		 return  (length < r1 || (length > r1*this.radialIslandsCoefficient && length < r2));
	}

	private boolean circularIsland(double x, double y, double seed) {
		Point p = new Point(x - this.size/2.0, y - this.size/2.0);
		double z = (ImprovedNoise.noise(x * this.noiseSamplingFrequency, y * this.noiseSamplingFrequency, seed) + 1.0)/2.0;
		double diag = Math.sqrt(2.0) * this.size/2.0;
		double q = p.magnitudo() / diag;
		return (z > Math.sin(q));
	}
	
	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		builder.add("regionsGraph", this.regionsGraph.toJSON());

		return builder.get();
	}

	@Override
	public UUID id() {
		return this.id;
	}

	/*
	private void assignCornerElevation() {
		LinkedList<RegionCorner> queue = new LinkedList<RegionCorner>();

		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();
		while (cornersIt.hasNext()) {
			RegionCorner rCorner = cornersIt.next();
			//VoronoiCorner vCorner = this.regionsGraph.corners().get(rCorner.index);

			//if (vCorner.border) {
			if (rCorner.water) {
				rCorner.elevation = 0.0;
				queue.add(rCorner);
			}
			else
				rCorner.elevation = Double.MAX_VALUE;
		}

		//traverse the graph and assign elevations to each point. As we
	    //move away from the map border, increase the elevations. This
	    //guarantees that rivers always have a way down to the coast by
	    //going downhill (no local minima).
		while (!queue.isEmpty()) {
			RegionCorner rCorner = queue.poll();
			VoronoiCorner vCorner = this.regionsGraph.corners().get(rCorner.index);

			for (Integer cornerIdx : vCorner.adjacent) {
				RegionCorner adjCorner = this.regionsCorners.get(cornerIdx);

				double newElevation = 0.01 + rCorner.elevation;
				if (!rCorner.water && !adjCorner.water)
					newElevation += 1.0;

				//if this point changed, we'll add it to the queue so
	            //that we can process its neighbors too.
				if (newElevation < adjCorner.elevation) {
					adjCorner.elevation = newElevation;
					queue.add(adjCorner);
				}
			}
		}

	}

	private void redistributeElevations(List<RegionCorner> landCorners) {
		Collections.sort(landCorners, new Comparator<RegionCorner>() {
			@Override
			public int compare(RegionCorner o1, RegionCorner o2) {
				if (o1.elevation < o2.elevation)
					return -1;

				if (o1.elevation > o2.elevation)
					return 1;

				return 0;
			}
		});

		for (int i = 0; i < landCorners.size(); i++) {
			//Let y(x) be the total area that we want at elevation <= x.
	        //We want the higher elevations to occur less than lower
	        //ones, and set the area to be y(x) = 1 - (1-x)^2.
			double y = (double)i / (landCorners.size() - 1);
			//Now we have to solve for x, given the known y.
	        // *  y = 1 - (1-x)^2
	        // *  y = 1 - (1 - 2x + x^2)
	        // *  y = 2x - x^2
	        // *  x^2 - 2x + y = 0
	        //From this we can use the quadratic equation to get:
			double x = Math.sqrt(1.1) - Math.sqrt(1.1*(1-y));
			if (x > 1.0)
				x = 1.0;

			landCorners.get(i).elevation = x;
		}

		//assign elevations to non-land corners
		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();
		while (cornersIt.hasNext()) {
			RegionCorner corner = cornersIt.next();
			if (corner.ocean || corner.coast)
				corner.elevation = 0.0;
		}

		//TODO gestire i elevation laghi
	}

	private void assignRegionElevations() {
		Iterator<Region> regionsIt = this.regions.iterator();
		while (regionsIt.hasNext()) {
			Region region = regionsIt.next();
			VoronoiSite site = this.regionsGraph.sites().get(region.index);

			//compute mean elevation for region and skip oceanic and lakes regions
			if (!region.water) {
				for (Integer cornerIdx : site.corners) {
					RegionCorner corner = this.regionsCorners.get(cornerIdx);
					region.elevation += corner.elevation;
				}

				region.elevation /= site.corners.size();
			}
		}
	}

	//change the overall distribution of moisture to be evenly distributed.
	private void redistributeMoisture(List<RegionCorner> landCorners) {
		Collections.sort(landCorners, new Comparator<RegionCorner>() {
			@Override
			public int compare(RegionCorner o1, RegionCorner o2) {
				if (o1.elevation < o2.elevation)
					return -1;

				if (o1.elevation > o2.elevation)
					return 1;

				return 0;
			}
		});

		for (int i = 0; i < landCorners.size(); i++) {
			landCorners.get(i).moisture = (double)i/(landCorners.size() - 1);
		}
	}

	private List<RegionCorner> landCorners() {
		List<RegionCorner> landCorners = new Vector<RegionCorner>();
		Iterator<RegionCorner> cornersIt = this.regionsCorners.iterator();
		while (cornersIt.hasNext()) {
			RegionCorner corner = cornersIt.next();
			if (!corner.ocean && !corner.coast)
				landCorners.add(corner);
		}

		return landCorners;
	}
	*/
}
