package cek.ruins.jws.listeners;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import cek.ruins.bookofnames.BookOfNames;
import cek.ruins.configuration.Configuration;
import cek.ruins.events.Event;
import cek.ruins.events.EventName;
import cek.ruins.events.EventsDispatcher;
import cek.ruins.map.Map;
import cek.ruins.map.PoliticalRegion;
import cek.ruins.map.Region;
import cek.ruins.map.voronoi.VoronoiSite;
import cek.ruins.utils.MapPainter;
import cek.ruins.world.civilizations.Civilization;
import cek.ruins.world.civilizations.CivilizationTemplate;
import cek.ruins.world.civilizations.Civilizations;
import cek.ruins.world.civilizations.CivilizationsTemplates;
import cek.ruins.world.civilizations.CivilizationsHerald;
import cek.ruins.world.environment.Biomes;
import cek.ruins.world.environment.Climates;
import cek.ruins.world.environment.Resources;
import cek.ruins.world.history.HistoriansDirector;
import cek.ruins.world.history.WorldBuildingHistorian;
import cek.ruins.world.locations.Locations;
import cek.ruins.world.locations.LocationsBuilder;
import cek.ruins.world.locations.Settlement;
import cek.ruins.world.locations.settlements.Architect;
import cek.ruins.world.locations.settlements.SettlementsArchitect;

public class AdminListener extends GenericListener {
	private static String NS = AdminListener.class.toString().substring(6);

	//error codes
	private static Integer SETTLEMENT_INVALID_LOCATION_ERR = 2;

	private Logger logger;

	public AdminListener() {
		this.logger = Logger.getLogger(AdminListener.NS);
	}

	@Override
	public void processOpened(WebSocketServerEvent aEvent) {
		logger.debug(AdminListener.NS + " processOpened");
	}

	@Override
	public void processPacket(WebSocketServerEvent aEvent, WebSocketPacket aPacket) {
		logger.info(AdminListener.NS + " processPacket");
	}

	@Override
	public void processClosed(WebSocketServerEvent aEvent) {
		logger.info(AdminListener.NS + " processClosed");
		
		//FIXME trovare un modo per pulirla quando finito.
		WebSocketSession session = aEvent.getSession();
		java.util.Map<String, Object> sstorage = session.getStorage();
		sstorage.put(AdminListener.NS + "__session_initialized__", false);
	}

	@Override
	public void processToken(WebSocketServerTokenEvent aEvent, Token aToken) {
		String tokenType = aToken.getType();
		String tokenNamespace = aToken.getNS();

		WebSocketSession session = aEvent.getSession();
		java.util.Map<String, Object> sstorage = session.getStorage();
		
		if (tokenType != null && AdminListener.NS.equals(tokenNamespace)) {
			logger.debug("client '" + aEvent.getSessionId() + "' sent Token: '" + aToken.toString() + "'.");
			
			//FIXME con più listener bisognerà inizializzare la sessione da un'altra parte
			//init session if needed 
			if (sstorage.get(AdminListener.NS + "__session_initialized__") == null) {
				//create EventDispatcher for this user and subscribe to it
				EventsDispatcher dispatcher = new EventsDispatcher();
				dispatcher.registerSubscriber(aEvent.getConnector());
				sstorage.put("dispatcher", dispatcher);
				
				sstorage.put(AdminListener.NS + "__session_initialized__", true);
			}

			if (TokenType.CREATE_NEW_WORLD_TKN.equals(tokenType)) {
				try {
					//retrive regional resources data
					Resources resources = (Resources) ApplicationMap.get("resources");

					Token response = aEvent.createResponse(aToken);

					//retrive and test user's inputs
					int seed = 0;
					try {
						seed = Integer.parseInt(aToken.getString("seed", "0"));
					}
					catch (NumberFormatException nfe) {
						//FIXME do nothing?
					}

					String climateStr = aToken.getString("climate", "temperate");
					Climates climate = Climates.temperate;
					if (climateStr.equals(Climates.continental.toString()))
						climate = Climates.continental;
					else if (climateStr.equals(Climates.dry.toString()))
						climate = Climates.dry;
					else if (climateStr.equals(Climates.polar.toString()))
						climate = Climates.polar;
					else if (climateStr.equals(Climates.tropical.toString()))
						climate = Climates.tropical;

					double baseTemperature = 10;
					double thermicalExcursionA = -5;
					double thermicalExcursionB = 30;

					String temperature = aToken.getString("temperature", "mild");
					if (temperature.equals("freezing")) {
						baseTemperature = 0;
						thermicalExcursionA = -20;
						thermicalExcursionB = 10;
					}
					else if (temperature.equals("cold")) {
						baseTemperature = 10;
						thermicalExcursionA = -15;
						thermicalExcursionB = 25;
					}
					else if (temperature.equals("mild")) {
						baseTemperature = 10;
						thermicalExcursionA = -5;
						thermicalExcursionB = 30;
					}
					else if (temperature.equals("hot")) {
						baseTemperature = 20;
						thermicalExcursionA = -5;
						thermicalExcursionB = 30;
					}
					else if (temperature.equals("scorching")) {
						baseTemperature = 30;
						thermicalExcursionA = -10;
						thermicalExcursionB = 20;
					}

					int numSites = 4000;
					try {
						numSites = Integer.parseInt(aToken.getString("sites", "4000"));
					}
					catch (NumberFormatException nfe) {
						//FIXME do nothing?
					}

					//if seed is equal to zero, then we generate a randomic seed
					Random generator = null;
					if (seed != 0)
						generator = new Random(seed);
					else
						generator = new Random(new Date().getTime());

					Map map = createNewWorld(generator, 1000, numSites, climate, baseTemperature, thermicalExcursionA, thermicalExcursionB, resources);
					sstorage.put("generator", generator);

					//save into session for future use
					sstorage.put("map", map);

					//save into session objects needed for next steps
					sstorage.put("locations", new Locations());
					SettlementsArchitect settlementsArchitect = (SettlementsArchitect) ApplicationMap.get("settlementsArchitect");
					sstorage.put("architect", settlementsArchitect.newArchitectInstance());

					response.setString("data", map.id().toString());

					aEvent.sendToken(response);
				}
				catch (Exception e) {
					logger.error(e.getMessage(), e);

					Token error = createGenericErrorResponse(aEvent, aToken, e.getMessage());
					aEvent.sendToken(error);
				}
			}
			else if (TokenType.REGIONS_INFOS_TKN.equals(tokenType)) {
				try {
					Token response = aEvent.createResponse(aToken);
					Map map = (Map) sstorage.get("map");

					if (map != null) {
						java.util.Map<Integer, java.util.Map<String, Object>> regionsInfos = new HashMap<Integer, java.util.Map<String,Object>>();

						for (int i = 0; i < map.regions().size(); i++) {
							java.util.Map<String, Object> info = createRegionInfoMap(map, i);
							regionsInfos.put(i, info);
						}

						response.setMap("regions", regionsInfos);
						aEvent.sendToken(response);
					}
					else {
						Token error = createGenericErrorResponse(aEvent, aToken, "TODO");
						aEvent.sendToken(error);
					}
				}
				catch (Exception e) {
					logger.error(e.getMessage(), e);

					Token error = createGenericErrorResponse(aEvent, aToken, e.getMessage());
					aEvent.sendToken(error);
				}
			}
			else if (TokenType.REGION_INFO_TKN.equals(tokenType)) {
				try {
					Token response = aEvent.createResponse(aToken);

					Map map = (Map) sstorage.get("map");
					Integer rIndex = aToken.getInteger("rindex");
					if (map != null && rIndex != null) {
						response = fillRegionInfoToken(response, map, rIndex);

						aEvent.sendToken(response);
					}
					else {
						Token error = createGenericErrorResponse(aEvent, aToken, "TODO");
						aEvent.sendToken(error);
					}
				}
				catch (Exception e) {
					logger.error(e.getMessage(), e);

					Token error = createGenericErrorResponse(aEvent, aToken, e.getMessage());
					aEvent.sendToken(error);
				}
			}
			else if (TokenType.CIVILIZATIONS_TEMPLATE_TKN.equals(tokenType)) {
				try {
					Token response = aEvent.createResponse(aToken);
					CivilizationsTemplates civilizationsTemplates = (CivilizationsTemplates) ApplicationMap.get("civilizationsTemplates");

					//create a map to accomodate civilizations created during world building process
					CivilizationsHerald herald = (CivilizationsHerald) ApplicationMap.get("herald");
					Civilizations civilizations = civilizationsTemplates.civilizationsNewInstance(herald.newCoatsInstance());
					sstorage.put("existingCivilizations", civilizations);

					BasicDBList jsonList = new BasicDBList();
					Iterator<CivilizationTemplate> civTIt = civilizationsTemplates.templates().iterator();
					while (civTIt.hasNext()) {
						CivilizationTemplate template = civTIt.next();
						jsonList.add(template.toJSON());
					}

					response.setString("civilizations", jsonList.toString());

					aEvent.sendToken(response);
				}
				catch (Exception e) {
					logger.error(e.getMessage(), e);

					Token error = createGenericErrorResponse(aEvent, aToken, e.getMessage());
					aEvent.sendToken(error);
				}
			}
			else if (TokenType.ADD_CIVILIZATION_TKN.equals(tokenType)) {
				try {
					Token response = aEvent.createResponse(aToken);

					Civilizations civilizations = (Civilizations) sstorage.get("existingCivilizations");
					Integer civIndex = aToken.getInteger("civindex");
					Integer rIndex = aToken.getInteger("rindex");
					Integer x = aToken.getInteger("x");
					Integer y = aToken.getInteger("y");

					response.setInteger("rindex", rIndex);

					Map map = (Map) sstorage.get("map");
					Locations locations = (Locations) sstorage.get("locations");
					Random generator = (Random) sstorage.get("generator");
					Architect architect = (Architect) sstorage.get("architect");

					if (map != null && civIndex != null && rIndex != null && locations != null && generator != null && architect != null) {
						//during world creation shouldn't exist more than one settlement for every region
						if (locations.settelementsInRegion(rIndex) != null && locations.settelementsInRegion(rIndex).size() > 0) {
							response.setInteger("code", AdminListener.SETTLEMENT_INVALID_LOCATION_ERR);
							aEvent.sendToken(response);

							return;
						}
						else {
							BookOfNames bookOfNames = (BookOfNames) ApplicationMap.get("bookOfNames");
							
							EventsDispatcher dispatcher = (EventsDispatcher) sstorage.get("dispatcher");

							Civilization civilization = civilizations.createCivilization(civIndex, map, rIndex, bookOfNames, generator);

							response.setString("civilization", civilization.toJSON().toString());
							aEvent.sendToken(response);

							//adding civilization also generate their first settlement
							Settlement newSettlement = addInitialCivilizationSettlement(map, civilization, rIndex, x, y, generator, bookOfNames, architect);
							locations.addSettlement(newSettlement);

							Event event = new Event(AdminListener.NS, EventName.NEW_LOCATION_EVENT);
							event.addData("location", newSettlement);
							event.addData("locationType", "settlement");
							dispatcher.publish(event);

							//set region ownership to the new civilization
							PoliticalRegion politicalRegion = map.politicalRegions().get(rIndex);
							politicalRegion.setOwnerCivilization(civilization.id());

							event = new Event(AdminListener.NS, EventName.UPDATE_REGIONS_EVENT);
							java.util.Map<Integer, java.util.Map<String,Object>> regionsUpdates = new HashMap<Integer, java.util.Map<String,Object>>();
							java.util.Map<String,Object> regionUpdate = new HashMap<String,Object>();
							regionUpdate.put("owner", politicalRegion.ownerCivilization());
							regionsUpdates.put(rIndex, regionUpdate);
							event.addData("political", regionsUpdates);
							dispatcher.publish(event);
						}
					}
					else {
						Token error = createGenericErrorResponse(aEvent, aToken, "TODO");
						aEvent.sendToken(error);
					}
				}
				catch (Exception e) {
					logger.error(e.getMessage(), e);

					Token error = createGenericErrorResponse(aEvent, aToken, e.getMessage());
					aEvent.sendToken(error);
				}
			}
			else if (TokenType.START_HISTORY_TKN.equals(tokenType)) {
				try {
					Token response = aEvent.createResponse(aToken);

					Integer endYear = aToken.getInteger("endYear");

					Map map = (Map) sstorage.get("map");
					Locations locations = (Locations) sstorage.get("locations");
					Civilizations civilizations = (Civilizations) sstorage.get("existingCivilizations");
					Architect architect = (Architect) sstorage.get("architect");
					
					Biomes biomes = (Biomes) ApplicationMap.get("biomes");
					Random generator = (Random) sstorage.get("generator");
					BookOfNames bookOfNames = (BookOfNames) ApplicationMap.get("bookOfNames");
					Resources resources = (Resources) ApplicationMap.get("resources");

					aEvent.sendToken(response);

					if (map != null && locations != null && civilizations != null && architect != null) {
						runHistory(aEvent, aToken, endYear, map, locations, civilizations, architect, biomes, resources, generator, bookOfNames);
					}
					else {
						Token error = createGenericErrorResponse(aEvent, aToken, "TODO");
						aEvent.sendToken(error);
					}
				}
				catch (Exception e) {
					logger.error(e.getMessage(), e);

					Token error = createGenericErrorResponse(aEvent, aToken, e.getMessage());
					aEvent.sendToken(error);
				}
			}
		}
	}

	protected Map createNewWorld(Random generator, int size, int numSites, Climates climate, double baseTemperature, double thermicalExcursionA, double thermicalExcursionB, Resources resources) throws IOException {
		UUID id = UUID.randomUUID();

		//generate a fresh map
		Map worldMap = new Map(id, climate, baseTemperature, thermicalExcursionA, thermicalExcursionB);
		worldMap.generate(size, numSites, generator, resources);

		//create a new entry in worlds folder
		File worldDirectory = new File(Configuration.WORLDS_FOLDER_LOCATION + File.separator + id);
		boolean dirCreated = worldDirectory.mkdir();

		if (dirCreated) {
			MapPainter mapPainter = new MapPainter(worldMap, generator);
			BufferedImage image = mapPainter.createPaperMap();

			File outputfile = new File(worldDirectory, "paper.png");
			ImageIO.write(image, "png", outputfile);

			image = mapPainter.createElevationMap();

			outputfile = new File(worldDirectory, "elevation.png");
			ImageIO.write(image, "png", outputfile);

			image = mapPainter.createMoistureMap();

			outputfile = new File(worldDirectory, "moisture.png");
			ImageIO.write(image, "png", outputfile);

			image = mapPainter.createTemperatureMap();

			outputfile = new File(worldDirectory, "temperature.png");
			ImageIO.write(image, "png", outputfile);

			image = mapPainter.createBiomesMap();

			outputfile = new File(worldDirectory, "biomes.png");
			ImageIO.write(image, "png", outputfile);

			image = mapPainter.createRegionIndex();

			//create coords to regions map with image data
			int[] imgData = new int[image.getWidth() * image.getHeight()];
			for (int y = 0; y < worldMap.size(); y++) {
				for (int x = 0; x < worldMap.size(); x++) {
					imgData[x + y*worldMap.size()] = ((image.getRGB(x, y) >> 8)  & 0x0000FFFF) - 1;
				}
			}

			worldMap.processCoordsToRegionsMap(imgData);

			outputfile = new File(worldDirectory, "rindex.png");
			ImageIO.write(image, "png", outputfile);

			//now save map to db
			//TODO
		}
		else {
			throw new IOException("Cannot create directory: " + worldDirectory.getAbsolutePath());
		}

		return worldMap;
	}

	protected Settlement addInitialCivilizationSettlement(Map map, Civilization civilization, int regionIdx, Integer x, Integer y, Random generator, BookOfNames bookOfNames, Architect architect) throws Exception {
		//check input validity
		int index = map.getRegionIndexFromCoords(x, y);
		if (index != regionIdx)
			return null;

		//TEMP////////////////////////
		float startingPopulation = 20;
		//////////////////////////////

		LocationsBuilder builder = new LocationsBuilder(map, generator, bookOfNames);
		return builder.buildSettlement(civilization, regionIdx, startingPopulation, x, y, architect);
	}

	protected Token fillRegionInfoToken(Token token, Map map, Integer regionIndex) {
		Region region = map.regions().get(regionIndex);
		VoronoiSite site = map.regionsGraph().sites().get(regionIndex);
		PoliticalRegion politicalRegion = map.politicalRegions().get(regionIndex);

		token.setString("region", region.toJSON().toString());
		token.setString("site", site.toJSON().toString());
		token.setString("political", politicalRegion.toJSON().toString());

		//TODO add more info

		return token;
	}

	protected  java.util.Map<String, Object> createRegionInfoMap(Map map, Integer regionIndex) {
		Region region = map.regions().get(regionIndex);
		VoronoiSite site = map.regionsGraph().sites().get(regionIndex);
		PoliticalRegion politicalRegion = map.politicalRegions().get(regionIndex);

		java.util.Map<String, Object> info = new HashMap<String, Object>();
		info.put("region", region.toJSON().toString());
		info.put("site", site.toJSON().toString());
		info.put("political", politicalRegion.toJSON().toString());

		//TODO add more info

		return info;
	}

	protected void runHistory(WebSocketServerTokenEvent aEvent, Token aInToken, Integer endYear, Map map, Locations locations,
							  Civilizations civilizations,  Architect architect, Biomes biomes, Resources resources,
							  Random generator, BookOfNames bookOfNames) throws Exception {

		HistoriansDirector historiansDirector = (HistoriansDirector) aEvent.getSession().getStorage().get("historiansDirector");

		if (historiansDirector == null) {
			historiansDirector = new HistoriansDirector(map, locations, civilizations, architect, biomes, resources, generator, bookOfNames);
			historiansDirector.setDateNotification(false, true, true, true);

			historiansDirector.setHistoryLoggingFile(Configuration.WORLDS_FOLDER_LOCATION + File.separator + map.id() + File.separator + "history.log");
			historiansDirector.writeHistory("-- Start of known history --");
			historiansDirector.disableHistoryLogging();
			
			aEvent.getSession().getStorage().put("historiansDirector", historiansDirector);

			WorldBuildingHistorian historian = new WorldBuildingHistorian();
			historiansDirector.registerHistorian(historian);
		}

		historiansDirector.clearDispatchers();
		
		EventsDispatcher dispatcher = (EventsDispatcher) aEvent.getSession().getStorage().get("dispatcher");
		historiansDirector.registerDispatcher(dispatcher);

		historiansDirector.setHistoryLoggingFile(Configuration.WORLDS_FOLDER_LOCATION + File.separator + map.id() + File.separator + "history.log");

		for (int year = 1; year <= endYear; year++) {
			for (int day = 0; day < HistoriansDirector.DAYS_IN_A_YEAR; day++) {
				historiansDirector.advanceDay();
			}
		}

		historiansDirector.disableHistoryLogging();
	}
}
