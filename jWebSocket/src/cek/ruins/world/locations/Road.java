package cek.ruins.world.locations;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;
import cek.ruins.Point;

public class Road implements Marshallable {
	private List<Integer> path;
	private List<Point> pathCoords;

	public Road(List<Integer> path, List<Point> pathCoords) {
		this.path = path;
		this.pathCoords = pathCoords;
	}

	public List<Integer> path() {
		return this.path;
	}

	public List<Point> pathCoords() {
		return this.pathCoords;
	}

	@Override
	public UUID id() {
		return null;
	}

	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		BasicDBList jsonList = new BasicDBList();

		Iterator<Integer> pathIt = this.path.iterator();
		while (pathIt.hasNext()) {
			jsonList.add(pathIt.next());
		}
		builder.add("path", jsonList);

		jsonList = new BasicDBList();

		Iterator<Point> cpIt = this.pathCoords.iterator();
		while (cpIt.hasNext()) {
			jsonList.add(cpIt.next().toJSON());
		}
		builder.add("cps", jsonList);

		return builder.get();
	}
}
