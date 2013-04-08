package cek.ruins.map.voronoi;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;

public class VoronoiGraph implements Marshallable {
	private List<VoronoiSite> sites;
	private List<VoronoiCorner> corners;
	private List<VoronoiEdge> edges;

	public VoronoiGraph() {
		this.sites = new Vector<VoronoiSite>();
		this.corners = new Vector<VoronoiCorner>();
		this.edges = new Vector<VoronoiEdge>();
	}

	public List<VoronoiSite> sites() {
		return this.sites;
	}

	public List<VoronoiCorner> corners() {
		return this.corners;
	}

	public List<VoronoiEdge> edges() {
		return this.edges;
	}

	public Integer lookupEdgeFromCorner(VoronoiCorner corner, Integer oppositeCornerIdx, Integer temp) { //FIXME rimuovere temp quando risolto problemi edge che non centrano un cazzo
		Iterator<Integer> edgesIt = corner.protudes.iterator();
		while (edgesIt.hasNext()) {
			int edgeIdx = edgesIt.next();
			VoronoiEdge edge = this.edges.get(edgeIdx);

			if ((edge.corner0 == oppositeCornerIdx && edge.corner1 == temp)|| (edge.corner1 == oppositeCornerIdx && edge.corner0 == temp))
				return edgeIdx;

		 }

		 return null;
	}

	public Integer lookupEdgeFromSites(VoronoiSite site0, Integer site0Index, Integer site1Index) {
		Iterator<Integer> edgesIt = site0.borders.iterator();
		while (edgesIt.hasNext()) {
			int edgeIdx = edgesIt.next();
			VoronoiEdge edge = this.edges.get(edgeIdx);

			if ((edge.site0 == site0Index && edge.site1 == site1Index) || (edge.site0 == site1Index && edge.site1 == site0Index))
				return edgeIdx;
		}

		return null;
	}

	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		BasicDBList jsonList = new BasicDBList();

		Iterator<VoronoiSite> sitesIt = this.sites.iterator();
		while (sitesIt.hasNext()) {
			jsonList.add(sitesIt.next().toJSON());
		}
		builder.add("sites", jsonList);

		jsonList = new BasicDBList();
		Iterator<VoronoiCorner> cornersIt = this.corners.iterator();
		while (cornersIt.hasNext()) {
			jsonList.add(cornersIt.next().toJSON());
		}
		builder.add("corners", jsonList);

		jsonList = new BasicDBList();
		Iterator<VoronoiEdge> edgesIt = this.edges.iterator();
		while (edgesIt.hasNext()) {
			jsonList.add(edgesIt.next().toJSON());
		}
		builder.add("edges", jsonList);

		return builder.get();
	}

	@Override
	public UUID id() {
		// TODO Auto-generated method stub
		return null;
	}
}
