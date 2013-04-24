package cek.ruins.world.locations.dungeons.templates;

import org.mozilla.javascript.Script;

public class BuildEventTemplate {
	protected String id;
	protected Script buildScript;
	
	public String id() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Script buildScript() {
		return buildScript;
	}
	public void setBuildScript(Script buildScript) {
		this.buildScript = buildScript;
	}
}
