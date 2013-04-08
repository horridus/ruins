var eventsHandlers = {
};

eventsHandlers.newLocation = function(event) {
	switch (event.locationType) {
	case 'settlement':
		var settlement = window.JSON.parse(event.location);
		var owner = main.civilizations.getCivilization(settlement.ownerCivililizationId);
		main.map.addSettlement(settlement);
		mainui.mapRenderer.addSettlement(settlement, owner);
		break;
	case 'cave':
		var cave = window.JSON.parse(event.location);
		main.map.addCave(cave);
		mainui.mapRenderer.addCave(cave);
		break;
	default:
		break;
	}
};

eventsHandlers.deleteLocation = function(event) {
	switch (event.locationType) {
	case 'settlement':
		var settlementId = event.locationId;
		mainui.mapRenderer.removeSettlement(settlementId);
		main.map.removeSettlement(settlementId);
		break;

	default:
		break;
	}
};

eventsHandlers.updateLocations = function(event) {
	var update = event.settlements;
	if (update) {
		for (var locationId in update) {
			var settlement = main.map.settlements[locationId];
			var innerUpdate = update[locationId];

			if (innerUpdate.population)
				settlement.population = innerUpdate.population;
			if (innerUpdate.level) {
				settlement.level = innerUpdate.level;
				mainui.mapRenderer.settlementLevelUpated(settlement);
			}

			//TODO finire il resto delle properties di settlement
		}
	}
};

eventsHandlers.updateRegions = function(event) {
	var update = event.political;
	if (update) {
		for (var regionId in update) {
			var region = main.map.regions[regionId];
			var innerUpdate = update[regionId];

			if (innerUpdate.owner || innerUpdate.owner === '') {
				region.political.owner = innerUpdate.owner;

				//region has no political owner now
				if (innerUpdate.owner === '') {
					mainui.mapRenderer.resetPoliticalMap(regionId);
				}
			}

			//TODO finire il resto delle properties di settlement
		}
	}
};

eventsHandlers.dateChange = function(event) {
	var date = event.date;
	main.day = date.day;
	main.month = date.month;
	main.year = date.year;
	main.season = date.season;
};

eventsHandlers.newRoad = function(event) {
	var road = window.JSON.parse(event.road);
	mainui.mapRenderer.newRoad(road);
};
