package cek.ruins.jws.listeners;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.kit.WebSocketSession;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.token.Token;

import com.mongodb.BasicDBList;

import cek.ruins.ApplicationMap;
import cek.ruins.ScriptExecutor;
import cek.ruins.configuration.Configuration;
import cek.ruins.utils.DungeonPainter;
import cek.ruins.world.locations.dungeons.Digger;
import cek.ruins.world.locations.dungeons.Dungeon;
import cek.ruins.world.locations.dungeons.DungeonsArchitect;
import cek.ruins.world.locations.dungeons.Master;
import cek.ruins.world.locations.dungeons.entities.Entity;
import cek.ruins.world.locations.dungeons.materials.Material;
import cek.ruins.world.locations.dungeons.materials.Materials;
import cek.ruins.world.locations.dungeons.templates.DungeonTemplate;

public class DungeonsCreationListener extends GenericListener {
	private static String NS = DungeonsCreationListener.class.toString().substring(6);
	private Logger logger;
	
	public DungeonsCreationListener() {
		this.logger = Logger.getLogger(DungeonsCreationListener.NS);
	}
	
	@Override
	public void processClosed(WebSocketServerEvent aEvent) {
		logger.debug(DungeonsCreationListener.NS + " processClosed");
	}

	@Override
	public void processOpened(WebSocketServerEvent aEvent) {
		logger.debug(DungeonsCreationListener.NS + " processOpened");
	}

	@Override
	public void processPacket(WebSocketServerEvent aEvent, WebSocketPacket aPacket) {
		logger.debug(DungeonsCreationListener.NS + " processPacket");
	}

	@Override
	public void processToken(WebSocketServerTokenEvent aEvent, Token aToken) {
		String tokenType = aToken.getType();
		String tokenNamespace = aToken.getNS();

		WebSocketSession session = aEvent.getSession();
		java.util.Map<String, Object> sstorage = session.getStorage();
		
		if (tokenType != null && DungeonsCreationListener.NS.equals(tokenNamespace)) {
			logger.debug("client '" + aEvent.getSessionId() + "' sent Token: '" + aToken.toString() + "'.");
			
			if (TokenType.CREATE_NEW_DUNGEON_TKN.equals(tokenType)) {
				Token response = aEvent.createResponse(aToken);

				try {
					//retrive and test user's inputs
					int seed = 0;
					seed = Integer.parseInt(aToken.getString("seed", "0"));
					
					//if seed is equal to zero, then we generate a randomic seed
					Random generator = null;
					if (seed != 0)
						generator = new Random(seed);
					else
						generator = new Random(new Date().getTime());
					
					DungeonsArchitect dungeonsArchitect = (DungeonsArchitect) ApplicationMap.get("dungeonsArchitect");
					Materials materials = (Materials) ApplicationMap.get("materials");
					
					Digger digger = dungeonsArchitect.newDigger(generator);
					sstorage.put("digger", digger);
					Master master = dungeonsArchitect.newMaster(generator);
					sstorage.put("master", master);
					
					DungeonTemplate dungeonTemplate = dungeonsArchitect.dungeonsTemplates().get("generic"); //TEMP
					Dungeon dungeon = createNewDungeon(dungeonTemplate, digger, master, materials, generator);
					sstorage.put("dungeon", dungeon);
					
					response.setString("id", dungeon.id().toString());
					response.setInteger("depth", dungeon.depth());
					response.setInteger("size", dungeon.size());
					aEvent.sendToken(response);
					
				} catch (Exception e) {
					logger.error(e.getMessage(), e);

					Token error = createGenericErrorResponse(aEvent, aToken, e.getMessage());
					aEvent.sendToken(error);
				}
			}
			else if (TokenType.GET_ENTITIES_TKN.equals(tokenType)) {
				Token response = aEvent.createResponse(aToken);
				
				try {
					int depth = aToken.getInteger("depth");
					Digger digger = (Digger) sstorage.get("digger");
					
					BasicDBList jsonList = new BasicDBList();
					List<Entity> entities = digger.entities(depth);
					for (Entity entity : entities) {
						jsonList.add(entity.statusToJSON());
					}
					response.setList("entities", jsonList);
					
					aEvent.sendToken(response);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);

					Token error = createGenericErrorResponse(aEvent, aToken, e.getMessage());
					aEvent.sendToken(error);
				}
			}
		}
	}

	protected Dungeon createNewDungeon(DungeonTemplate dungeonTemplate, Digger digger, Master master, Materials materials, Random generator) throws Exception {
		UUID id = UUID.randomUUID();
		
		Dungeon dungeon = null;
		if (dungeonTemplate != null) {
			dungeon = init(id, dungeonTemplate, digger, master, materials);
			build(dungeonTemplate, dungeon, digger, master, materials);
		}
		
		//create a new entry in dungeons folder
		File dungeonsDirectory = new File(Configuration.DUNGEONS_FOLDER_LOCATION + File.separator + id);
		boolean dirCreated = dungeonsDirectory.mkdir();

		if (dirCreated) {
			DungeonPainter painter = new DungeonPainter();
			
			for (int n = 0; n < dungeon.depth(); n++) {
				BufferedImage image = painter.createDungeonImage(dungeon, n);
				File outputfile = new File(dungeonsDirectory, String.format("%03d", n) + ".png");
				ImageIO.write(image, "png", outputfile);
			}
		}
		else {
			throw new IOException("Cannot create directory: " + dungeonsDirectory.getAbsolutePath());
		}
		
		return dungeon;
	}
	
	public Dungeon init(UUID dungeonId, DungeonTemplate template, Digger digger, Master master, Materials materials) throws IOException {
		ScriptExecutor executor = ScriptExecutor.executor();
		
		//init global objects map for scripts
		Map<String, Object> scriptsGlobalObjects = new HashMap<String, Object>();
		scriptsGlobalObjects.put("DIGGER", digger);
		//scriptsGlobalObjects.put("_master_", master);
		
		//create new dungeon and level 0
		Dungeon dungeon = new Dungeon(dungeonId, template.size(), template.cells(), template.depth());
		//scriptsGlobalObjects.put("_dungeon_", dungeon);
		
		//add materials
		for (Map.Entry<String, Material> materialEntry : materials.templates().entrySet()) {
			scriptsGlobalObjects.put("MAT_" + materialEntry.getKey(), materialEntry.getValue());
		}
		
		//init subsystems
		digger.setDungeon(dungeon);
		digger.setExecutorScope(scriptsGlobalObjects);
		master.setDungeon(dungeon);
		master.setExecutorScope(scriptsGlobalObjects);
		
		for (int d = 0; d < dungeon.depth(); d++) {
			digger.setPosition(0, 0, d);
			executor.executeScript(template.initScript(), scriptsGlobalObjects);
		}
		
		return dungeon;
	}
	
	protected void build(DungeonTemplate dungeonTemplate, Dungeon dungeon, Digger digger, Master master, Materials materials) {
		//init global objects map for scripts
		Map<String, Object> scriptsGlobalObjects = new HashMap<String, Object>();
		scriptsGlobalObjects.put("DIGGER", digger);
		
		//scriptsGlobalObjects.put("_master_", master);
		//scriptsGlobalObjects.put("_dungeon_", dungeon);
		
		//add materials
		for (Map.Entry<String, Material> materialEntry : materials.templates().entrySet()) {
			scriptsGlobalObjects.put("MAT_" + materialEntry.getKey(), materialEntry.getValue());
		}
		
		//init digger
		digger.setPosition(0, 0, 0);
		digger.setDungeon(dungeon);
		digger.setExecutorScope(scriptsGlobalObjects);
		//master.setDungeon(dungeon);
		//master.setExecutorScope(scriptsGlobalObjects);
		
		//dig dungeon
		ScriptExecutor executor = ScriptExecutor.executor();
		executor.executeScript(dungeonTemplate.buildScript(), scriptsGlobalObjects);
	}
}
