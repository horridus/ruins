package cek.ruins.map;

import java.util.ArrayList;
import java.util.List;

public class MacroRegion {
	String name;
	List<Integer> regionsIdxs;
	
	int index;
	
	public MacroRegion(int index) {
		this.name = "";
		this.regionsIdxs = new ArrayList<Integer>();
		
		this.index = index;
	}
	
	public String name() {
		return this.name;
	}
	
	public List<Integer> regions() {
		return this.regionsIdxs;
	}
}
