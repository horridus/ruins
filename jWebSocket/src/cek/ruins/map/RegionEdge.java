package cek.ruins.map;

public class RegionEdge {
	double river;
	boolean border;
	
	int index;
	
	public RegionEdge() {
		this.river = 0.0;
		this.border = false;
		
		this.index = -1;
	}
	
	public RegionEdge(int index) {
		this();
		
		this.index = index;
	}
	
	public boolean isRiver() {
		return this.river > 0.0;
	}
	
	public boolean isBorder() {
		return this.border;
	}
	
	public int index() {
		return this.index;
	}
}
