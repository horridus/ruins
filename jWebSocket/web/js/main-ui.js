var mainui = {}

mainui.initGUI = function() {
	mainui.clock = new THREE.Clock();

	mainui.createLoginArea();
};

mainui.animateWorld = function() {
	requestAnimationFrame(mainui.animateWorld);
	mainui.mapRenderer.update(mainui.clock.getDelta());
	mainui.mapRenderer.render(mainui.clock.getDelta());
};

mainui.animateDungeon = function() {
	//requestAnimationFrame(mainui.animateDungeon);
	//mainui.mapRenderer.update(mainui.clock.getDelta());
	mainui.dungeonRenderer.render(mainui.clock.getDelta());
};

mainui.createLoginArea = function() {
	var loginHtml = $('#userArea').empty();
	loginHtml.append(['<fieldset id="loginContainer" class="center">',
	                   '	<input id="user_name" type="text" placeholder="Username" autofocus/>',
	                   '	<input id="user_password" type="password" placeholder="Password"/>',
	                   '</fieldset>',
	                   '<fieldset id="loginButtons" class="center">',
	                   '</fieldset>'].join('')
	                 		  );

	var button = WIDGETS.createButton('login', 'login_button', 180);
	button.click(actions.login);
	$('#loginButtons').append(button);
};

mainui.createLoggedinArea = function() {
	var loginHtml = $('#userArea').empty();
	loginHtml.append(['<fieldset id="loggedContainer">',
	                  '	<label>' + main.user.name + '</label>',
	                  '</fieldset>',
	                  '<fieldset id="loginButtons">',
	                  '</fieldset>',].join('')
	                 		  );

	var button = WIDGETS.createButton('logout', 'logout_button', 180);
	button.click(actions.logout);
	$('#loginButtons').append(button);
};

mainui.clearMenu = function() {
	return $('#navlist').empty();
};

mainui.clearContent = function() {
	return $('#content').empty();
};

mainui.clearHeader = function() {
	return $('#header').empty();
};

mainui.clearContentHeader = function() {
	return $('#contentHeader').empty();
};

mainui.createRegionInfoTable = function(regionInfo, siteInfo, politicalInfo) {
	var table = $(document.createElement('table'));
	var tbody = $(document.createElement('tbody'));
	table.append(tbody);

	var civilization = main.civilizations.existingCivilizations[politicalInfo.owner];

	if (civilization)
		tbody.append('<tr><td>owner</td><td>' + civilization.name + '</td></tr>')
	else
		tbody.append('<tr><td>owner</td><td>none</td></tr>')

	tbody.append('<tr><td>index</td><td>' + regionInfo.index + '</td></tr>')
	tbody.append('<tr><td>biome</td><td>' + regionInfo.biome + '</td></tr>')
	tbody.append('<tr><td>water</td><td>' + regionInfo.water + '</td></tr>')
	tbody.append('<tr><td>ocean</td><td>' + regionInfo.ocean + '</td></tr>')
	tbody.append('<tr><td>coast</td><td>' + regionInfo.coast + '</td></tr>')
	tbody.append('<tr><td>moisture</td><td>' + regionInfo.moisture.toFixed(2) + '</td></tr>')
	tbody.append('<tr><td>elevation</td><td>' + regionInfo.elevation.toFixed(2) + '</td></tr>')
	tbody.append('<tr><td>temperature</td><td>' + regionInfo.temperature.toFixed(1) + '</td></tr>')
	tbody.append('<tr><td>resources</td><td>' + regionInfo.principalResources + '</td></tr>')

	//TODO debug: remove!
	$.each(siteInfo.neighbors, function(index, value) {
		tbody.append('<tr><td>neighbor</td><td>' + value + '</td></tr>')
	});

	return table;
}

mainui.createAdminMenu = function() {
	var menuContainer = mainui.clearMenu();
	var menu = $(document.createElement('ul'));

	var entry = $(document.createElement('li'));
	var button = WIDGETS.createButton('Create new world', '', 180);
	button.click(mainui.createNewWorldForm);
	entry.append(button);
	
	menu.append(entry);
	
	entry = $(document.createElement('li'));
	button = WIDGETS.createButton('Create new dungeon', '', 180);
	button.click(mainui.createNewDungeonForm);
	entry.append(button);

	menu.append(entry);

	menuContainer.append(menu);
};

mainui.createNewWorldForm = function() {
	var container = mainui.clearContent();
	container.append(['<div style="margin-bottom: 2px;"><label>seed</label><input id="CNWseed" type="text" value="0" align="right"/></div>',
	                  '<div style="margin-bottom: 2px;"><label>climate</label>',
	                  '<select id="CNWclimate">',
	                  '<option value="dry">dry</option>',
	                  '<option value="tropical">tropical</option>',
	                  '<option value="temperate">temperate</option>',
	                  '<option value="continental">continental</option>',
	                  '<option value="polar">polar</option>',
	                  '</select>',
	                  '</div>',
	                  '<div><label>temperature</label>',
	                  '<input type="radio" name="temperature" value="freezing"/><label>freezing</label>',
	                  '<input type="radio" name="temperature" value="cold"/><label>cold</label>',
	                  '<input type="radio" name="temperature" value="mild" checked="true"/><label>mild</label>',
	                  '<input type="radio" name="temperature" value="hot"/><label>hot</label>',
	                  '<input type="radio" name="temperature" value="scorching"/><label>scorching</label>',
	                  '</div>',
	                  '<div><label>Sites</label>',
	                  '<input id="CNWsites" type="text" value="4000" align="right"/>',
	                  '</div>',
	                  '<div id="buttonsHolder"></div>'].join('')
					);

	var button = WIDGETS.createButton('create', 'submitCreateNewWorld', 200);
	button.click(mainui.createNewWorld);
	$('#buttonsHolder').append(button);
};

mainui.createNewDungeonForm = function() {
	var container = mainui.clearContent();
	container.append(['<div style="margin-bottom: 2px;"><label>seed</label><input id="CNDseed" type="text" value="0" align="right"/></div>',
	                  /*
	                  '<div style="margin-bottom: 2px;"><label>climate</label>',
	                  '<select id="CNWclimate">',
	                  '<option value="dry">dry</option>',
	                  '<option value="tropical">tropical</option>',
	                  '<option value="temperate">temperate</option>',
	                  '<option value="continental">continental</option>',
	                  '<option value="polar">polar</option>',
	                  '</select>',
	                  '</div>',
	                  '<div><label>temperature</label>',
	                  '<input type="radio" name="temperature" value="freezing"/><label>freezing</label>',
	                  '<input type="radio" name="temperature" value="cold"/><label>cold</label>',
	                  '<input type="radio" name="temperature" value="mild" checked="true"/><label>mild</label>',
	                  '<input type="radio" name="temperature" value="hot"/><label>hot</label>',
	                  '<input type="radio" name="temperature" value="scorching"/><label>scorching</label>',
	                  '</div>',
	                  '<div><label>Sites</label>',
	                  '<input id="CNWsites" type="text" value="4000" align="right"/>',
	                  '</div>',
	                  */
	                  '<div id="buttonsHolder"></div>'].join('')
					);

	var button = WIDGETS.createButton('create', 'submitCreateNewDungeon', 200);
	button.click(mainui.createNewDungeon);
	$('#buttonsHolder').append(button);
};

mainui.createNewWorld = function() {
	var CNWseed = $('#CNWseed').val();
	var CNWclimate = $('#CNWclimate').val();
	var CWNtemperature = $('input[name=temperature]:checked').val();
	var CWNsites = $('#CNWsites').val();

	actions.createNewWorld(CNWseed, CNWclimate, CWNtemperature, CWNsites);
};

mainui.createNewDungeon = function() {
	var CNDseed = $('#CNWseed').val();

	actions.createNewDungeon(CNDseed);
};

mainui.showWorld = function(id) {
	var container = mainui.clearContent();

	var img = new Image();
	$(img).load(function() {
		var canvas = document.createElement('canvas');
		canvas.width = img.width;
		canvas.height = img.height;

		var ctx = canvas.getContext('2d');
		ctx.drawImage(img, 0, 0);

		var imgData = ctx.getImageData(0, 0, img.width, img.height);

		main.map = new RUINS.Map(img.width, imgData.data);
		mainui.mapRenderer = new RUINS.MapRenderer(main.map, $('#column1').width(), $('#column1').height(), '/worlds/' + id, container);

		mainui.animateWorld();

		mainui.updateDate();
	});

	$(img).attr('src', '/worlds/' + id + '/rindex.png');
};

mainui.showDungeon = function(dungeonData) {
	var container = mainui.clearContent();
	main.dungeon = new RUINS.Dungeon(dungeonData.id, dungeonData.name, dungeonData.size, dungeonData.data);
	mainui.dungeonRenderer = new RUINS.DungeonRenderer(main.dungeon, $('#column1').width(), $('#column1').height(), '/dungeons/' + dungeonData.id, container, 32);
	
	mainui.animateDungeon();
};

mainui.updateDate = function() {
	// create map header
	var header = mainui.clearContentHeader();
	var date = WIDGETS.createDate(main.day, main.month, main.year, main.season);
	header.append(date);
};

mainui.showRegionInfo = function(rindex, x, y) {
	if (rindex >= 0) {
		var rinfo = main.map.regions[rindex];

		/*
		if (!rinfo) {
			$('#mapInfoArea').empty();
			actions.regionInfo(rindex);
		}
		else {
			$('#mapInfoArea').empty();
			$('#mapInfoArea').append(mainui.createRegionInfoTable(rinfo.region, rinfo.site, rinfo.political));
		}
		*/
		
		if (rinfo) {
			$('#mapInfoArea').empty();
			$('#mapInfoArea').append(mainui.createRegionInfoTable(rinfo.region, rinfo.site, rinfo.political));
		}
	}
	else {
		$('#mapInfoArea').empty();
	}
};

mainui.showCivilizationsTemplates = function(templates) {
	var menuContainer = mainui.clearMenu();

	for (var i = 0, n = templates.length; i < n; i++) {
		var civtemplate = $(document.createElement('div'));
		civtemplate.attr('id', 'civtemplate_' + templates[i].id);
		civtemplate.attr('icon', templates[i].iconsFolder + "/species.png");

		civtemplate.draggable({
			helper: function(event) {
				var draggableHelper = $(document.createElement('img'));
				draggableHelper.attr('src', main.configuration.imgs_location + $(event.currentTarget).attr('icon'));

				return draggableHelper;
			},
			cursorAt: {
				top: 0,
				left: 0,
			},
		});

		var icon = $(document.createElement('img'));

		icon.hide();

		icon.load(function(event) {

			 // TODO spostare in funzione per caricare immagini?

			 $(this).fadeIn();
		});
		icon.attr('src', main.configuration.imgs_location + templates[i].iconsFolder + "/species.png");

		icon.addClass('rounded icon');
		civtemplate.append(icon);

		var species = $(document.createElement('span')).text(templates[i].species);
		species.addClass('rightLabel');
		civtemplate.append(species);

		menuContainer.append(civtemplate);
	}
};

mainui.showWorldCreationMenu = function() {
	var menuContainer = mainui.clearHeader();
	var menuBlock = $(document.createElement('div'));

	var button = WIDGETS.createButton('run 10 years of history', 'startHistoryBtn', 180);
	button.on('click', null, 10, actions.startHistory);
	menuBlock.append(button);

	button = WIDGETS.createButton('run 50 years of history', 'startHistoryBtn', 180);
	button.on('click', null, 50, actions.startHistory);
	menuBlock.append(button);

	button = WIDGETS.createButton('run 100 years of history', 'startHistoryBtn', 180);
	button.on('click', null, 100, actions.startHistory);
	menuBlock.append(button);

	button = WIDGETS.createButton('political map', '', 180);
	button.on('click', null, 'political', actions.switchMapType);
	menuBlock.append(button);

	button = WIDGETS.createButton('geographical map', '', 180);
	button.on('click', null, 'geographical', actions.switchMapType);
	menuBlock.append(button);

	button = WIDGETS.createButton('biomes map', '', 180);
	button.on('click', null, 'biomes', actions.switchMapType);
	menuBlock.append(button);

	button = WIDGETS.createButton('elevation map', '', 180);
	button.on('click', null, 'elevation', actions.switchMapType);
	menuBlock.append(button);

	button = WIDGETS.createButton('temperature map', '', 180);
	button.on('click', null, 'temperature', actions.switchMapType);
	menuBlock.append(button);

	button = WIDGETS.createButton('moisture map', '', 180);
	button.on('click', null, 'moisture', actions.switchMapType);
	menuBlock.append(button);

	menuContainer.append(menuBlock);
};

mainui.addNewCivilization = function(civIndex, rindex, x, y) {

	// TODO inserire eventualmente un form di conferma e configurazione

	actions.addNewCivilization(civIndex, rindex, x, y);
};

mainui.showLocationInfo = function(selectedLocationType, selectedLocationIconId) {
	var id = mainui.mapRenderer.getLocationIdFromIcon(selectedLocationIconId);
	var location = main.map.getLocation(id, selectedLocationType);

	switch (selectedLocationType) {
	case RUINS.LOCATION_TYPES.SETTLEMENT:
		mainui.showSettlementInfo(location, $('#mapInfoArea'));
		break;
	}
};

mainui.showSettlementInfo = function(settlement, container) {
	var ownerCivilization = main.civilizations.getCivilization(settlement.ownerCivililizationId);

	// TEMP
	container.empty();

	var infoBlock = $(document.createElement('div'));
	infoBlock.append("<div><span style='font-weight:bold;'>" + settlement.name + "</span></div>");
	infoBlock.append("<div><span>civilization </span><span>" + ownerCivilization.name + "</span></div>");
	infoBlock.append("<div><span>species </span><span>" + ownerCivilization.species + "</span></div>");
	infoBlock.append("<div><span>population </span><span>" + settlement.population + "</span></div>");
	infoBlock.append("<div><span>coords </span><span>" + settlement.x + " " + settlement.y + "</span></div>");

	infoBlock.append("<div><span>level </span><span>" + settlement.level + "</span></div>");
	infoBlock.append("<div><span>region </span><span>" + settlement.regionIndex + "</span></div>");

	container.append(infoBlock);
};