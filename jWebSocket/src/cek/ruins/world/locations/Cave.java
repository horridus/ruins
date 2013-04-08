package cek.ruins.world.locations;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class Cave extends Location {
	public Cave() {
		super();

	}

	public Cave(String name, int x, int y, int regionIndex) {
		super(name, x, y, regionIndex);
	}

	@Override
	public DBObject toJSON() {
		DBObject location = super.toJSON();

		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start(location.toMap());

		return builder.get();
	}
}
