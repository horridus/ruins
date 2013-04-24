package cek.ruins.world.locations.dungeons.templates;

import org.mozilla.javascript.Script;

public class DungeonTemplate {
	protected String id;
	protected int size;
	protected int cells;
	protected int depth;
	protected Script initScript;
	protected Script buildScript;
	
	public String id() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int size() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int cells() {
		return cells;
	}
	public void setCells(int cells) {
		this.cells = cells;
	}
	public int depth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public Script initScript() {
		return initScript;
	}
	public void setInitScript(Script initScript) {
		this.initScript = initScript;
	}
	public Script buildScript() {
		return buildScript;
	}
	public void setBuildScript(Script buildScript) {
		this.buildScript = buildScript;
	}
}
