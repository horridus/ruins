package cek.ruins.world.locations.dungeons;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cek.ruins.Point;

public class DungeonCell {
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;
	
	protected int row, column, depth;
	protected int size;
	protected List<Point> northEntrances;
	protected List<Point> southEntrances;
	protected List<Point> eastEntrances;
	protected List<Point> westEntrances;
	
	public DungeonCell(int row, int column, int depth, int size) {
		this.row = row;
		this.column = column;
		this.depth = depth;
		this.size = size;
		
		this.northEntrances = new ArrayList<Point>();
		this.southEntrances = new ArrayList<Point>();
		this.eastEntrances = new ArrayList<Point>();
		this.westEntrances = new ArrayList<Point>();
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
	
	public void markAsCellEntrance(int side, int coord) {
		switch (side) {
		case DungeonCell.NORTH:
		{
			Point coords = new Point(coord, 0);
			this.northEntrances.add(coords);
			break;
		}
		case DungeonCell.SOUTH:
		{
			Point coords = new Point(coord, this.size - 1);
			this.southEntrances.add(coords);
			break;
		}
		case DungeonCell.WEST:
		{
			Point coords = new Point(0, coord);
			this.eastEntrances.add(coords);
			break;
		}
		case DungeonCell.EAST:
		{
			Point coords = new Point(this.size - 1, coord);
			this.westEntrances.add(coords);
			break;
		}
		}
	}

	public List<Point> entrances(int side) {
		switch (side) {
		case DungeonCell.NORTH:
			return this.northEntrances;
		case DungeonCell.SOUTH:
			return this.southEntrances;
		case DungeonCell.EAST:
			return this.eastEntrances;
		case DungeonCell.WEST:
			return this.westEntrances;
		default:
			return null;
		}
	}
	
	public List<Point> entrances() {
		List<Point> entrances = new Vector<Point>();
		entrances.addAll(northEntrances);
		entrances.addAll(southEntrances);
		entrances.addAll(eastEntrances);
		entrances.addAll(westEntrances);
		
		return entrances;
	}
}
