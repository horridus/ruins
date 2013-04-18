package cek.ruins.world.locations.dungeons.entities;

import java.util.Observer;

import org.dom4j.Document;

public abstract class EntityComponent implements Observer {
	public EntityComponent() {
	
	}
	
	public abstract void configure(Document configuration);
}
