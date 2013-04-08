package cek.ruins.map.voronoi;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;
import cek.ruins.Point;

public class VoronoiSite implements Marshallable {
	public Point center;
	public int area;
	public List<Integer> corners; //corners
	public List<Integer> neighbors; //sites
	public List<Integer> borders; //edges

	public int index;

	public VoronoiSite() {
		this.center = new Point();
		this.area = 0;
		this.corners = new Vector<Integer>();
		this.neighbors = new Vector<Integer>();
		this.borders = new Vector<Integer>();

		this.index = -1;
	}

	public VoronoiSite(int index) {
		this();

		this.index = index;
	}

	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		BasicDBList jsonList = new BasicDBList();

		builder.add("center", this.center.toJSON());

		jsonList.addAll(this.corners);
		builder.add("corners", jsonList);

		jsonList = new BasicDBList();
		jsonList.addAll(this.neighbors);
		builder.add("neighbors", jsonList);

		jsonList = new BasicDBList();
		jsonList.addAll(this.borders);
		builder.add("borders", jsonList);

		return builder.get();
	}

	@Override
	public UUID id() {
		return null;
	}
}
