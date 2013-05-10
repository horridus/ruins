package cek.ruins.world.locations.dungeons;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.dom4j.Element;
import org.mozilla.javascript.Script;

import cek.ruins.ScriptExecutor;
import cek.ruins.XmlDocument;
import cek.ruins.world.locations.dungeons.templates.BuildEventTemplate;
import cek.ruins.world.locations.dungeons.templates.DungeonTemplate;
import cek.ruins.world.locations.dungeons.templates.RoomTemplate;

public class DungeonsArchitect {
	private static String DEFAULT_DEPTH_NUM = "5";
	private static String DEFAULT_DUNGEON_SIZE = "512";
	private static String DEFAULT_CELLS_NUM = "32";
	
	private Map<String, RoomTemplate> roomsTemplates;
	private Map<String, BuildEventTemplate> eventsTemplates;
	private Map<String, DungeonTemplate> dungeonsTemplates;
	
	public DungeonsArchitect() {}
	
	@SuppressWarnings("unchecked")
	public void loadData(String path) throws Exception {
		this.roomsTemplates = new HashMap<String, RoomTemplate>();
		this.eventsTemplates = new HashMap<String, BuildEventTemplate>();
		this.dungeonsTemplates = new HashMap<String, DungeonTemplate>();
		
		//load rooms templates
		File roomsTemplatesDirectory = new File(path + File.separator + "rooms");
		File[] templateFiles = roomsTemplatesDirectory.listFiles(new FilenameFilter() {
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
					
					if (id == null || roomGenerator == null) {
						throw new Exception(path + File.separator + templateFile.getName() + " malformed: generator and id is mandatory.");
					}
					else {
						RoomTemplate roomTemplate = new RoomTemplate();
						Script roomGeneratorScript = ScriptExecutor.executor().compileScript(roomGenerator.getText(), "DungeonsArchitect");
						
						roomTemplate.setId(id);
						roomTemplate.setRoomGenerator(roomGeneratorScript);
						
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
						Script buildScript = ScriptExecutor.executor().compileScript(script.getText(), "DungeonsArchitect");
						
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
					
					String size = dungeon.attributeValue("size", DungeonsArchitect.DEFAULT_DUNGEON_SIZE);
					String cells = dungeon.attributeValue("cells", DungeonsArchitect.DEFAULT_CELLS_NUM);
					String depth = dungeon.attributeValue("depth", DungeonsArchitect.DEFAULT_DEPTH_NUM);
					
					//read and compile template's scripts
					Element init = (Element) dungeon.selectSingleNode("./init");
					Script dungeonInitScript = ScriptExecutor.executor().compileScript(init.getText(), "DungeonsArchitect");
					
					Element build = (Element) dungeon.selectSingleNode("./build");
					Script dungeonBuildScript = ScriptExecutor.executor().compileScript(build.getText(), "DungeonsArchitect");
					
					dungeonTemplate.setId(id);
					dungeonTemplate.setSize(Integer.parseInt(size));
					dungeonTemplate.setCells(Integer.parseInt(cells));
					dungeonTemplate.setDepth(Integer.parseInt(depth));
					dungeonTemplate.setInitScript(dungeonInitScript);
					dungeonTemplate.setBuildScript(dungeonBuildScript);
					
					this.dungeonsTemplates.put(id, dungeonTemplate);
				}
			}
		}
	}
	
	public Digger newDigger(Random generator) {
		return new Digger(this, generator);
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
