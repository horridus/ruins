var actions = {};

//actions.TIMEOUT = jws.DEF_RESP_TIMEOUT;
actions.TIMEOUT = 60000;

actions.login = function(event) {
	var userName = $('#user_name').val();
	var userPassword = $('#user_password').val();
	main.jwsHandler = main.jwsClient.logon(main.configuration.wsUrl, userName, userPassword, main.connectionHandler);
};

actions.logout = function(event) {
	if( main.jwsClient ) {
		main.jwsClient.close();

		mainui.clearMenu();
		mainui.clearContent();
		mainui.createLoginArea();
	}
};

actions.createNewWorld = function(seed, climate, temperature, sites) {
	var token = tokensFactory.createNewWorldPkt(seed, climate, temperature, sites);
	main.jwsClient.sendToken(token, {
		timeout: actions.TIMEOUT,

		OnTimeout: function() {
			console.error("[actions.createNewWorld] timed out.");
			alert("[actions.createNewWorld] timed out.");
		},

		OnResponse: function(aEvent) {
		},

		OnSuccess: function(aEvent) {
			console.debug("[actions.createNewWorld] success.");

			if (aEvent.ns == tokensFactory.NS_ADMIN_LISTENER) {
				switch (aEvent.reqType) {
				case tokensFactory.CREATE_NEW_WORLD_TKN:
					mainui.showWorld(aEvent.data);
					mainui.showWorldCreationMenu();

					actions.getCivilizationsTemplates();

					actions.regionsInfos();
					break;
				default:
					break;
				}
			}
		},

		OnFailure: function() {
			console.error("[actions.createNewWorld] failure.");
		}
	});
};

actions.createNewDungeon = function(seed) {
	var token = tokensFactory.createNewDungeonPkt(seed);
	main.jwsClient.sendToken(token, {
		timeout: actions.TIMEOUT,

		OnTimeout: function() {
			console.error("[actions.createNewDungeon] timed out.");
			alert("[actions.createNewDungeon] timed out.");
		},

		OnResponse: function(aEvent) {
		},

		OnSuccess: function(aEvent) {
			console.debug("[actions.createNewDungeon] success.");

			if (aEvent.ns == tokensFactory.NS_DUNGEONS_CREATION_LISTENER) {
				switch (aEvent.reqType) {
				case tokensFactory.CREATE_NEW_DUNGEON_TKN:
					mainui.showDungeon(aEvent);
					for (var i=0; i < main.dungeon.depth; i++) {
						actions.getDungeonEntities(i);
					}
					
					mainui.dungeonRenderer.showLevel(0);
					mainui.animateDungeon();
					
					break;
				default:
					break;
				}
			}
		},

		OnFailure: function() {
			console.error("[actions.createNewDungeon] failure.");
		}
	});
};

actions.getDungeonEntities = function(depth) {
	var token = tokensFactory.dungeonEntitiesPkt(depth);
	main.jwsClient.sendToken(token, {
		timeout: actions.TIMEOUT,

		OnTimeout: function() {
			console.error("[actions.getDungeonEntities] timed out.");
			alert("[actions.getDungeonEntities] timed out.");
		},

		OnResponse: function(aEvent) {
		},

		OnSuccess: function(aEvent) {
			console.debug("[actions.getDungeonEntities] success.");

			if (aEvent.ns == tokensFactory.NS_DUNGEONS_CREATION_LISTENER) {
				switch (aEvent.reqType) {
				case tokensFactory.GET_ENTITIES_TKN:
					$.each(aEvent.entities, function(id, entity) {
						main.dungeon.addEntity(entity);
						mainui.dungeonRenderer.addEntity(entity);
					});
					break;
				default:
					break;
				}
			}
		},

		OnFailure: function() {
			console.error("[actions.createNewWorld] failure.");
		}
	});
};

actions.regionsInfos  = function() {
	var token = tokensFactory.regionsInfosPkt();
	main.jwsClient.sendToken(token, {
		timeout: actions.TIMEOUT,

		OnTimeout: function() {
			console.error("[actions.regionsInfos] timed out.");
			alert("[actions.regionsInfos] timed out.");
		},

		OnResponse: function(aEvent) {
		},

		OnSuccess: function(aEvent) {
			console.debug("[actions.regionsInfos] success.");

			if (aEvent.ns == tokensFactory.NS_ADMIN_LISTENER) {
				switch (aEvent.reqType) {
				case tokensFactory.REGIONS_INFOS_TKN:
					var regions = aEvent.regions;
					for (var id in regions) {
						var info = regions[id];
						var region = window.JSON.parse(info.region);
						var site = window.JSON.parse(info.site);
						var political = window.JSON.parse(info.political);

						main.map.addRegion(region, site, political);
					}
					break;
				default:
					break;
				}
			}
		},

		OnFailure: function() {
			console.error("[actions.regionsInfos] failure.");
		}
	});
};

actions.regionInfo = function(rindex) {
	var token = tokensFactory.regionInfoPkt(rindex);
	main.jwsClient.sendToken(token, {
		timeout: actions.TIMEOUT,

		OnTimeout: function() {
			console.error("[actions.regionInfo] timed out.");
			alert("[actions.regionInfo] timed out.");
		},

		OnResponse: function(aEvent) {
		},

		OnSuccess: function(aEvent) {
			console.debug("[actions.regionInfo] success.");

			if (aEvent.ns == tokensFactory.NS_ADMIN_LISTENER) {
				switch (aEvent.reqType) {
				case tokensFactory.REGION_INFO_TKN:
					var region = window.JSON.parse(aEvent.region);
					var site = window.JSON.parse(aEvent.site);
					var political = window.JSON.parse(aEvent.political);

					main.map.addRegion(region, site, political);
					break;
				default:
					break;
				}
			}
		},

		OnFailure: function() {
			console.error("[actions.regionInfo] failure.");
		}
	});
};

actions.getCivilizationsTemplates = function() {
	var token = tokensFactory.civilizationsTemplatesPkt();
	main.jwsClient.sendToken(token, {
		timeout: actions.TIMEOUT,

		OnTimeout: function() {
			console.error("[actions.getCivilizationsTemplates] timed out.");
			alert("[actions.getCivilizationsTemplates] timed out.");
		},

		OnResponse: function(aEvent) {
		},

		OnSuccess: function(aEvent) {
			console.debug("[actions.getCivilizationsTemplates] success.");

			if (aEvent.ns == tokensFactory.NS_ADMIN_LISTENER) {
				switch (aEvent.reqType) {
				case tokensFactory.CIVILIZATIONS_TEMPLATES_TKN:
					//create global ciivilizations container
					main.civilizations = new RUINS.Civilizations(window.JSON.parse(aEvent.civilizations));

					mainui.showCivilizationsTemplates(main.civilizations.civilizationsTemplates);

					break;
				default:
					break;
				}
			}
		},

		OnFailure: function() {
			console.error("[actions.getCivilizationsTemplates] failure.");
		}
	});
};

actions.addNewCivilization = function(civindex, rindex, x, y) {
	var token = tokensFactory.addNewCivilizationPkt(civindex, rindex, x, y);
	main.jwsClient.sendToken(token, {
		timeout: actions.TIMEOUT,

		OnTimeout: function() {
			console.error("[actions.addNewCivilization] timed out.");
			alert("[actions.addNewCivilization] timed out.");
		},

		OnResponse: function(aEvent) {
		},

		OnSuccess: function(aEvent) {
			console.debug("[actions.addNewCivilization] success.");

			if (aEvent.ns == tokensFactory.NS_ADMIN_LISTENER) {
				switch (aEvent.reqType) {
				case tokensFactory.ADD_CIVILIZATION_TKN:
					var civilization = window.JSON.parse(aEvent.civilization);
					var rindex = window.JSON.parse(aEvent.rindex);

					main.civilizations.addNewCivilization(civilization);

					break;
				default:
					break;
				}
			}
		},

		OnFailure: function() {
			console.error("[actions.addNewCivilization] failure.");
		}
	});
};

actions.startHistory = function(event) {
	var token = tokensFactory.startHistoryPkt(event.data);

	main.jwsClient.sendToken(token, {
		timeout: actions.TIMEOUT,

		OnTimeout: function() {
			console.error("[actions.startHistory] timed out.");
			alert("[actions.startHistory] timed out.");
		},

		OnResponse: function(aEvent) {
		},

		OnSuccess: function(aEvent) {
			console.debug("[actions.startHistory] success.");

			if (aEvent.ns == tokensFactory.NS_ADMIN_LISTENER) {
				switch (aEvent.reqType) {
				case tokensFactory.START_HISTORY_TKN:

					break;
				default:
					break;
				}
			}
		},

		OnFailure: function() {
			console.error("[actions.startHistory] failure.");
		}
	});
};

actions.switchMapType = function(event) {
	mainui.mapRenderer.switchMapType(event.data);
};

actions.showDeeperLevel = function(event) {
	mainui.dungeonRenderer.showDeeperLevel();
};

actions.showShallowerLevel = function(event) {
	mainui.dungeonRenderer.showShallowerLevel();
};