package cek.ruins.world.locations.settlements;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Script;

public class SettlementDistrict {
	protected String category;
	protected String id;
	
	protected List<Script> onCreationEffects;
	protected List<Script> onDestructionEffects;
	
	public SettlementDistrict(String category, String id) {
		this.category = category;
		this.id = id;
		
		this.onCreationEffects = new LinkedList<Script>();
		this.onDestructionEffects = new LinkedList<Script>();
	}
}
