package cek.ruins.world.locations.dungeons;

import cek.ruins.world.locations.dungeons.materials.Material;
import cek.ruins.world.locations.dungeons.materials.Materials;

public class DungeonTile {
	protected Material material;
	protected int x, y, depth;
	protected boolean isEntrance;
	private boolean isCorridor;
	private boolean isRoom;
	//private boolean isPassable;
	
	public DungeonTile(int x, int y, int depth) {
		this.material = Materials.NOMATERIAL();
		this.x = x;
		this.y = y;
		this.depth = depth;
		this.isEntrance = false;
		this.isCorridor = false;
		this.isRoom = false;
		//this.isPassable = false;
	}

	public Material material() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public int x() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int y() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public int depth() {
		return this.depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public boolean isEntrance() {
		return isEntrance;
	}

	public void setEntrance(boolean isEntrance) {
		this.isEntrance = isEntrance;
	}
	
	public boolean isCorridor() {
		return isCorridor;
	}

	public void setCorridor(boolean isCorridor) {
		this.isCorridor = isCorridor;
	}
	
	public boolean isRoom() {
		return isRoom;
	}

	public void setRoom(boolean isRoom) {
		this.isRoom = isRoom;
	}
	
//	public boolean isPassable() {
//		return isPassable;
//	}
//
//	public void setPassable(boolean isPassable) {
//		this.isPassable = isPassable;
//	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DungeonTile) {
			DungeonTile dt = (DungeonTile) obj;
			if (dt.x == this.x && dt.y == this.y)
				return true;
		}
		
		return false;
	}
}
