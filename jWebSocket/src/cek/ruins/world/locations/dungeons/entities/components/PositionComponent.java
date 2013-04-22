package cek.ruins.world.locations.dungeons.entities.components;

import java.util.Observable;

import org.dom4j.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

import cek.ruins.world.locations.dungeons.entities.EntityComponent;

public class PositionComponent extends EntityComponent {

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configure(Document configuration) throws Exception {
		getOwnerEntity().setAttribute("position:x", 0);
		getOwnerEntity().setAttribute("position:y", 0);
		getOwnerEntity().setAttribute("position:z", 0);
	}

	@Override
	public void statusToJSON(BasicDBObjectBuilder builder) {
		BasicDBObject position = new BasicDBObject();
		position.put("x", getOwnerEntity().attribute("position:x"));
		position.put("y", getOwnerEntity().attribute("position:y"));
		position.put("z", getOwnerEntity().attribute("position:z"));
		
		builder.add("position", position);
	}

}
