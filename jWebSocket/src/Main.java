import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.server.TokenServer;

import cek.ruins.ApplicationMap;
import cek.ruins.jws.listeners.AdminListener;
import cek.ruins.jws.listeners.DungeonsCreationListener;
import cek.ruins.world.civilizations.CivilizationsTemplates;
import cek.ruins.world.civilizations.CivilizationsHerald;
import cek.ruins.world.environment.Biomes;
import cek.ruins.world.environment.Resources;
import cek.ruins.world.locations.dungeons.DungeonsArchitect;
import cek.ruins.world.locations.dungeons.materials.Materials;
import cek.ruins.world.locations.settlements.SettlementsArchitect;
import cek.ruins.bookofnames.BookOfNames;
import cek.ruins.configuration.Configuration;


public class Main {
	public static void main(String[] args) throws Exception {
		boolean done = false;

		BasicConfigurator.configure(); //TODO leggere conf da file

		Logger logger = Logger.getLogger(Configuration.LOGGER_NAME);
		logger.info("Starting...");
		
		//load regional resources data
		logger.info("Reading " + Configuration.DATA_FOLDER_LOCATION + "/world/resources.xml");
		Resources resources = new Resources();
		resources.loadData(Configuration.DATA_FOLDER_LOCATION + "/world/resources.xml");
		ApplicationMap.set("resources", resources);

		//load regional resources data
		logger.info("Reading " + Configuration.DATA_FOLDER_LOCATION + "/world/biomes.xml");
		Biomes biomes = new Biomes();
		biomes.loadData(Configuration.DATA_FOLDER_LOCATION + "/world/biomes.xml");
		ApplicationMap.set("biomes", biomes);

		//load civilizations templates
		logger.info("Reading " + Configuration.DATA_FOLDER_LOCATION + "/civilizations/civilizations.xml");
		CivilizationsTemplates civilizationsTemplates = new CivilizationsTemplates();
		civilizationsTemplates.loadData(Configuration.DATA_FOLDER_LOCATION + "/civilizations/civilizations.xml");
		ApplicationMap.set("civilizationsTemplates", civilizationsTemplates);

		//load heraldry coats
		logger.info("Reading " + Configuration.DATA_FOLDER_LOCATION + "/civilizations/heraldry.xml");
		CivilizationsHerald herald = new CivilizationsHerald();
		herald.loadData(Configuration.DATA_FOLDER_LOCATION + "/civilizations/heraldry.xml");
		ApplicationMap.set("herald", herald);
		
		//load grammars
		logger.info("Reading " + Configuration.DATA_FOLDER_LOCATION + "/grammars/");
		BookOfNames bookOfNames = new BookOfNames();
		bookOfNames.loadData(Configuration.DATA_FOLDER_LOCATION + "/grammars/");
		ApplicationMap.set("bookOfNames", bookOfNames);
		
		//load settlements architect
		logger.info("Reading " + Configuration.DATA_FOLDER_LOCATION + "/settlements/districts.xml");
		SettlementsArchitect settlementsArchitect = new SettlementsArchitect();
		settlementsArchitect.loadData(Configuration.DATA_FOLDER_LOCATION + "/settlements/districts.xml");
		ApplicationMap.set("settlementsArchitect", settlementsArchitect);
		
		//load dungeons architect
		logger.info("Reading " + Configuration.DATA_FOLDER_LOCATION + "/dungeons/");
		DungeonsArchitect dungeonsArchitect = new DungeonsArchitect();
		dungeonsArchitect.loadData(Configuration.DATA_FOLDER_LOCATION + "/dungeons");
		ApplicationMap.set("dungeonsArchitect", dungeonsArchitect);
		
		//load dungeon's materials
		logger.info("Reading " + Configuration.DATA_FOLDER_LOCATION + "/dungeons/materials");
		Materials materials = new Materials();
		materials.loadData(Configuration.DATA_FOLDER_LOCATION + "/dungeons/materials");
		ApplicationMap.set("materials", materials);
		
		Server fileServer = null;
		try {
			Resource fileServerConfFile = Resource.newResource(Configuration.FILESERVER_CONF_FILE);
			XmlConfiguration fileServerConfiguration = new XmlConfiguration(fileServerConfFile.getInputStream());
			fileServer = (Server) fileServerConfiguration.configure();
		} catch (Exception e) {
			logger.error("File server configuration not found: " + Configuration.FILESERVER_CONF_FILE);
			throw e;
		}

		fileServer.start();

		JWebSocketConfig.initForConsoleApp(args);
		JWebSocketFactory.start();

		// get the token server
		TokenServer tokenServer = (TokenServer)JWebSocketFactory.getServer(Configuration.TOKEN_SERVER_ID);
		if( tokenServer != null ) {
		  // and add listeners to the server's listener chain
		  tokenServer.addListener(new AdminListener());
		  tokenServer.addListener(new DungeonsCreationListener());
		}
		else {
			logger.fatal("Token server \"" + Configuration.TOKEN_SERVER_ID + "\" not found, aborting...");
			JWebSocketFactory.stop();
			fileServer.stop();
			return;
		}

		//try {
			while (!done) {
				Thread.sleep(300);
			}
		//}
		/*
		catch (Exception e) {
			//TODO
			throw e;
		}
		*/
		//finally {
			JWebSocketFactory.stop();
			fileServer.stop();

			logger.info("Terminated...");
		//}
	}
}
