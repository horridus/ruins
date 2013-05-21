package cek.ruins.world.locations.dungeons.entities;

import java.util.Observer;

import org.dom4j.Document;

import com.mongodb.BasicDBObjectBuilder;

public abstract class EntityComponent implements Observer {
	private ObservableEntity ownerEntity;
	
	public void setOwnerEntity(ObservableEntity ownerEntity) {
		this.ownerEntity = ownerEntity;
	}
	public ObservableEntity getOwnerEntity() {
		return ownerEntity;
	}
	
	public abstract void configure(Document configuration) throws Exception;
	public abstract void statusToJSON(BasicDBObjectBuilder builder);
}
