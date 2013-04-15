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

public class DungeonsArchitect {
	private static String DEFAULT_DUNGEON_SIZE = "512";
	private static String DEFAULT_CELLS_NUM = "32";
	
	private Map<String, RoomTemplate> roomsTemplates;
	private Map<String, DungeonTemplate> dungeonsTemplates;
	
	public DungeonsArchitect() {}
	
	@SuppressWarnings("unchecked")
	public void loadData(String path) throws Exception {
		this.roomsTemplates = new HashMap<String, RoomTemplate>();
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
						
						roomTemplate.id = id;
						roomTemplate.roomGenerator = roomGeneratorScript;
						
						this.roomsTemplates.put(id, roomTemplate);
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
					
					//read and compile template's scripts
					Element init = (Element) dungeon.selectSingleNode("./init");
					Script dungeonInitScript = ScriptExecutor.executor().compileScript(init.getText(), "DungeonsArchitect");
					
					Element build = (Element) dungeon.selectSingleNode("./build");
					Script dungeonBuildScript = ScriptExecutor.executor().compileScript(build.getText(), "DungeonsArchitect");
					
					
					dungeonTemplate.id = id;
					dungeonTemplate.size = Integer.parseInt(size);
					dungeonTemplate.cells = Integer.parseInt(cells);
					dungeonTemplate.initScript = dungeonInitScript;
					dungeonTemplate.buildScript = dungeonBuildScript;
					
					this.dungeonsTemplates.put(id, dungeonTemplate);
				}
			}
		}
	}
	
	public Digger newDiggerInstance(Random generator) {
		return new Digger(this, generator);
	}

	public Map<String, RoomTemplate> roomsTemplates() {
		return this.roomsTemplates;
	}
	
	public Map<String, DungeonTemplate> dungeonsTemplates() {
		return this.dungeonsTemplates;
	}
}
