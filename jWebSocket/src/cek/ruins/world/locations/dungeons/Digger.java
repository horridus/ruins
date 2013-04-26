package cek.ruins.world.locations.dungeons;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.mozilla.javascript.NativeObject;

import cek.ruins.Point;
import cek.ruins.ScriptExecutor;
import cek.ruins.world.locations.dungeons.materials.Material;
import cek.ruins.world.locations.dungeons.materials.Materials;
import cek.ruins.world.locations.dungeons.templates.BuildEventTemplate;
import cek.ruins.world.locations.dungeons.templates.RoomTemplate;

//import com.infomatiq.jsi.rtree.RTree;

public class Digger {
	private DungeonsArchitect dungeonsArchitect;
	private Dungeon dungeon;
	private int depth;
	private Point iterator;
	private DungeonCell currentCell;
	private Random generator;
	private boolean corridorFlag;
	private boolean roomFlag;
	private Map<String, Object> executorScope;
	
	//private RTree roomsTree;
	
	public Digger(DungeonsArchitect dungeonsArchitect, Random generator, Map<String, Object> executorScope) {
		this.dungeonsArchitect = dungeonsArchitect;
		this.generator = generator;
		this.corridorFlag = false;
		this.roomFlag = false;
		this.executorScope = executorScope;
		
		//this.roomsTree = new RTree();
		//this.roomsTree.init(null);
	}
	
	
	public void setGenerator(Random generator) {
		this.generator = generator;
	}
	
	/* scripts utility functions */
	public void digCellTile(int cellx, int celly, Material material) {
		int dungeonx = cellx + this.currentCell.column() * this.currentCell.size();
		int dungeony = celly + this.currentCell.row() * this.currentCell.size();
		
		DungeonTile tile = tile(dungeonx, dungeony, this.currentCell.depth()); 
		tile.setMaterial(material);
		tile.setCorridor(this.corridorFlag);
		tile.setRoom(this.roomFlag);
		
		//FIXME vale pere tutti i materiali?
		if (cellx == 0)
			addCellWestEntrance(celly);
		if (cellx == this.currentCell.size() - 1)
			addCellEastEntrance(celly);
		if (celly == 0)
			addCellNorthEntrance(cellx);
		if (celly == this.currentCell.size() - 1)
			addCellSouthEntrance(cellx);
	}
	
	public void digTile(int x, int y, int depth, Material material) {
		DungeonTile tile = tile(x, y, depth);
		tile.setMaterial(material);
		
		tile.setCorridor(this.corridorFlag);
		tile.setRoom(this.roomFlag);
	}
	
	public Material cellTileMaterial(int cellx, int celly) {
		int dungeonx = cellx + this.currentCell.column() * this.currentCell.size();
		int dungeony = celly + this.currentCell.row() * this.currentCell.size();
		
		return tile(dungeonx, dungeony, this.currentCell.depth()).material();
	}
	
	public DungeonTile tile(int x, int y, int depth) {
		return this.dungeon.tile(x, y, depth);
	}
	
	public int randomI(int min, int max) {
		return this.generator.nextInt(max - min) + min;
	}
	
	public boolean nextCell() {
		if (this.depth >= this.dungeon.depth())
			return false;
		
		if (this.iterator.y >= this.dungeon.numCellsPerSide())
			return false;
		
		this.currentCell = this.dungeon.cell((int)this.iterator.x, (int)this.iterator.y, this.depth);
		
		if (this.iterator.x >= this.dungeon.numCellsPerSide() - 1) {
			this.iterator.x = 0;
			this.iterator.y += 1;
		}
		else
			this.iterator.x += 1;
		
		return true;
	}
	
	public void setCurrentCell(int column, int row, int depth) {
		this.currentCell = this.dungeon.cell(column, row, depth);
	}
	
	public void initCellsIterator(int depth) {
		this.iterator = new Point(0, 0);
		this.depth = depth;
	}
	
	public String currentRoomId() {
		return this.currentCell.roomId();
	}
	
	public void createRoom(String roomId, NativeObject args) {
		ScriptExecutor executor = ScriptExecutor.executor();
		RoomTemplate roomTemplate = this.dungeonsArchitect.roomsTemplates().get(roomId);
		if (roomTemplate != null) {
			startRoom();
			
			//set function args
			this.executorScope.put("_args_", args);
			//generate room and fill cell
			this.currentCell.setRoomId(roomId);
			executor.executeScript(roomTemplate.roomGenerator(), this.executorScope);
			//rest function args
			this.executorScope.put("_args_", null);
			
			stopRoom();
		}
	}
	
	public int cellSize() {
		return this.dungeon.size()/this.dungeon.numCellsPerSide();
	}
	
	public void digCorridor(int cellx0, int celly0, int cellx1, int celly1, Material material, Material wallMaterial) {
		int dungeonx = cellx0 + this.currentCell.column() * this.currentCell.size();
		int dungeony = celly0 + this.currentCell.row() * this.currentCell.size();
		DungeonTile start = this.tile(dungeonx, dungeony, this.currentCell.depth());
		
		dungeonx = cellx1 + this.currentCell.column() * this.currentCell.size();
		dungeony = celly1 + this.currentCell.row() * this.currentCell.size();
		DungeonTile goal = this.tile(dungeonx, dungeony, this.currentCell.depth());
		
		CorridorsDigger corridorsDigger = new CorridorsDigger(this.dungeon, this.currentCell);
		List<DungeonTile> corridor = corridorsDigger.dig(start, goal);
		
		if (corridor != null) {
			startCorridor();
			
			for (DungeonTile tile : corridor) {
				digTile(tile.x(), tile.y(), tile.depth(), material);
				
				//FIXME vale per tutti i materiali?
				if (tile.x%this.cellSize() == 0)
					addCellWestEntrance(tile.y%this.cellSize());
				if (tile.x%this.cellSize() == this.currentCell.size() - 1)
					addCellEastEntrance(tile.y%this.cellSize());
				if (tile.y%this.cellSize() == 0)
					addCellNorthEntrance(tile.x%this.cellSize());
				if (tile.y%this.cellSize() == this.currentCell.size() - 1)
					addCellSouthEntrance(tile.x%this.cellSize());
			}
			
			if (!wallMaterial.equals(Materials.NOMATERIAL())) {
				for (int i = 0; i < corridor.size(); i++) {
					DungeonTile tile = corridor.get(i);
					int skipDir = 0;
					
					//corridors should be open on ends
					if ((i == 0 || i == corridor.size() - 1) && corridor.size() > 1) {
						DungeonTile neighbor = (i == 0)? corridor.get(1) : corridor.get(corridor.size() - 2);
						int dirx = tile.x() - neighbor.x();
						int diry = tile.y() - neighbor.y();
						
						if (dirx == -1)
							skipDir = 1;
						else if (dirx == 1)
							skipDir = 2;
						else if (diry == -1)
							skipDir = 3;
						else if (diry == 1)
							skipDir = 4;
					}
					
					if (tile.x() > 0 && skipDir != 1) {
						DungeonTile neighbor = this.tile(tile.x() - 1, tile.y(), tile.depth());
						if (!neighbor.isRoom()) {
							digTile(tile.x() - 1, tile.y(), tile.depth(), wallMaterial);
						}
					}
					
					if (tile.x() < this.dungeon.size() - 1 && skipDir != 2) {
						DungeonTile neighbor = this.tile(tile.x() + 1, tile.y(), tile.depth());
						if (!neighbor.isRoom()) {
							digTile(tile.x() + 1, tile.y(), tile.depth(), wallMaterial);
						}
					}
					
					if (tile.y() > 0 && skipDir != 3) {
						DungeonTile neighbor = this.tile(tile.x(), tile.y() - 1, tile.depth());
						if (!neighbor.isRoom()) {
							digTile(tile.x(), tile.y() - 1, tile.depth(), wallMaterial);
						}
					}
					
					if (tile.y() < this.dungeon.size() - 1 && skipDir != 4) {
						DungeonTile neighbor = this.tile(tile.x(), tile.y() + 1, tile.depth());
						if (!neighbor.isRoom()) {
							digTile(tile.x(), tile.y() + 1, tile.depth(), wallMaterial);
						}
					}
					
					if (tile.x() > 0 && tile.y() > 0) {
						DungeonTile neighbor = this.tile(tile.x() - 1, tile.y() - 1, tile.depth());
						if (!neighbor.isRoom()) {
							digTile(tile.x() - 1, tile.y() - 1, tile.depth(), wallMaterial);
						}
					}
					
					if (tile.x() > 0 && tile.y() < this.dungeon.size() - 1) {
						DungeonTile neighbor = this.tile(tile.x() - 1, tile.y() + 1, tile.depth());
						if (!neighbor.isRoom()) {
							digTile(tile.x() - 1, tile.y() + 1, tile.depth(), wallMaterial);
						}
					}
					
					if (tile.x() < this.dungeon.size() - 1 && tile.y() > 0) {
						DungeonTile neighbor = this.tile(tile.x() + 1, tile.y() - 1, tile.depth());
						if (!neighbor.isRoom()) {
							digTile(tile.x() + 1, tile.y() - 1, tile.depth(), wallMaterial);
						}
					}
					
					if (tile.x() < this.dungeon.size() - 1 && tile.y() < this.dungeon.size() - 1) {
						DungeonTile neighbor = this.tile(tile.x() + 1, tile.y() + 1, tile.depth());
						if (!neighbor.isRoom()) {
							digTile(tile.x() + 1, tile.y() + 1, tile.depth(), wallMaterial);
						}
					}
				}
			}
			
			stopCorridor();
		}
	}
	
	public void addCellNorthEntrance(int cellx) {
		this.currentCell.markAsCellEntrance(DungeonCell.NORTH, cellx);
		
		if (this.currentCell.row() > 0)
			this.dungeon.cell(this.currentCell.column(), this.currentCell.row() - 1, this.currentCell.depth()).markAsCellEntrance(DungeonCell.SOUTH, cellx);
	}
	
	public void addCellSouthEntrance(int cellx) {
		this.currentCell.markAsCellEntrance(DungeonCell.SOUTH, cellx);
		
		if (this.currentCell.row() < this.dungeon.numCellsPerSide() - 1)
			this.dungeon.cell(this.currentCell.column(), this.currentCell.row() + 1, this.currentCell.depth()).markAsCellEntrance(DungeonCell.NORTH, cellx);
	}

	public void addCellEastEntrance(int celly) {
		this.currentCell.markAsCellEntrance(DungeonCell.EAST, celly);
		
		if (this.currentCell.column() < this.dungeon.numCellsPerSide() - 1)
			this.dungeon.cell(this.currentCell.column() + 1, this.currentCell.row(), this.currentCell.depth()).markAsCellEntrance(DungeonCell.WEST, celly);
	}
	
	public void addCellWestEntrance(int celly) {
		this.currentCell.markAsCellEntrance(DungeonCell.WEST, celly);
		
		if (this.currentCell.column() > 0)
			this.dungeon.cell(this.currentCell.column() - 1, this.currentCell.row(), this.currentCell.depth()).markAsCellEntrance(DungeonCell.EAST, celly);
	}
	
	public List<Point> getNorthCellEntrances() {
		return this.currentCell.entrances(DungeonCell.NORTH);
	}
	
	public List<Point> getSouthCellEntrances() {
		return this.currentCell.entrances(DungeonCell.SOUTH);
	}

	public List<Point> getEastCellEntrances() {
		return this.currentCell.entrances(DungeonCell.EAST);
	}
	
	public List<Point> getWestCellEntrances() {
		return this.currentCell.entrances(DungeonCell.WEST);
	}
	
	public List<Point> getNotNorthCellEntrances() {
		List<Point> entrances = new ArrayList<Point>();
		entrances.addAll(getSouthCellEntrances());
		entrances.addAll(getEastCellEntrances());
		entrances.addAll(getWestCellEntrances());
		
		return entrances;
	}
	
	public List<Point> getNotSouthCellEntrances() {
		List<Point> entrances = new ArrayList<Point>();
		entrances.addAll(getNorthCellEntrances());
		entrances.addAll(getEastCellEntrances());
		entrances.addAll(getWestCellEntrances());
		
		return entrances;
	}

	public List<Point> getNotEastCellEntrances() {
		List<Point> entrances = new ArrayList<Point>();
		entrances.addAll(getSouthCellEntrances());
		entrances.addAll(getNorthCellEntrances());
		entrances.addAll(getWestCellEntrances());
		
		return entrances;
	}
	
	public List<Point> getNotWestCellEntrances() {
		List<Point> entrances = new ArrayList<Point>();
		entrances.addAll(getSouthCellEntrances());
		entrances.addAll(getEastCellEntrances());
		entrances.addAll(getNorthCellEntrances());
		
		return entrances;
	}
	
	public List<Point> entrances() {
		List<Point> entrances = new ArrayList<Point>();
		entrances.addAll(getSouthCellEntrances());
		entrances.addAll(getEastCellEntrances());
		entrances.addAll(getNorthCellEntrances());
		entrances.addAll(getWestCellEntrances());
		
		return entrances;
	}
	
	public void executeEvent(String eventId, NativeObject args) {
		ScriptExecutor executor = ScriptExecutor.executor();
		BuildEventTemplate eventTemplate = this.dungeonsArchitect.buildEventsTemplates().get(eventId);
		if (eventTemplate != null) {
			//set function args
			this.executorScope.put("_args_", args);
			executor.executeScript(eventTemplate.buildScript(), this.executorScope);
			//rest function args
			this.executorScope.put("_args_", null);
		}
	}
	
	/* *** */
	
	private void startCorridor() {
		this.corridorFlag = true;
	}
	
	private void stopCorridor() {
		this.corridorFlag = false;
	}
	
	private void startRoom() {
		this.roomFlag = true;
	}
	
	private void stopRoom() {
		this.roomFlag = false;
	}

	public void setDungeon(Dungeon dungeon) {
		this.dungeon = dungeon;
	}
}
