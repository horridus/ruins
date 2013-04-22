package cek.ruins.world.locations.dungeons.entities.components;

import java.util.Observable;

import org.dom4j.Document;
import org.dom4j.Element;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

import cek.ruins.world.locations.dungeons.entities.EntityComponent;

public class DrawableComponent extends EntityComponent {

	@Override
	public void update(Observable subject, Object event) {}

	@Override
	public void configure(Document configuration) throws Exception {
		Element img = (Element) configuration.selectSingleNode("/drawable/img"); 
		if (img != null) {
			getOwnerEntity().setAttribute("drawable:img", img.getTextTrim());
		}
		else
			throw new Exception("Missing img element in drawable, entity id ");
	}

	@Override
	public void statusToJSON(BasicDBObjectBuilder builder) {
		BasicDBObject drawable = new BasicDBObject();
		drawable.put("img", getOwnerEntity().attribute("drawable:img"));
		
		builder.add("drawable", drawable);
	}

}
