package cek.ruins.jws.listeners;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.kit.WebSocketSession;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.token.Token;

import cek.ruins.ApplicationMap;
import cek.ruins.configuration.Configuration;
import cek.ruins.utils.DungeonPainter;
import cek.ruins.world.locations.dungeons.Digger;
import cek.ruins.world.locations.dungeons.Dungeon;
import cek.ruins.world.locations.dungeons.DungeonsArchitect;
import cek.ruins.world.locations.dungeons.materials.Materials;

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
					Digger digger = dungeonsArchitect.newDiggerInstance(generator);
				
					Materials materials = (Materials) ApplicationMap.get("materials");
					Dungeon dungeon = createNewDungeon(digger, materials);
					
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
		}
	}

	protected Dungeon createNewDungeon(Digger digger, Materials materials) throws Exception {
		UUID id = UUID.randomUUID();
		
		Dungeon dungeon = digger.generate(id, "generic", materials); //TEMP
		
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
}
