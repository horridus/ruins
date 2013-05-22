package cek.ruins.world.locations.dungeons;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;

import cek.ruins.Point;
import cek.ruins.ScriptExecutor;
import cek.ruins.world.locations.dungeons.entities.ObservableEntity;
import cek.ruins.world.locations.dungeons.entities.EntityComponent;
import cek.ruins.world.locations.dungeons.entities.EntityTemplate;
import cek.ruins.world.locations.dungeons.entities.components.ComponentMessageId;
import cek.ruins.world.locations.dungeons.materials.Material;
import cek.ruins.world.locations.dungeons.materials.Materials;
import cek.ruins.world.locations.dungeons.templates.BuildEventTemplate;
import cek.ruins.world.locations.dungeons.templates.RoomTemplate;

//import com.infomatiq.jsi.rtree.RTree;

public class Digger extends ScriptableObject {
	private static final long serialVersionUID = 1L;
	
	private DungeonsArchitect dungeonsArchitect;
	private Dungeon dungeon;
	private Point iterator;
	private DungeonCell currentCell;
	private List<List<List<DungeonCell>>> cells;
	private Random generator;
	private boolean corridorFlag;
	private boolean roomFlag;
	private Map<String, Object> executorScope;
	
	//private RTree roomsTree;
	
	public void setDungeon(Dungeon dungeon) {
		this.dungeon = dungeon;
		
		//reset dungeon's cells list.
		cells.clear();
		
		for (int d = 0; d < this.dungeon.depth(); d++) {
			List<List<DungeonCell>> cellsLevel = new Vector<List<DungeonCell>>();
			for (int n = 0; n < this.dungeon.numCellsPerSide(); n++) {
				List<DungeonCell> row = new Vector<DungeonCell>();
				for (int r = 0; r < this.dungeon.numCellsPerSide(); r++) {
					DungeonCell cell = new DungeonCell(r, n, d, this.dungeon.size()/this.dungeon.numCellsPerSide());
					row.add(cell);
				}
				cellsLevel.add(row);
			}
			
			this.cells.add(cellsLevel);
		}
	}
	
	public void setGenerator(Random generator) {
		this.generator = generator;
	}
	
	public void setExecutorScope(Map<String, Object> executorScope) {
		this.executorScope = executorScope;
	}
	
	public List<ObservableEntity> entities(int depth) {
		return this.dungeon.entities(depth);
	}
	
	public void setPosition(int column, int row, int depth) {
		this.currentCell = this.cell(column, row, depth);
	}
	
	/* javascript side methods */
	
	@Override
	public String getClassName() {
		return Digger.class.getSimpleName();
	}
	
	//this exists only for being called from ScriptableObject.defineClass method 
	public Digger() throws Exception {}
	
	public void jsConstructor(Object dungeonsArchitect, Object generator) {
		this.dungeonsArchitect = (DungeonsArchitect) dungeonsArchitect;
		this.generator = (Random) generator;
		this.corridorFlag = false;
		this.roomFlag = false;
		this.executorScope = new HashMap<String, Object>();
		this.cells = new Vector<List<List<DungeonCell>>>();
		
		//this.roomsTree = new RTree();
		//this.roomsTree.init(null);
	}
	
	/**
	 * Gets number of tiles per side.
	 * @return number of tiles per side.
	 */
	public int jsGet_size() {
		return this.dungeon.size();
	}
	
	/**
	 * Gets number of cells per side.
	 * @return number of cells per side.
	 */
	public int jsGet_cellsnum() {
		return this.dungeon.numCellsPerSide();
	}
	
	/**
	 * Gets number of tiles per cell's side.
	 * @return number of tiles per cell's side.
	 */
	public int jsGet_cellsize() {
		return this.cellSize();
	}
	
	/**
	 * Gets maximum depth of current dungeon.
	 * @return maximum depth of dungeon.
	 */
	public int jsGet_dungeondepth() {
		return this.dungeon.depth();
	}
	
	/**
	 * Returns digger's current depth.
	 * @return digger's current depth.
	 */
	public int jsGet_depth() {
		return this.currentCell.depth();
	}
	
	/**
	 * Returns digger's current x coordinate.
	 * @return digger's current x coordinate.
	 */
	public int jsGet_x() {
		return this.currentCell.column();
	}
	
	/**
	 * Returns digger's current y coordinate.
	 * @return digger's current y coordinate.
	 */
	public int jsGet_y() {
		return this.currentCell.row();
	}
	
	/**
	 * Moves digger to the cell at coordinates (x,y,depth).
	 * @param x
	 * @param y
	 * @param depth
	 */
	public void jsFunction_move(int x, int y, int depth) {
		this.setPosition(x, y, depth);
	}
	
	/**
	 * Moves digger to the first cell at depth <code>depth</code>.
	 * @param depth
	 */
	public void jsFunction_toDepth(int depth) {
		this.setPosition(0, 0, depth);
	}
	
	/**
	 * Sets selected tile's material to <code>material</code>.
	 * @param x related to whole map, NOT to cell internal coordinates.
	 * @param y related to whole map, NOT to cell internal coordinates.
	 * @param depth
	 * @param material
	 */
	public void jsFunction_digTile(int x, int y, int depth, Object material) {
		this.digTile(x, y, depth, (Material) Context.jsToJava(material, Material.class));
	}
	
	/**
	 * Returns a random integer between [min,max). 
	 * @param min
	 * @param max
	 * @return a random integer between [min,max).
	 */
	public int jsFunction_randomI(int min, int max) {
		return this.randomI(min, max);
	}
	
	/**
	 * Returns a random integer between [0,100). 
	 * @return a random integer between [0,100).
	 */
	public int jsFunction_roll() {
		return this.randomI(0, 100);
	}
	
	/**
	 * Execute an event.
	 * @param eventId event id.
	 * @param args optional arguments to called event script.
	 */
	public void jsFunction_event(String eventId, NativeObject args) {
		this.event(eventId, args);
	}
	
	/**
	 * Create a room at current cell's position.
	 * @param roomId room id.
	 * @param args optional arguments to called room script.
	 */
	public void jsFunction_createRoom(String roomId, NativeObject args) {
		this.createRoom(roomId, args);
	}
	
	/**
	 * Sets selected tile's material to <code>material</code>.
	 * @param cellx x related to current cell internal coordinates.
	 * @param celly y related to current cell internal coordinates.
	 * @param material
	 */
	public void jsFunction_dig(int cellx, int celly, Material material) {
		this.digCellTile(cellx, celly, material);
	}
	
	/**
	 * Returns tile's material.
	 * @param cellx x related to current cell internal coordinates.
	 * @param celly y related to current cell internal coordinates.
	 * @return
	 */
	public Material jsFunction_material(int cellx, int celly) {
		return this.cellTileMaterial(cellx, celly);
	}
	
	/**
	 * Returns an array of all passable tiles in current cell.
	 * @return an array of tiles.
	 */
	public NativeArray jsFunction_passable() {
		return this.passable();
	}
	
	/**
	 * Spawns a new entity.
	 * @param templateId entity's template id.
	 * @param cellx x related to current cell internal coordinates.
	 * @param celly y related to current cell internal coordinates.
	 * @return the spawned entity.
	 * @throws Exception
	 */
	public ObservableEntity jsFunction_breed(String templateId, int cellx, int celly) throws Exception {
		return this.breed(templateId, cellx, celly);
	}
	
	/**
	 * Digs a corridor between two point in a cell.
	 * @param cellx0 x related to current cell internal coordinates.
	 * @param celly0 y related to current cell internal coordinates.
	 * @param cellx1 x related to current cell internal coordinates.
	 * @param celly1 y related to current cell internal coordinates.
	 * @param material material used to build the corridor.
	 * @param wallMaterial material used to build corridor's walls.
	 */
	public void jsFunction_corridor(int cellx0, int celly0, int cellx1, int celly1, Material material, Material wallMaterial) {
		int dungeonx0 = cellx0 + this.currentCell.column() * this.currentCell.size();
		int dungeony0 = celly0 + this.currentCell.row() * this.currentCell.size();
		int dungeonx1 = cellx1 + this.currentCell.column() * this.currentCell.size();
		int dungeony1 = celly1 + this.currentCell.row() * this.currentCell.size();
		
		this.digCorridor(dungeonx0, dungeony0, dungeonx1, dungeony1, this.currentCell.depth(), material, wallMaterial);
	}
	
	/**
	 * Mark tile as possible exit point for cells linking 
	 * @param cellx x related to current cell internal coordinates.
	 * @param celly y related to current cell internal coordinates.
	 */
	public void jsFunction_exit(int cellx, int celly) {
		int dungeonx = cellx + this.currentCell.column() * this.currentCell.size();
		int dungeony = celly + this.currentCell.row() * this.currentCell.size();
		this.currentCell.markAsExit(dungeonx, dungeony);
	}
	
	/**
	 * 
	 * @param column0
	 * @param row0
	 * @param column1
	 * @param row1
	 * @param material
	 * @param wallMaterial
	 * @throws Exception
	 */
	public void jsFunction_link(int column0, int row0, int column1, int row1, Material material, Material wallMaterial) throws Exception {
		DungeonCell cell0 = this.cell(column0, row0, this.currentCell.depth());
		DungeonCell cell1 = this.cell(column1, row1, this.currentCell.depth());
		this.linkCells(cell0, cell1, material, wallMaterial);
	}
	
	/**
	 * 
	 * @param name
	 * @param value
	 */
	public void jsFunction_remember(String name, Object value) {
		this.currentCell.remeber(name, value);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Object jsFunction_remember(String name) {
		return this.currentCell.remeber(name);
	}
	
	/**
	 * Digs a corridor that exits the cell from north side.
	 * @param startX x related to current cell internal coordinates.
	 * @param startY y related to current cell internal coordinates.
	 * @param material material used to build the corridor.
	 * @param wallMaterial material used to build corridor's walls.
	 */
	public void jsFunction_exitNorth(int startX, int startY, Material material, Material wallMaterial) {
	}
	
	/**
	 * Digs a corridor that exits the cell from north side.
	 * @param startX x related to current cell internal coordinates.
	 * @param startY y related to current cell internal coordinates.
	 * @param material material used to build the corridor.
	 * @param wallMaterial material used to build corridor's walls.
	 */
	public void jsFunction_exitSouth(int startX, int startY, Material material, Material wallMaterial) {
	}
	
	/**
	 * Digs a corridor that exits the cell from north side.
	 * @param startX x related to current cell internal coordinates.
	 * @param startY y related to current cell internal coordinates.
	 * @param material material used to build the corridor.
	 * @param wallMaterial material used to build corridor's walls.
	 */
	public void jsFunction_exitWest(int startX, int startY, Material material, Material wallMaterial) {
	}
	
	/**
	 * Digs a corridor that exits the cell from north side.
	 * @param startX x related to current cell internal coordinates.
	 * @param startY y related to current cell internal coordinates.
	 * @param material material used to build the corridor.
	 * @param wallMaterial material used to build corridor's walls.
	 */
	public void jsFunction_exitEast(int startX, int startY, Material material, Material wallMaterial) {
	}
	
	/* *** */
	
	/* scripts utility functions */
	public DungeonCell cell(int column, int row, int depth) {
		return this.cells.get(depth).get(column).get(row);
	}
	
	private void digCellTile(int cellx, int celly, Material material) {
		int dungeonx = cellx + this.currentCell.column() * this.currentCell.size();
		int dungeony = celly + this.currentCell.row() * this.currentCell.size();
		
		DungeonTile tile = tile(dungeonx, dungeony, this.currentCell.depth()); 
		tile.setMaterial(material);
		tile.setCorridor(this.corridorFlag);
		tile.setRoom(this.roomFlag);
	}
	
	private void digTile(int x, int y, int depth, Material material) {
		DungeonTile tile = tile(x, y, depth);
		tile.setMaterial(material);
		
		tile.setCorridor(this.corridorFlag);
		tile.setRoom(this.roomFlag);
	}
	
	private Material cellTileMaterial(int cellx, int celly) {
		int dungeonx = cellx + this.currentCell.column() * this.currentCell.size();
		int dungeony = celly + this.currentCell.row() * this.currentCell.size();
		
		return tile(dungeonx, dungeony, this.currentCell.depth()).material();
	}
	
	private DungeonTile tile(int x, int y, int depth) {
		return this.dungeon.tile(x, y, depth);
	}
	
	private int randomI(int min, int max) {
		if ((max - min) <= 0)
			return 0;
		else
			return this.generator.nextInt(max - min) + min;
	}
	
	private int cellSize() {
		return this.dungeon.size()/this.dungeon.numCellsPerSide();
	}
	
	private void createRoom(String roomId, NativeObject args) {
		ScriptExecutor executor = ScriptExecutor.executor();
		RoomTemplate roomTemplate = this.dungeonsArchitect.roomsTemplates().get(roomId);
		if (roomTemplate != null) {
			startRoom();
			
			//set function args
			this.executorScope.put("_args_", args);
			//generate room and fill cell
			executor.executeScript(roomTemplate.roomGenerator(), this.executorScope);
			//rest function args
			this.executorScope.put("_args_", null);
			
			stopRoom();
		}
	}
	
	private void event(String eventId, NativeObject args) {
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
	
	private ObservableEntity breed(String templateId, int cellx, int celly) throws Exception {
		EntityTemplate template = this.dungeonsArchitect.entitiesTemplates().get(templateId);
		
		if (template != null) {
			ObservableEntity entity = new ObservableEntity();
			entity.setTemplateId(templateId);
			entity.setId(generateUniqueId());
			
			for (Entry<String, Document> componentEntry : template.components().entrySet()) {
				Class<?> componentClass = Class.forName(componentEntry.getKey());
				EntityComponent component = (EntityComponent) componentClass.newInstance();
				component.setOwnerEntity(entity);
				component.configure(componentEntry.getValue());
				
				entity.addComponent(component);
			}
			
			int dungeonx = cellx + this.currentCell.column() * this.currentCell.size();
			int dungeony = celly + this.currentCell.row() * this.currentCell.size();
			
			//notify to entity's components its own creation
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("position:x", dungeonx);
			args.put("position:y", dungeony);
			args.put("position:z", this.currentCell.depth());
			
			entity.processMessage(ComponentMessageId.CREATION, args);
			
			//insert new entity in existing entities map
			this.dungeon.addEntity(entity);
			return entity;
		}
		else
			throw new Exception("Entity template " + templateId + " not found.");
	}
	
	private NativeArray passable() {
		List<Point> passableTiles = new ArrayList<Point>();
		
		for (int celly = 0; celly < this.cellSize(); celly++) {
			for (int cellx = 0; cellx < this.cellSize(); cellx++) {
				int dungeonx = cellx + this.currentCell.column() * this.currentCell.size();
				int dungeony = celly + this.currentCell.row() * this.currentCell.size();
				
				if (tile(dungeonx, dungeony, this.currentCell.depth()).material().isPassable())
					passableTiles.add(new Point(cellx, celly));
			}
		}
		
		NativeArray na = ScriptExecutor.executor().convertToJSArray(passableTiles.toArray(new Object[passableTiles.size()]));
		
		return na;
	}
	
	private void digCorridor(int dungeonx0, int dungeony0, int dungeonx1, int dungeony1, int depth, Material material, Material wallMaterial) {
		DungeonTile start = this.tile(dungeonx0, dungeony0, depth);
		DungeonTile goal = this.tile(dungeonx1, dungeony1, depth);
		
		DungeonCell cell0 = this.cell(dungeonx0/this.dungeon.numCellsPerSide(), dungeony0/this.dungeon.numCellsPerSide(), depth);
		DungeonCell cell1 = this.cell(dungeonx1/this.dungeon.numCellsPerSide(), dungeony1/this.dungeon.numCellsPerSide(), depth);
		
		int minx = Math.min(cell0.column()*cellSize(), cell1.column()*cellSize());
		int miny = Math.min(cell0.row()*cellSize(), cell1.row()*cellSize());
		int maxx = Math.max(cell0.column()*cellSize(), cell1.column()*cellSize());
		int maxy = Math.max(cell0.row()*cellSize(), cell1.row()*cellSize());
		
		CorridorsDigger corridorsDigger = new CorridorsDigger(this.dungeon, minx, miny, maxx + cellSize(), maxy + cellSize()); 
		List<DungeonTile> corridor = corridorsDigger.dig(start, goal);
		
		if (corridor != null) {
			startCorridor();
			
			for (DungeonTile tile : corridor) {
				digTile(tile.x(), tile.y(), tile.depth(), material);
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
						if (!neighbor.isRoom() && !neighbor.isCorridor()) {
							digTile(tile.x() - 1, tile.y(), tile.depth(), wallMaterial);
						}
					}
					
					if (tile.x() < this.dungeon.size() - 1 && skipDir != 2) {
						DungeonTile neighbor = this.tile(tile.x() + 1, tile.y(), tile.depth());
						if (!neighbor.isRoom() && !neighbor.isCorridor()) {
							digTile(tile.x() + 1, tile.y(), tile.depth(), wallMaterial);
						}
					}
					
					if (tile.y() > 0 && skipDir != 3) {
						DungeonTile neighbor = this.tile(tile.x(), tile.y() - 1, tile.depth());
						if (!neighbor.isRoom() && !neighbor.isCorridor()) {
							digTile(tile.x(), tile.y() - 1, tile.depth(), wallMaterial);
						}
					}
					
					if (tile.y() < this.dungeon.size() - 1 && skipDir != 4) {
						DungeonTile neighbor = this.tile(tile.x(), tile.y() + 1, tile.depth());
						if (!neighbor.isRoom() && !neighbor.isCorridor()) {
							digTile(tile.x(), tile.y() + 1, tile.depth(), wallMaterial);
						}
					}
					
					if (tile.x() > 0 && tile.y() > 0) {
						DungeonTile neighbor = this.tile(tile.x() - 1, tile.y() - 1, tile.depth());
						if (!neighbor.isRoom() && !neighbor.isCorridor()) {
							digTile(tile.x() - 1, tile.y() - 1, tile.depth(), wallMaterial);
						}
					}
					
					if (tile.x() > 0 && tile.y() < this.dungeon.size() - 1) {
						DungeonTile neighbor = this.tile(tile.x() - 1, tile.y() + 1, tile.depth());
						if (!neighbor.isRoom() && !neighbor.isCorridor()) {
							digTile(tile.x() - 1, tile.y() + 1, tile.depth(), wallMaterial);
						}
					}
					
					if (tile.x() < this.dungeon.size() - 1 && tile.y() > 0) {
						DungeonTile neighbor = this.tile(tile.x() + 1, tile.y() - 1, tile.depth());
						if (!neighbor.isRoom() && !neighbor.isCorridor()) {
							digTile(tile.x() + 1, tile.y() - 1, tile.depth(), wallMaterial);
						}
					}
					
					if (tile.x() < this.dungeon.size() - 1 && tile.y() < this.dungeon.size() - 1) {
						DungeonTile neighbor = this.tile(tile.x() + 1, tile.y() + 1, tile.depth());
						if (!neighbor.isRoom() && !neighbor.isCorridor()) {
							digTile(tile.x() + 1, tile.y() + 1, tile.depth(), wallMaterial);
						}
					}
				}
			}
			
			stopCorridor();
		}
	}
	
	private void linkCells(DungeonCell cell0, DungeonCell cell1, Material material, Material wallMaterial) throws Exception {
		if (cell0.depth() != cell1.depth())
			throw new Exception("cell0 and cell1 must lay at the same depth. cell0: " + cell0.depth() + " cell1: " + cell1.depth());
		
		Point selectedExit0 = null;
		Point selectedExit1 = null;
		
		List<Point> exits = cell0.exits();
		if (!exits.isEmpty())
			selectedExit0 = exits.get(randomI(0, exits.size()));
		
		exits = cell1.exits();
		if (!exits.isEmpty())
			selectedExit1 = exits.get(randomI(0, exits.size()));
		
		if (selectedExit0 != null && selectedExit1 != null)
			digCorridor((int)selectedExit0.x, (int)selectedExit0.y, (int)selectedExit1.x, (int)selectedExit1.y, cell0.depth(), material, wallMaterial);
	}
	
	private String generateUniqueId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}
	
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
	
	/*
	private boolean nextCell() {
		if (this.depth >= this.dungeon.depth())
			return false;
		
		if (this.iterator.y >= this.dungeon.numCellsPerSide())
			return false;
		
		this.currentCell = this.cell((int)this.iterator.x, (int)this.iterator.y, this.depth);
		
		if (this.iterator.x >= this.dungeon.numCellsPerSide() - 1) {
			this.iterator.x = 0;
			this.iterator.y += 1;
		}
		else
			this.iterator.x += 1;
		
		return true;
	}
	
	private void initCellsIterator(int depth) {
		this.iterator = new Point(0, 0);
		this.depth = depth;
	}
	
	private NativeArray room() {
		List<Point> roomTiles = new ArrayList<Point>();
		
		for (int celly = 0; celly < this.cellSize(); celly++) {
			for (int cellx = 0; cellx < this.cellSize(); cellx++) {
				int dungeonx = cellx + this.currentCell.column() * this.currentCell.size();
				int dungeony = celly + this.currentCell.row() * this.currentCell.size();
				
				if (tile(dungeonx, dungeony, this.currentCell.depth()).isRoom())
					roomTiles.add(new Point(cellx, celly));
			}
		}
		
		NativeArray na = ScriptExecutor.executor().convertToJSArray(roomTiles.toArray(new Object[roomTiles.size()]));
		
		return na;
	}
	
	private void deleteEntity(String id) {
		this.dungeon.deleteEntity(id);
	}
	*/
}
