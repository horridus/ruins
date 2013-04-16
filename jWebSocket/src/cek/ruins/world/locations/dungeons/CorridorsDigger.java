package cek.ruins.world.locations.dungeons;

import java.util.ArrayList;
import java.util.List;

import cek.ruins.utils.AStar;

public class CorridorsDigger extends AStar<DungeonTile> {
	private Dungeon dungeon;
	private DungeonCell cell;
	private DungeonTile start;
	private DungeonTile goal;
	
	public CorridorsDigger(Dungeon dungeon, DungeonCell cell) {
		this.dungeon = dungeon;
		this.cell = cell;
	}

	public List<DungeonTile> dig(DungeonTile start, DungeonTile goal) {
		this.goal = goal;
		this.start = start;
		
		return this.compute(this.start);
	}
	
	@Override
	protected boolean isGoal(DungeonTile tile) {
		if (tile.x() == goal.x() && tile.y() == goal.y())
			return true;
		else
			return false;
	}

	@Override
	protected Double g(DungeonTile from, DungeonTile to) {
		if (from.x() == to.x() && from.y() == to.y())
			return 0.0;
		else {
			if ((to.material().equals(Materials.NOMATERIAL())) || to.isCorridor())
				return 1.0;
			else
				return Double.MAX_VALUE;
		}
	}

	@Override
	protected Double h(DungeonTile from, DungeonTile to) {
		double distance = Math.sqrt(Math.pow(from.x() - goal.x(), 2.0) + Math.pow(from.y() - goal.y(), 2.0));
		
		//double distance = Math.abs(from.x() - goal.x()) + Math.abs(from.y() - goal.y());
		return distance;
	}

	@Override
	protected List<DungeonTile> generateSuccessors(DungeonTile tile) {
		List<DungeonTile> successors = new ArrayList<DungeonTile>();
		
		int cellBoundX0 = this.cell.column() * this.cell.size();
		int cellBoundX1 = ((this.cell.column() + 1) * this.cell.size()) - 1;
		int cellBoundY0 = this.cell.row() * this.cell.size();
		int cellBoundY1 = ((this.cell.row() + 1) * this.cell.size()) - 1;
		
		if (tile.x() > cellBoundX0) {
			DungeonTile successor = dungeon.tile(tile.x() - 1, tile.y(), tile.depth());
			
			if (successor.material.equals(Materials.NOMATERIAL()) || successor.isEntrance() || successor.isCorridor())
				successors.add(successor);
		}
		
		if (tile.x() < cellBoundX1) {
			DungeonTile successor = dungeon.tile(tile.x() + 1, tile.y(), tile.depth());
			
			if (successor.material.equals(Materials.NOMATERIAL()) || successor.isEntrance() || successor.isCorridor())
				successors.add(successor);
		}
		
		if (tile.y() > cellBoundY0) {
			DungeonTile successor = dungeon.tile(tile.x(), tile.y() - 1, tile.depth());
			
			if (successor.material.equals(Materials.NOMATERIAL()) || successor.isEntrance() || successor.isCorridor())
				successors.add(successor);
		}
		
		if (tile.y() < cellBoundY1) {
			DungeonTile successor = dungeon.tile(tile.x(), tile.y() + 1, tile.depth());
			
			if (successor.material.equals(Materials.NOMATERIAL()) || successor.isEntrance() || successor.isCorridor())
				successors.add(successor);
		}
		
		return successors;
	}

}
