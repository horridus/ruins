package cek.ruins.map.voronoi;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;
import cek.ruins.Point;

public class VoronoiCorner implements Marshallable {
	public Point point;
	public boolean border;

	public List<Integer> touches; //sites
	public List<Integer> adjacent; //corners
	public List<Integer> protudes; //edges

	public VoronoiCorner() {
		this.point = new Point();
		this.border = false;

		this.touches = new Vector<Integer>();
		this.adjacent = new Vector<Integer>();
		this.protudes = new Vector<Integer>();
	}

	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		builder.add("point", this.point.toJSON());
		builder.add("border", this.border);

		BasicDBList jsonList = new BasicDBList();

		jsonList.addAll(this.touches);
		builder.add("touches", jsonList);

		jsonList = new BasicDBList();
		jsonList.addAll(this.adjacent);
		builder.add("adjacent", jsonList);

		jsonList = new BasicDBList();
		jsonList.addAll(this.protudes);
		builder.add("protudes", jsonList);

		return builder.get();
	}

	@Override
	public UUID id() {
		// TODO Auto-generated method stub
		return null;
	}
}
