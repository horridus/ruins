package cek.ruins.world.locations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Locations {
	
	private class RoadsMapEdge extends DefaultEdge {
		private static final long serialVersionUID = 1L;
		private Road road;
		
		public RoadsMapEdge(Road road) {
			this.road = road;
		}
		
		public Road road() {
			return this.road;
		}
	}
	
	private Map<Integer, Map<UUID, Settlement>> regionToSettlements;
	private UndirectedGraph<UUID, RoadsMapEdge> roadsMap;

	
	private Map<UUID, Settlement> settlements;
	private Map<UUID, Cave> caves;

	public Locations() {
		this.settlements = new HashMap<UUID, Settlement>();
		this.caves = new HashMap<UUID, Cave>();

		this.regionToSettlements = new HashMap<Integer, Map<UUID,Settlement>>();
		this.roadsMap = new SimpleGraph<UUID, RoadsMapEdge>(RoadsMapEdge.class);
	}

	public Map<UUID, Settlement> settlements() {
		return this.settlements;
	}

	public void addSettlement(Settlement settlement) {
		this.settlements.put(settlement.id(), settlement);
		if (!this.regionToSettlements.containsKey(settlement.regionIndex))
			this.regionToSettlements.put(settlement.regionIndex, new HashMap<UUID, Settlement>());

		this.regionToSettlements.get(settlement.regionIndex).put(settlement.id(), settlement);
	}

	public void removeSettlement(Settlement settlement) {
		this.settlements.remove(settlement.id());
		this.regionToSettlements.get(settlement.regionIndex).remove(settlement.id());
	}

	public Map<UUID, Settlement> settelementsInRegion(int regionIndex) {
		if (!this.regionToSettlements.containsKey(regionIndex))
			this.regionToSettlements.put(regionIndex, new HashMap<UUID, Settlement>());

		return regionToSettlements.get(regionIndex);
	}

	public void addCave(Cave cave) {
		this.caves.put(cave.id(), cave);
	}

	public void removeCave(Cave cave) {
		this.caves.remove(cave.id());
	}
	
	public void addRoad(Road road) {
		//TODO
	}
}
