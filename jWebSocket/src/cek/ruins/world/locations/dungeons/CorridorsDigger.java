package cek.ruins.world.locations.dungeons;

import java.util.ArrayList;
import java.util.List;

import cek.ruins.utils.AStar;

public class CorridorsDigger extends AStar<DungeonTile> {
	private Dungeon dungeon;
	private DungeonTile start;
	private DungeonTile goal;
	private int x0, y0, x1, y1;
	
	public CorridorsDigger(Dungeon dungeon, int x0, int y0, int x1, int y1) {
		this.dungeon = dungeon;
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
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
			if (!to.isRoom() || to.isCorridor() || isGoal(to))
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
		
		int cellBoundX0 = Math.min(x0, x1);
		int cellBoundX1 = Math.max(x0, x1);
		int cellBoundY0 = Math.min(y0, y1);
		int cellBoundY1 = Math.max(y0, y1);
		
		if (tile.x() > cellBoundX0) {
			DungeonTile successor = dungeon.tile(tile.x() - 1, tile.y(), tile.depth());
			
			if (!successor.isRoom() || successor.isEntrance() || successor.isCorridor() || isGoal(successor))
				successors.add(successor);
		}
		
		if (tile.x() < cellBoundX1) {
			DungeonTile successor = dungeon.tile(tile.x() + 1, tile.y(), tile.depth());
			
			if (!successor.isRoom() || successor.isEntrance() || successor.isCorridor() || isGoal(successor))
				successors.add(successor);
		}
		
		if (tile.y() > cellBoundY0) {
			DungeonTile successor = dungeon.tile(tile.x(), tile.y() - 1, tile.depth());
			
			if (!successor.isRoom() || successor.isEntrance() || successor.isCorridor() || isGoal(successor))
				successors.add(successor);
		}
		
		if (tile.y() < cellBoundY1) {
			DungeonTile successor = dungeon.tile(tile.x(), tile.y() + 1, tile.depth());
			
			if (!successor.isRoom() || successor.isEntrance() || successor.isCorridor() || isGoal(successor))
				successors.add(successor);
		}
		
		return successors;
	}

}
