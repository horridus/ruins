package cek.ruins;

import java.util.UUID;

import com.mongodb.DBObject;

public interface Marshallable {
	public UUID id();
	public DBObject toJSON();
}
