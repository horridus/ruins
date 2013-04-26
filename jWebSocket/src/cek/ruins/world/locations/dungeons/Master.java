package cek.ruins.world.locations.dungeons;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;

import org.dom4j.Document;

import cek.ruins.world.locations.dungeons.entities.Entity;
import cek.ruins.world.locations.dungeons.entities.EntityComponent;
import cek.ruins.world.locations.dungeons.entities.EntityTemplate;

public class Master {
	private DungeonMaster dungeonMaster;
	private Random generator;
	private Map<String, Entity> bredEntities;
	
	public Master(DungeonMaster dungeonMaster, Random generator, Map<String, Object> executorScope) {
		this.dungeonMaster = dungeonMaster;
		this.generator = generator;
		this.bredEntities = new HashMap<String, Entity>();
	}
	
	public Entity breed(String id) throws Exception {
		EntityTemplate template = this.dungeonMaster.entitiesTemplates().get(id);
		
		if (template != null) {
			Entity entity = new Entity();
			entity.setTemplateId(id);
			entity.setId(generateUniqueId());
			
			for (Entry<String, Document> componentEntry : template.components().entrySet()) {
				Class<?> componentClass = Class.forName(componentEntry.getKey());
				EntityComponent component = (EntityComponent) componentClass.newInstance();
				component.setOwnerEntity(entity);
				component.configure(componentEntry.getValue());
				
				entity.addComponent(component);
			}
			
			//insert new entity in existing entities map
			this.bredEntities.put(entity.id(), entity);
			return entity;
		}
		else
			throw new Exception("Entity template " + id + " not found.");
	}
	
	protected String generateUniqueId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}
	
	public void deleteEntity(String id) {
		this.bredEntities.remove(id);
	}
}
