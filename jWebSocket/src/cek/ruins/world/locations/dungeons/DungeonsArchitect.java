package cek.ruins.world.locations.dungeons;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.dom4j.Element;
import org.mozilla.javascript.Script;

import cek.ruins.Point;
import cek.ruins.ScriptExecutor;
import cek.ruins.XmlDocument;
import cek.ruins.world.locations.dungeons.entities.EntityTemplate;
import cek.ruins.world.locations.dungeons.templates.BuildEventTemplate;
import cek.ruins.world.locations.dungeons.templates.DungeonTemplate;
import cek.ruins.world.locations.dungeons.templates.RoomTemplate;

public class DungeonsArchitect {
	private static String DEFAULT_COMPONENTS_NAMESPACE = "cek.ruins.world.locations.dungeons.entities.components";
	
	private static String DEFAULT_DEPTH_NUM = "5";
	private static String DEFAULT_CELLS_NUM = "16";
	private static int DEFAULT_CELL_SIZE = 16;
	
	private Map<String, RoomTemplate> roomsTemplates;
	private Map<String, BuildEventTemplate> eventsTemplates;
	private Map<String, DungeonTemplate> dungeonsTemplates;
	private Map<String, EntityTemplate> entitiesTemplates;
	
	public DungeonsArchitect() {}
	
	@SuppressWarnings("unchecked")
	public void loadData(String path) throws Exception {
		this.roomsTemplates = new HashMap<String, RoomTemplate>();
		this.eventsTemplates = new HashMap<String, BuildEventTemplate>();
		this.dungeonsTemplates = new HashMap<String, DungeonTemplate>();
		this.entitiesTemplates = new HashMap<String, EntityTemplate>();
		
		//load entities templates
		File enititesTemplatesDirectory = new File(path + File.separator + "entities");
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
								clazz = DungeonsArchitect.DEFAULT_COMPONENTS_NAMESPACE + "." + WordUtils.capitalize(componentConfig.getName()) + "Component";
							}
							
							entityTemplate.addComponent(clazz, componentConfig);
						}
						
						this.entitiesTemplates.put(id, entityTemplate);
					}
				}
			}
		}
		
		//load rooms templates
		File roomsTemplatesDirectory = new File(path + File.separator + "rooms");
		templateFiles = roomsTemplatesDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		for (File templateFile : templateFiles) {
			if (templateFile.exists() && templateFile.isFile()) {
				XmlDocument roomsTemplate = new XmlDocument(FileUtils.readFileToString(templateFile, "UTF-8"));
				
				Iterator<Element> roomsIt = (Iterator<Element>) roomsTemplate.selectNodes("/rooms/room").iterator();
				
				if (!roomsIt.hasNext()) {
					throw new Exception(path + File.separator + templateFile.getName() + " malformed: no element <room> found.");
				}
				
				while (roomsIt.hasNext()) {
					Element room = roomsIt.next();
					String id = room.attributeValue("id");
					Element roomGenerator = (Element) room.selectSingleNode("./generator");
					Element roomMap = (Element) room.selectSingleNode("./map");
					
					if (id == null || (roomGenerator == null && roomMap == null)) {
						throw new Exception(path + File.separator + templateFile.getName() + " malformed: id and generator or map are mandatory.");
					}
					else {
						RoomTemplate roomTemplate = new RoomTemplate();
						roomTemplate.setId(id);
						
						if (roomGenerator != null) {
							Script roomGeneratorScript = ScriptExecutor.executor().compileScript(roomGenerator.getText(), id);
							roomTemplate.setRoomGenerator(roomGeneratorScript);
						}
						
						if (roomMap != null) {
							Map<Character, String> roomMaterialsMap = new HashMap<Character, String>();
							String materialsAttr = roomMap.attributeValue("materials", "");
							String[] materials = materialsAttr.split(",");
							
							for (String material : materials) {
								String[] keyValue = material.split(":");
								if (keyValue.length == 2) {
									roomMaterialsMap.put(keyValue[0].charAt(0), keyValue[1]);
								}
							}
							
							List<Character> map = new ArrayList<Character>();
							String mapLayout = roomMap.getText().trim();
							for (int i = 0; i < mapLayout.length(); i++) {
								Character tile = mapLayout.charAt(i);
								
								if (Character.isJavaIdentifierPart(tile)) {
									map.add(tile);
								}
							}
							
							List<Point> exitsList = new ArrayList<Point>();
							String exitsAttr = roomMap.attributeValue("exits", "");
							String[] exits = exitsAttr.split(",");
							
							for (String exit : exits) {
								String[] coords = exit.split(":");
								if (coords.length == 2) {
									exitsList.add(new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
								}
							}
							
							roomTemplate.setMap(map, roomMaterialsMap, exitsList);
						}
						
						this.roomsTemplates.put(id, roomTemplate);
					}
				}
			}
		}
		
		File buildEventsTemplatesDirectory = new File(path + File.separator + "buildEvents");
		templateFiles = buildEventsTemplatesDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		for (File templateFile : templateFiles) {
			if (templateFile.exists() && templateFile.isFile()) {
				XmlDocument buildEventsTemplate = new XmlDocument(FileUtils.readFileToString(templateFile, "UTF-8"));
				
				Iterator<Element> buildEventsIt = (Iterator<Element>) buildEventsTemplate.selectNodes("/events/event").iterator();
				
				if (!buildEventsIt.hasNext()) {
					throw new Exception(path + File.separator + templateFile.getName() + " malformed: no element <event> found.");
				}
				
				while (buildEventsIt.hasNext()) {
					Element event = buildEventsIt.next();
					
					String id = event.attributeValue("id");
					Element script = (Element) event.selectSingleNode("./script");
					
					if (id == null) {
						throw new Exception(path + File.separator + templateFile.getName() + " malformed: id is mandatory.");
					}
					else {
						BuildEventTemplate eventTemplate = new BuildEventTemplate();
						Script buildScript = ScriptExecutor.executor().compileScript(script.getText(), id);
						
						eventTemplate.setId(id);
						eventTemplate.setBuildScript(buildScript);
						
						this.eventsTemplates.put(id, eventTemplate);
					}
				}
			}
		}
		
		//load dungeons templates
		File templateFile = new File(path + File.separator + "dungeons.xml");

		if (templateFile.exists() && templateFile.isFile()) {
			XmlDocument dungeonsTemplate = new XmlDocument(FileUtils.readFileToString(templateFile, "UTF-8"));
			
			Iterator<Element> dungeonsIt = (Iterator<Element>) dungeonsTemplate.selectNodes("/dungeons/dungeon").iterator();
			while (dungeonsIt.hasNext()) {
				Element dungeon = dungeonsIt.next();
				String id = dungeon.attributeValue("id");
				
				if (id == null) {
					throw new Exception(path + "/dungeons.xml malformed: id is mandatory.");
				}
				else {
					DungeonTemplate dungeonTemplate = new DungeonTemplate();
					
					int cells = Integer.parseInt(dungeon.attributeValue("cells", DungeonsArchitect.DEFAULT_CELLS_NUM));
					int depth = Integer.parseInt(dungeon.attributeValue("depth", DungeonsArchitect.DEFAULT_DEPTH_NUM));
					int size = cells * DungeonsArchitect.DEFAULT_CELL_SIZE;
					
					//read and compile template's scripts
					Element init = (Element) dungeon.selectSingleNode("./init");
					Script dungeonInitScript = ScriptExecutor.executor().compileScript(init.getText(), id + ":init");
					
					Element build = (Element) dungeon.selectSingleNode("./build");
					Script dungeonBuildScript = ScriptExecutor.executor().compileScript(build.getText(), id + ":build");
					
					dungeonTemplate.setId(id);
					dungeonTemplate.setSize(size);
					dungeonTemplate.setCells(cells);
					dungeonTemplate.setDepth(depth);
					dungeonTemplate.setInitScript(dungeonInitScript);
					dungeonTemplate.setBuildScript(dungeonBuildScript);
					
					this.dungeonsTemplates.put(id, dungeonTemplate);
				}
			}
		}
	}
	
	public Digger newDigger(Random generator) throws Exception {
		Object[] args = {this, generator};
		Digger digger = (Digger) ScriptExecutor.executor().registerAndCreateObject(Digger.class, args, true);
		return digger;
	}
	
	public Master newMaster(Random generator) {
		return new Master(this, generator);
	}
	
	public Map<String, EntityTemplate> entitiesTemplates() {
		return entitiesTemplates;
	}

	public Map<String, RoomTemplate> roomsTemplates() {
		return this.roomsTemplates;
	}
	
	public Map<String, BuildEventTemplate> buildEventsTemplates() {
		return this.eventsTemplates;
	}
	
	public Map<String, DungeonTemplate> dungeonsTemplates() {
		return this.dungeonsTemplates;
	}
}
