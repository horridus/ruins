package cek.ruins.events;

import java.util.HashMap;
import java.util.Map;

public class Event {
	String namespace;
	int name;
	
	int day;
	int month;
	int year;
	int season;
	
	Map<String, Object> data;

	public Event(String namespace, int name) {
		this.namespace = namespace;
		this.name = name;
		this.data = new HashMap<String, Object>();
	}
	
	public int name() {
		return this.name;
	}
	public int year() {
		return this.year;
	}
	public int season() {
		return this.season;
	}
	public int month() {
		return this.month;
	}
	public int day() {
		return this.day;
	}
	
	public void addData(String key, Object value) {
		this.data.put(key, value);
	}
}
