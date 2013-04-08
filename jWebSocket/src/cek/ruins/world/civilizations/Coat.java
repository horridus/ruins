package cek.ruins.world.civilizations;

import java.util.UUID;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import cek.ruins.Marshallable;

public class Coat implements Marshallable {
	private int index;
	private String primaryColor;
	private String secondaryColor;
	private String pattern;
	private String flag;
	
	public Coat(int index, String primaryColor, String secondaryColor, String pattern, String flag) {
		this.index = index;
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		this.pattern = pattern;
		this.flag = flag;
	}
	
	public int index() {
		return this.index;
	}
	
	public String primaryColor() {
		return this.primaryColor;
	}
	
	public String secondaryColor() {
		return this.secondaryColor;
	}
	
	public String pattern() {
		return this.pattern;
	}
	
	public String flag() {
		return this.flag;
	}
	
	@Override
	public UUID id() {
		return null;
	}

	@Override
	public DBObject toJSON() {
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start();

		builder.add("primary", this.primaryColor);
		builder.add("secondary", this.secondaryColor);
		builder.add("pattern", this.pattern);
		builder.add("flag", this.flag);

		return builder.get();
	}

}
