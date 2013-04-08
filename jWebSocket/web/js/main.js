var main = {
	jwsClient: {},
	jwsHandler: {},

	day: 1,
	month: 1,
	year: 1,
	season: 0,
};

main.user = {};

$(document).ready(function() {
	main.configuration.init();

	main.initJWS();
	mainui.initGUI();
	main.initLogic();
});

main.initJWS = function() {
    // jws.browserSupportsWebSockets checks if web sockets are available
    // either natively, by the FlashBridge or by the ChromeFrame.
    if( jws.browserSupportsWebSockets() ) {
      main.jwsClient = new jws.jWebSocketJSONClient();
      // Optionally enable GUI controls here
    } else {
      // Optionally disable GUI controls here
      var lMsg = jws.MSG_WS_NOT_SUPPORTED;
      alert( lMsg );
    }
};

main.initLogic = function() {
	main.jwsClient.setSystemCallbacks(main.systemPluginHandler);
};

main.connectionHandler = {
	subProtocol: jws.WS_SUBPROT_JSON,

	// connection timeout in ms
	openTimeout: 30000,

	// OnConnectionTimeout callback
	OnOpenTimeout: function( aEvent ) {
		console.debug("Opening timeout exceeded!");
		alert("Opening timeout exceeded!");
	},

	// OnOpen callback
	//chiamato alla open dell' oggetto websocket
	OnOpen: function( aEvent ) {
		console.debug("jWebSocket connection established.");
	},

	// OnWelcome event
	//inviato system plug-in dopo open, chiamato per token con type == welcome
	OnWelcome: function( aEvent )  {
		console.debug("jWebSocket 'welcome' received.");

		console.info('Welcome to ' + main.configuration.applicationName);
		console.info('vendor: ' + aEvent.vendor);
		console.info('server version: ' + aEvent.version);
		console.debug('session timeout: ' + aEvent.timeout);
		console.debug('client id: ' + aEvent.sourceId);
	},

	// OnGoodBye event
	OnGoodBye: function( aEvent )  {
		console.debug("jWebSocket 'goodbye' received.");
	},

	// OnMessage callback
	//chiamato per ogni messaggio dell' oggetto websocket dopo gli handler dei plug-in
	OnMessage: function( aEvent ) {
		console.debug("jWebSocket message received: '" + aEvent.data + "'");

		var message = window.JSON.parse(aEvent.data);
		//if message is an "event" call right handler
		if (message.type == 'event') {
			switch (message.name) {
			case tokensFactory.NEW_LOCATION_EVENT:
				eventsHandlers.newLocation(message);
				break;
			case tokensFactory.UPDATE_LOCATIONS_EVENT:
				eventsHandlers.updateLocations(message);
				break;
			case tokensFactory.DATE_CHANGE_EVENT:
				eventsHandlers.dateChange(message);
				mainui.updateDate();
				break;
			case tokensFactory.DELETE_LOCATION_EVENT:
				eventsHandlers.deleteLocation(message);
				break;
			case tokensFactory.UPDATE_REGIONS_EVENT:
				eventsHandlers.updateRegions(message);
				break;
			case tokensFactory.NEW_ROAD_EVENT:
				eventsHandlers.newRoad(message);
				break;
			default:
				break;
			}
		}
	},

	// OnReconnecting callback
	OnReconnecting: function( aEvent ) {
		console.debug("Re-establishing jWebSocket connection...");
	},

	// OnClose callback
	//chiamato alla close dell' oggetto websocket
	OnClose: function( aEvent ) {
		console.debug( "jWebSocket connection closed, reason: '" + aEvent.reason + "'" );
	}
};

main.systemPluginHandler = {
	// OnLoggedIn event
	OnLoggedIn: function( aEvent )  {
		console.debug("[system plug-in] 'loggedin' received.");
		if (main.jwsClient.isLoggedIn()) {
			main.user = new User(main.jwsClient.getUsername());

			mainui.createLoggedinArea();
			mainui.createAdminMenu();
		}
	},
	// OnLoginError event
	OnLoginError: function( aEvent )  {
		console.debug("[system plug-in] 'logginerror' received.");
	},
	// OnLoggedOut event
	OnLoggedOut: function( aEvent )  {
		console.debug("[system plug-in] 'loggedout' received.");
	},
	// OnLogoutError event
	OnLogoutError: function( aEvent )  {
		console.debug("[system plug-in] 'logouterror' received.");
	}
}