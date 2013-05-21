package cek.ruins.world.locations.dungeons;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import com.mongodb.DBObject;

import cek.ruins.Marshallable;
import cek.ruins.world.locations.dungeons.entities.ObservableEntity;

public class Dungeon implements Marshallable {
	private UUID id;
	
	private int size;
	private int numCellsPerSide;
	private List<List<List<DungeonTile>>> levels;
	private Map<String, ObservableEntity> entities;

	public Dungeon(UUID id, int size, int numCellsPerSide, int depth) {
		this.id = id;
		this.size = size;
		this.numCellsPerSide = numCellsPerSide;
		this.levels = new Vector<List<List<DungeonTile>>>();
		this.entities = new HashMap<String, ObservableEntity>();
		
		for (int d = 0; d < depth; d++)
			addLevel();
	}
	
	public List<List<DungeonTile>> level(int depth) {
		return this.levels.get(depth);
	}
	
	public DungeonTile tile(int x, int y, int depth) {
		return this.level(depth).get(x).get(y);
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
		
		this.levels.add(level);
		
		return this.levels.size() - 1;
	}

	public List<ObservableEntity> entities(int depth) {
		List<ObservableEntity> entities = new LinkedList<ObservableEntity>(); 
		for (Map.Entry<String, ObservableEntity> entityEntry : this.entities.entrySet()) {
			ObservableEntity entity = entityEntry.getValue();
			if (entity.hasAttribute("position:z") && ((Integer)entity.attribute("position:z")) == depth) {
				entities.add(entity);
			}
		}
		
		return entities;
	}
	
	public void addEntity(ObservableEntity entity) {
		this.entities.put(entity.id(), entity);
	}
	
	public void deleteEntity(String id) {
		this.entities.remove(id);
	}
	
	@Override
	public DBObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}
}
