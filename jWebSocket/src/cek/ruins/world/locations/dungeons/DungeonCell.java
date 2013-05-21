package cek.ruins.world.locations.dungeons;

import java.util.ArrayList;
import java.util.List;

import cek.ruins.Point;

public class DungeonCell {
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;
	
	protected int row, column, depth;
	protected int size;
	protected List<Point> exits;
	
	public DungeonCell(int row, int column, int depth, int size) {
		this.row = row;
		this.column = column;
		this.depth = depth;
		this.size = size;
		
		this.exits = new ArrayList<Point>();
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
}
