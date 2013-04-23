package cek.ruins.world.locations.dungeons;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import com.mongodb.DBObject;

import cek.ruins.Marshallable;

public class Dungeon implements Marshallable {
	private UUID id;
	
	private int size;
	private int numCellsPerSide;
	private List<List<List<DungeonTile>>> levels;
	private List<List<List<DungeonCell>>> cells;

	public Dungeon(UUID id, int size, int numCellsPerSide, int depth) {
		this.id = id;
		this.size = size;
		this.numCellsPerSide = numCellsPerSide;
		this.levels = new Vector<List<List<DungeonTile>>>();
		this.cells = new Vector<List<List<DungeonCell>>>();
		
		for (int d = 0; d < depth; d++)
			addLevel();
	}
	
	public List<List<DungeonTile>> level(int depth) {
		return this.levels.get(depth);
	}
	
	public List<List<DungeonCell>> cells(int depth) {
		return cells.get(depth);
	}
	
	public DungeonTile tile(int x, int y, int depth) {
		return this.level(depth).get(x).get(y);
	}
	
	public DungeonCell cell(int column, int row, int depth) {
		return this.cells(depth).get(column).get(row);
	}
	
	@Override
	public UUID id() {
		return this.id;
	}
	
	public int size() {
		return this.size;
	}
	
	public int depth() {
		return this.levels.size();
	}
	
	public int numCellsPerSide() {
		return numCellsPerSide;
	}
	
	public int addLevel() {
		List<List<DungeonTile>> level = new Vector<List<DungeonTile>>(size);
		for (int n = 0; n < size; n++) {
			List<DungeonTile> row = new Vector<DungeonTile>(size);
			for (int r = 0; r < size; r++) {
				DungeonTile tile = new DungeonTile(n, r, this.levels.size());
				row.add(tile);
			}
			level.add(row);
		}
		
		List<List<DungeonCell>> cellsLevel = new Vector<List<DungeonCell>>();
		for (int n = 0; n < this.numCellsPerSide(); n++) {
			List<DungeonCell> row = new Vector<DungeonCell>();
			for (int r = 0; r < this.numCellsPerSide(); r++) {
				DungeonCell cell = new DungeonCell(r, n, this.levels.size(), this.size/this.numCellsPerSide());
				row.add(cell);
			}
			cellsLevel.add(row);
		}
		
		this.levels.add(level);
		this.cells.add(cellsLevel);
		
		return this.levels.size() - 1;
	}

	@Override
	public DBObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}
}
