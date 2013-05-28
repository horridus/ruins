package cek.ruins.world.locations.dungeons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cek.ruins.Point;

public class DungeonCell {
	protected int row, column, depth;
	protected int size;
	protected List<Point> exits;
	protected Map<String, Object> memories;
	
	public DungeonCell(int row, int column, int depth, int size) {
		this.row = row;
		this.column = column;
		this.depth = depth;
		this.size = size;
		
		this.exits = new ArrayList<Point>();
		this.memories = new HashMap<String, Object>();
	}
	
	public int size() {
		return this.size;
	}
	
	public int row() {
		return this.row;
	}
	
	public int column() {
		return this.column;
	}
	
	public int depth() {
		return this.depth;
	}
	
	public void markAsExit(int x, int y) {
		this.exits.add(new Point(x, y));
	}

	public List<Point> exits() {
		return this.exits;
	}
	
	public void remeber(String name, Object value) {
		this.memories.put(name, value);
	}
	
	public Object remeber(String name) {
		return this.memories.get(name);
	}
}
