var tokensFactory = {};

//known namespaces
tokensFactory.NS_ADMIN_LISTENER = "cek.ruins.jws.listeners.AdminListener";
tokensFactory.NS_DUNGEONS_CREATION_LISTENER = "cek.ruins.jws.listeners.DungeonsCreationListener";

//tokens types
tokensFactory.CREATE_NEW_WORLD_TKN = 'createnewworld';
tokensFactory.REGIONS_INFOS_TKN = 'regionsinfos';
tokensFactory.REGION_INFO_TKN = 'regioninfo';
tokensFactory.CIVILIZATIONS_TEMPLATES_TKN = 'civilizationstemplates';
tokensFactory.ADD_CIVILIZATION_TKN = 'addcivilization';
tokensFactory.START_HISTORY_TKN = 'starthistory';
tokensFactory.CREATE_NEW_DUNGEON_TKN = 'createnewdungeon';
tokensFactory.GET_ENTITIES_TKN = 'getentities';

//events types
tokensFactory.NEW_LOCATION_EVENT = 1;
tokensFactory.UPDATE_LOCATIONS_EVENT = 2;
tokensFactory.DATE_CHANGE_EVENT = 3;
tokensFactory.DELETE_LOCATION_EVENT = 4;
tokensFactory.UPDATE_REGIONS_EVENT = 5;
tokensFactory.NEW_ROAD_EVENT = 6;

tokensFactory.createNewWorldPkt = function(seed, climate, temperature, sites) {
	var token = {
		ns: tokensFactory.NS_ADMIN_LISTENER,
		type: tokensFactory.CREATE_NEW_WORLD_TKN,
		seed: seed,
		climate: climate,
		temperature: temperature,
		sites: sites
	};

	return token;
};

tokensFactory.createNewDungeonPkt = function(seed) {
	var token = {
		ns: tokensFactory.NS_DUNGEONS_CREATION_LISTENER,
		type: tokensFactory.CREATE_NEW_DUNGEON_TKN,
		seed: seed,
	};

	return token;
};

tokensFactory.dungeonEntitiesPkt = function(depth) {
	var token = {
		ns: tokensFactory.NS_DUNGEONS_CREATION_LISTENER,
		type: tokensFactory.GET_ENTITIES_TKN,
		depth: depth,
	};

	return token;
};

tokensFactory.regionsInfosPkt = function() {
	var token = {
		ns: tokensFactory.NS_ADMIN_LISTENER,
		type: tokensFactory.REGIONS_INFOS_TKN,
	};

	return token;
};

tokensFactory.regionInfoPkt = function(rindex) {
	var token = {
		ns: tokensFactory.NS_ADMIN_LISTENER,
		type: tokensFactory.REGION_INFO_TKN,
		rindex: rindex,
	};

	return token;
};

tokensFactory.civilizationsTemplatesPkt = function() {
	var token = {
		ns: tokensFactory.NS_ADMIN_LISTENER,
		type: tokensFactory.CIVILIZATIONS_TEMPLATES_TKN,
	};

	return token;
};

tokensFactory.addNewCivilizationPkt = function(civindex, rindex, x, y) {
	var token = {
		ns: tokensFactory.NS_ADMIN_LISTENER,
		type: tokensFactory.ADD_CIVILIZATION_TKN,
		civindex: civindex,
		rindex: rindex,
		x: x,
		y: y,
	};

	return token;
};

tokensFactory.startHistoryPkt = function(endYear) {
	var token = {
		ns: tokensFactory.NS_ADMIN_LISTENER,
		type: tokensFactory.START_HISTORY_TKN,
		endYear: endYear,
	};

	return token;
};