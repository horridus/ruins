package cek.ruins.world.locations.dungeons.entities.components;

import java.util.Map;
import java.util.Observable;

import org.dom4j.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

import cek.ruins.world.locations.dungeons.entities.ObservableEntity;
import cek.ruins.world.locations.dungeons.entities.EntityComponent;

public class PositionComponent extends EntityComponent {

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		ObservableEntity owner = (ObservableEntity) o;
		Map<String, Object> updateArgs = (Map<String, Object>) arg;
		
		ComponentMessageId msgId = (ComponentMessageId) updateArgs.get("_messageid_");
		switch (msgId) {
		case CREATION:
			if (updateArgs.containsKey("position:x"))
				owner.setAttribute("position:x", updateArgs.get("position:x"));
			
			if (updateArgs.containsKey("position:y"))
				owner.setAttribute("position:y", updateArgs.get("position:y"));
			
			if (updateArgs.containsKey("position:z"))
				owner.setAttribute("position:z", updateArgs.get("position:z"));
			break;

		default:
			break;
		}
		
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
