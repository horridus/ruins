package cek.ruins.world.locations.dungeons;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.dom4j.Element;

import cek.ruins.XmlDocument;
import cek.ruins.events.Event;
import cek.ruins.events.EventsDispatcher;
import cek.ruins.world.locations.dungeons.entities.EntityTemplate;

public class DungeonMaster {
	private static String DEFAULT_COMPONENTS_NAMESPACE = "cek.ruins.world.locations.dungeons.entities.components";
	
	private List<EventsDispatcher> dispatchers;
	private Map<String, EntityTemplate> entitiesTemplates;
	
	public DungeonMaster() {}
	
	@SuppressWarnings("unchecked")
	public void loadData(String path) throws Exception {
		this.entitiesTemplates = new HashMap<String, EntityTemplate>();
		
		//load entities templates
		File enititesTemplatesDirectory = new File(path);
		File[] templateFiles = enititesTemplatesDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		for (File templateFile : templateFiles) {
			if (templateFile.exists() && templateFile.isFile()) {
				XmlDocument entitiesTemplate = new XmlDocument(FileUtils.readFileToString(templateFile, "UTF-8"));
				
				Iterator<Element> entitiesIt = (Iterator<Element>) entitiesTemplate.selectNodes("/entities/entity").iterator();
				
				if (!entitiesIt.hasNext()) {
					throw new Exception(path + File.separator + templateFile.getName() + " malformed: no element <entity> found.");
				}
				
				while (entitiesIt.hasNext()) {
					Element entity = entitiesIt.next();
					String id = entity.attributeValue("id");
					
					if (id == null) {
						throw new Exception(path + File.separator + templateFile.getName() + " malformed: id is mandatory.");
					}
					else {
						EntityTemplate entityTemplate = new EntityTemplate();
						entityTemplate.setId(id);
						
						Iterator<Element> componentsIt = entity.elementIterator();
						while (componentsIt.hasNext()) {
							Element componentConfig = componentsIt.next();
							
							//generate class name from element name if component doesn't have a class attribute
							String clazz = componentConfig.attributeValue("class", "");
							if (clazz.equals("")) {
								clazz = DungeonMaster.DEFAULT_COMPONENTS_NAMESPACE + "." + WordUtils.capitalize(componentConfig.getName()) + "Component";
							}
							
							entityTemplate.addComponent(clazz, componentConfig);
						}
						
						this.entitiesTemplates().put(id, entityTemplate);
					}
				}
			}
		}
	}
	
	public Master newMaster(Random generator, Map<String, Object> executorScope) {
		return new Master(this, generator, executorScope);
	}
	
	public Map<String, EntityTemplate> entitiesTemplates() {
		return entitiesTemplates;
	}
	
	public void dispatch(Event event) {
		Iterator<EventsDispatcher> dispatchersIt = this.dispatchers.iterator();
		while (dispatchersIt.hasNext()) {
			dispatchersIt.next().publish(event);
		}
	}

	public void registerDispatcher(EventsDispatcher dispatcher) {
		this.dispatchers.add(dispatcher);
	}
	
	public void clearDispatchers() {
		this.dispatchers.clear();
	}
}
