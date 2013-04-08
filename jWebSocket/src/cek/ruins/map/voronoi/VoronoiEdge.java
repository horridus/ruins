package cek.ruins.map.voronoi;

import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;
import cek.ruins.Point;

public class VoronoiEdge implements Marshallable {
	//Delaunay edge
	public int site0, site1;
	
	//Voronoi edge
	public int corner0, corner1;
	
	public Point midpoint;
	
	public VoronoiEdge() {
		this.site0 = this.site1 = this.corner0 = this.corner1 = -1;
		this.midpoint = new Point();
	}

	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();
		
		builder.add("site0", this.site0);
		builder.add("site1", this.site1);
		
		builder.add("corner0", this.corner0);
		builder.add("corner1", this.corner1);
		
		builder.add("midpoint", midpoint.toJSON());
		
		return builder.get();
	}

	@Override
	public UUID id() {
		// TODO Auto-generated method stub
		return null;
	}
}
