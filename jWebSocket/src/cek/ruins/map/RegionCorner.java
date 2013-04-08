package cek.ruins.map;

import java.util.Vector;

import cek.ruins.world.environment.Biome;

public class RegionCorner {
	boolean water;
	boolean ocean;
	boolean coast;
	double river;
	double moisture;
	double elevation;
	double temperature;
	int downslope;
	Vector<Integer> upslopes;
	int watershed;
	Biome biome;
	
	int index;
	
	public RegionCorner() {
		this.water = false;
		this.ocean = false;
		this.coast = false;
		this.river = 0.0;
		this.moisture = 0.0;
		this.elevation = 0.0;
		this.temperature = 0.0;
		this.downslope = -1;
		this.upslopes = new Vector<Integer>();
		this.watershed = -1;
		this.biome = Biome.grassland;
		
		this.index = -1;
	}
	

	public RegionCorner(int index) {
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
	
	public boolean isRiver() {
		return this.river > 0.0;
	}
	
	public double moisture() {
		return this.moisture;
	}
	
	public double temperature() {
		return this.temperature;
	}
	
	public double elevation() {
		return this.elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	
	public int downslope() {
		return downslope;
	}

	public void setDownslope(int downslope) {
		this.downslope = downslope;
	}
	
	public int watershed() {
		return watershed;
	}

	public void setWatershed(int watershed) {
		this.watershed = watershed;
	}
	
	public int index() {
		return this.index;
	}
}
