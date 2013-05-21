package cek.ruins.world.locations.dungeons;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;

import org.dom4j.Document;

import cek.ruins.world.locations.dungeons.entities.ObservableEntity;
import cek.ruins.world.locations.dungeons.entities.EntityComponent;
import cek.ruins.world.locations.dungeons.entities.EntityTemplate;
import cek.ruins.world.locations.dungeons.entities.components.ComponentMessageId;

public class Master {
	private DungeonsArchitect dungeonArchitect;
	private Random generator;
	private Dungeon dungeon;
	private Map<String, ObservableEntity> bredEntities;
	private Map<String, Object> executorScope;
	
	public Master(DungeonsArchitect dungeonArchitect, Random generator) {
		this.dungeonArchitect = dungeonArchitect;
		this.generator = generator;
		this.bredEntities = new HashMap<String, ObservableEntity>();
		this.executorScope = new HashMap<String, Object>();
	}
	
	public void setDungeon(Dungeon dungeon) {
		this.dungeon = dungeon;
	}
	
	public void setGenerator(Random generator) {
		this.generator = generator;
	}
	
	public void setExecutorScope(Map<String, Object> executorScope) {
		this.executorScope = executorScope;
	}
	
	public ObservableEntity breed(String id, int x, int y, int depth) throws Exception {
		EntityTemplate template = this.dungeonArchitect.entitiesTemplates().get(id);
		
		if (template != null) {
			ObservableEntity entity = new ObservableEntity();
			entity.setTemplateId(id);
			entity.setId(generateUniqueId());
			
			for (Entry<String, Document> componentEntry : template.components().entrySet()) {
				Class<?> componentClass = Class.forName(componentEntry.getKey());
				EntityComponent component = (EntityComponent) componentClass.newInstance();
				component.setOwnerEntity(entity);
				component.configure(componentEntry.getValue());
				
				entity.addComponent(component);
			}
			
			//notify to entity's components its own creation
			Map<String, Object> args = new HashMap<String, Object>();
			args.put("position:x", x);
			args.put("position:y", y);
			args.put("position:z", depth);
			
			entity.processMessage(ComponentMessageId.CREATION, args);
			
			//insert new entity in existing entities map
			this.bredEntities.put(entity.id(), entity);
			return entity;
		}
		else
			throw new Exception("Entity template " + id + " not found.");
	}
	
	public List<ObservableEntity> entities(int depth) {
		List<ObservableEntity> entities = new LinkedList<ObservableEntity>(); 
		for (Map.Entry<String, ObservableEntity> entityEntry : this.bredEntities.entrySet()) {
			ObservableEntity entity = entityEntry.getValue();
			if (entity.hasAttribute("position:z") && ((Integer)entity.attribute("position:z")) == depth) {
				entities.add(entity);
			}
		}
		
		return entities;
	}
	
	protected String generateUniqueId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}
	
	public void deleteEntity(String id) {
		this.bredEntities.remove(id);
	}
}
