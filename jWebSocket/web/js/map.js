RUINS.Map = function(size, rindexData) {
	this.rindexMap = [];
	this.regions = [];
	this.settlements = {};
	this.caves = {};

	this.size = size;

	//extract region id from every pixel
    for(var i = 0, n = rindexData.length; i < n; i += 4) {
      var highByte = rindexData[i];
      var lowByte = rindexData[i + 1];

      var index = (highByte << 8) + lowByte; //FIXME testare anche ((highByte << 8) | lowByte)
      this.rindexMap.push(index - 1);
    }
};

RUINS.Map.prototype.rindex = function(posx, posy) {
	return this.rindexMap[posx + posy * this.size];
};

RUINS.Map.prototype.addRegion = function(region, site, political) {
	this.regions[region.index] = { region: region, site: site, political: political};
};

RUINS.Map.prototype.addSettlement = function(settlement) {
	this.settlements[settlement.id] = settlement;
};

RUINS.Map.prototype.removeSettlement = function(settlementId) {
	delete this.settlements[settlementId];
};

RUINS.Map.prototype.addCave = function(cave) {
	this.caves[cave.id] = cave;
};

RUINS.Map.prototype.removeCave = function(caveId) {
	delete this.caves[caveId];
};

RUINS.Map.prototype.getLocation = function(locationId, locationType) {
	switch (locationType) {
	case RUINS.LOCATION_TYPES.SETTLEMENT:
		return this.settlements[locationId];
		break;
	}
};

/*---------------------------------------------*/
RUINS.MapRenderer = function(map, width, height, texturePath, container) {
	this.map = map;
	this.width = width;
	this.height = height;

	this.settlementsIcons = [];
	this.settlementsIconsPickers = [];

	this.renderer = new THREE.WebGLRenderer();
	this.renderer.setSize(width, height);
	container.append(this.renderer.domElement);

	this.scene = new THREE.Scene();

	this.camera = new THREE.OrthographicCamera(-width/2.0, width/2.0, -height/2.0, height/2.0, 0.1, 100);

	this.camera.position.x = this.map.size/2.0;
	this.camera.position.y = this.map.size/2.0;
	this.camera.position.z = 1.0;
	this.camera.lookAt(new THREE.Vector3(this.map.size/2.0, this.map.size/2.0, 0));
	this.scene.add(this.camera);
	
	this.ICONSIZE = 32.0;
	this.ICONZOOMTHRESHOLD = this.ICONSIZE / this.map.size; 
	this.ICONMAPRATIO = 1.0/this.ICONSIZE; //icon texel size to map texel size ratio
	
	//private vars
	var tmpCanvas = document.createElement('canvas');
	var ctx = tmpCanvas.getContext('2d');

	//map textures
	this.geographicalTexture = THREE.ImageUtils.loadTexture(texturePath + '/paper.png');
	this.geographicalTexture.magFilter = THREE.NearestFilter;
	this.geographicalTexture.minFilter = THREE.NearestFilter;
	this.geographicalTexture.flipY = false;

	this.elevationTexture = THREE.ImageUtils.loadTexture(texturePath + '/elevation.png');
	this.elevationTexture.magFilter = THREE.NearestFilter;
	this.elevationTexture.minFilter = THREE.NearestFilter;
	this.elevationTexture.flipY = false;

	this.moistureTexture = THREE.ImageUtils.loadTexture(texturePath + '/moisture.png');
	this.moistureTexture.magFilter = THREE.NearestFilter;
	this.moistureTexture.minFilter = THREE.NearestFilter;
	this.moistureTexture.flipY = false;

	this.temperatureTexture = THREE.ImageUtils.loadTexture(texturePath + '/temperature.png');
	this.temperatureTexture.magFilter = THREE.NearestFilter;
	this.temperatureTexture.minFilter = THREE.NearestFilter;
	this.temperatureTexture.flipY = false;

	this.biomesTexture = THREE.ImageUtils.loadTexture(texturePath + '/biomes.png');
	this.biomesTexture.magFilter = THREE.NearestFilter;
	this.biomesTexture.minFilter = THREE.NearestFilter;
	this.biomesTexture.flipY = false;

	this.politicalTexture = new THREE.Texture();
	this.politicalTexture.flipY = false;
	this.politicalTexture.needsUpdate = true;

	this.overlayTexture = new THREE.Texture();
	this.overlayTexture.magFilter = THREE.NearestFilter;
	this.overlayTexture.minFilter = THREE.NearestFilter;
	this.overlayTexture.flipY = false;
	this.overlayTexture.needsUpdate = true;

	//prepare map materials
	tmpCanvas.width = map.size;
    tmpCanvas.height = map.size;
    ctx.fillStyle = 'rgba(0, 0, 0, 0)';
    ctx.fillRect(0, 0, tmpCanvas.width, tmpCanvas.height);
    this.overlayTexture.image = ctx.getImageData(0, 0, tmpCanvas.width, tmpCanvas.height);

    tmpCanvas.width = map.size;
    tmpCanvas.height = map.size;
    ctx.fillStyle = 'rgba(0, 0, 0, 0)';
    ctx.fillRect(0, 0, tmpCanvas.width, tmpCanvas.height);
    this.politicalTexture.image = ctx.getImageData(0, 0, tmpCanvas.width, tmpCanvas.height);

    this.politicalMapMaterial = new THREE.ShaderMaterial({
    	uniforms: {
    		geographicalTexture: { type: 't', value: this.geographicalTexture },
    		politicalTexture: { type: 't', value: this.politicalTexture },
    		overlayTexture: {type: 't', value: this.overlayTexture },
    	},
    	vertexShader: RUINS.SHADERS['politicalMap'].vertexShader,
    	fragmentShader: RUINS.SHADERS['politicalMap'].fragmentShader,
    	side: THREE.BackSide,
    });

    this.mapBaseMaterial = new THREE.MeshBasicMaterial({ map: this.geographicalTexture, wireframe: false, side: THREE.BackSide});

	//create map geometry
	this.mapPlane = new THREE.Mesh(new THREE.PlaneGeometry(this.map.size, this.map.size, this.map.size/100, this.map.size/100), this.politicalMapMaterial);
	//this.mapPlane.rotation.x = -Math.PI/2;
	//this.mapPlane.updateMatrix();
	this.mapPlane.position.x = this.map.size/2;
	this.mapPlane.position.y = this.map.size/2;
	//this.mapPlane.position.z = 0.2;
	this.mapPlane.updateMatrix();
	this.mapPlane.matrixAutoUpdate = false; //no need to transform plane ever again
	this.scene.add(this.mapPlane);

	this.scene.add(new THREE.AxisHelper); //TEMP

	//add map controls to scene
	this.controls = new THREE.MapControls(this.camera, container, this.map, this);
	this.controls.movementSpeed = 200.0;
	this.controls.setBoundingBox({min: new THREE.Vector3(0, 0, 0), max: new THREE.Vector3(this.map.size, this.map.size, this.map.size)});
	//this.controls.setBoundingBox({min: new THREE.Vector3(-this.map.size, -this.map.size, -this.map.size), max: new THREE.Vector3(this.map.size, this.map.size, this.map.size)});

	//variables for locations icons picking
	this.pickersRenderingTarget = new THREE.WebGLRenderTarget(this.width, this.height, { minFilter: THREE.LinearFilter, magFilter: THREE.NearestFilter, format: THREE.RGBAFormat });
	this.iconsPickersScene = new THREE.Scene();
	this.iconsPickersScene.add(this.camera);
	this.iconsIdtoLocationId = {};

	this.iconsRepository = {};

    //create white image for "sprite mouse picking" rendering pass
    tmpCanvas.width = this.ICONSIZE;
    tmpCanvas.height = this.ICONSIZE;
    ctx.fillStyle = '#ffffff';
    ctx.fillRect(0, 0, tmpCanvas.width, tmpCanvas.height);

    this.spriteNeutralBackground = new THREE.Texture(ctx.getImageData(0, 0, tmpCanvas.width, tmpCanvas.height));
    this.spriteNeutralBackground.needsUpdate = true;

    //gl context for internal methods
    this.glCtx = this.renderer.getContext();
};

RUINS.MapRenderer.prototype.update = function(clockDelta) {
	this.controls.update(clockDelta);

	//var zoom = 1.0 - this.controls.zoom;
	
	var zoom = 1.0;
	if (this.controls.zoom < this.ICONZOOMTHRESHOLD) {
		zoom = this.ICONMAPRATIO / this.controls.zoom;
	}
	
	for (var id in this.iconsRepository) {
		var reference = this.iconsRepository[id];
		reference.icon.scale = new THREE.Vector3(zoom, zoom, zoom);
		reference.iconPicker.scale = new THREE.Vector3(zoom, zoom, zoom);
	}
	/*
	for (var i=0, len=this.settlementsIcons.length; i<len; i++) {
		this.settlementsIcons[i].scale = new THREE.Vector3(zoom, zoom, zoom);

		this.settlementsIconsPickers[i].scale = new THREE.Vector3(zoom, zoom, zoom);
	}
	*/
};

RUINS.MapRenderer.prototype.render = function(clockDelta) {
	this.renderer.render(this.iconsPickersScene, this.camera, this.pickersRenderingTarget);

	//pick id of icon under the mouse
	var pixelBuffer = new Uint8Array(4);
	this.glCtx.readPixels(this.controls.mouseCanvasXCoord, this.pickersRenderingTarget.height - this.controls.mouseCanvasYCoord, 1, 1, this.glCtx.RGBA, this.glCtx.UNSIGNED_BYTE, pixelBuffer);
	this.controls.selectedIcon = ((pixelBuffer[0] << 16) | pixelBuffer[1] << 8 | pixelBuffer[2]);
	this.controls.selectedIconType = pixelBuffer[2];

	this.renderer.render(this.scene, this.camera);
};

RUINS.MapRenderer.prototype.addSettlement = function(settlement, civilization) {
	//TEMP/////////////////////////////////////////////////////////
	//var texture = THREE.ImageUtils.loadTexture( "imgs/locations/tower.png" );
	var texture = THREE.ImageUtils.loadTexture( "imgs/locations/camp.png" );
	//var texture = THREE.ImageUtils.loadTexture( "imgs/locations/farmhouse.png" );
	//var texture = THREE.ImageUtils.loadTexture( "imgs/locations/smalltown.png" );
	///////////////////////////////////////////////////////////////

	var settlementIcon = this.newIcon(texture, new THREE.Vector3(settlement.x, settlement.y, -1.0));
	this.scene.add(settlementIcon);
	
	var settlementIconPicker = this.newIcon(this.spriteNeutralBackground, new THREE.Vector3(settlement.x, settlement.y, -1.0));
	var id = (Object.keys(this.iconsRepository).length << 8 | RUINS.LOCATION_TYPES.SETTLEMENT);
	settlementIconPicker.color = new THREE.Color(id);
	this.iconsPickersScene.add(settlementIconPicker);
	
	//map icon id to location id
	this.iconsIdtoLocationId[id] = settlement.id;

	this.iconsRepository[settlement.id] = {
		type: 'settlement',
		icon: settlementIcon,
		iconPicker: settlementIconPicker,
		iconId: id
	};

	this.updatePoliticalMap(settlement.regionIndex, parseInt(civilization.coat.pattern), RUINS.UTILS.hexToRgb(civilization.coat.primary), RUINS.UTILS.hexToRgb(civilization.coat.secondary));
	
	/*
	var settlementIcon = new THREE.Sprite({
		map: texture,
		useScreenCoordinates: false,
		affectedByDistance: true,
		scaleByViewport: true,
		mergeWith3D: false,
		//color: 0xffffff
	});
	var settlementIconPicker = new THREE.Sprite({
		map: this.spriteNeutralBackground,
		useScreenCoordinates: false,
		affectedByDistance: true,
		scaleByViewport: true,
		mergeWith3D: false,
	});

	settlementIcon.position.set(settlement.x, settlement.y, -1.0);
	settlementIconPicker.position.set(settlement.x, settlement.y, -1.0);

	var zoom = 1.0 - this.controls.zoom;
	//var id = (this.settlementsIcons.length << 8 | RUINS.LOCATION_TYPES.SETTLEMENT);
	var id = (Object.keys(this.iconsRepository).length << 8 | RUINS.LOCATION_TYPES.SETTLEMENT);

	settlementIconPicker.color = new THREE.Color(id);

	settlementIcon.scale = new THREE.Vector3(zoom, zoom, zoom);
	this.scene.add(settlementIcon);
	//this.settlementsIcons.push(settlementIcon);

	settlementIconPicker.scale = new THREE.Vector3(zoom, zoom, zoom);
	this.iconsPickersScene.add(settlementIconPicker);
	//this.settlementsIconsPickers.push(settlementIconPicker);

	//map icon id to location id
	this.iconsIdtoLocationId[id] = settlement.id;

	this.iconsRepository[settlement.id] = {
		type: 'settlement',
		icon: settlementIcon,
		iconPicker: settlementIconPicker,
		iconId: id
	};

	this.updatePoliticalMap(settlement.regionIndex, {r:255, g:0, b:0, a:255});
	*/
};

RUINS.MapRenderer.prototype.addCave = function(cave) {
	var texture = THREE.ImageUtils.loadTexture( "imgs/locations/cave.png" );

	var caveIcon = this.newIcon(texture, new THREE.Vector3(cave.x, cave.y, -1.0));
	this.scene.add(caveIcon);
	
	var caveIconPicker = this.newIcon(this.spriteNeutralBackground, new THREE.Vector3(cave.x, cave.y, -1.0));
	var id = (Object.keys(this.iconsRepository).length << 8 | RUINS.LOCATION_TYPES.CAVE);
	caveIconPicker.color = new THREE.Color(id);
	this.iconsPickersScene.add(caveIconPicker);
	
	//map icon id to location id
	this.iconsIdtoLocationId[id] = cave.id;

	this.iconsRepository[cave.id] = {
		type: 'cave',
		icon: caveIcon,
		iconPicker: caveIconPicker,
		iconId: id
	};
	
	/*
	var caveIcon = new THREE.Sprite({
		map: texture,
		useScreenCoordinates: false,
		affectedByDistance: true,
		scaleByViewport: true,
		mergeWith3D: false,
		//color: 0xffffff
	});
	var caveIconPicker = new THREE.Sprite({
		map: this.spriteNeutralBackground,
		useScreenCoordinates: false,
		affectedByDistance: true,
		scaleByViewport: true,
		mergeWith3D: false,
	});

	caveIcon.position.set(cave.x, cave.y, -1.0);
	caveIconPicker.position.set(cave.x, cave.y, -1.0);

	var zoom = 1.0 - this.controls.zoom;
	//var id = (this.settlementsIcons.length << 8 | RUINS.LOCATION_TYPES.SETTLEMENT);
	
	var id = (Object.keys(this.iconsRepository).length << 8 | RUINS.LOCATION_TYPES.CAVE);
	caveIconPicker.color = new THREE.Color(id);

	caveIcon.scale = new THREE.Vector3(zoom, zoom, zoom);
	this.scene.add(caveIcon);
	//this.settlementsIcons.push(caveIcon);

	caveIconPicker.scale = new THREE.Vector3(zoom, zoom, zoom);
	this.iconsPickersScene.add(caveIconPicker);
	//this.settlementsIconsPickers.push(caveIconPicker);

	//map icon id to location id
	this.iconsIdtoLocationId[id] = cave.id;

	this.iconsRepository[cave.id] = {
		type: 'cave',
		icon: caveIcon,
		iconPicker: caveIconPicker,
		iconId: id
	};
	*/
};

RUINS.MapRenderer.prototype.removeSettlement = function(settlementId) {
	var reference = this.iconsRepository[settlementId];

	this.scene.remove(reference.icon);
	this.iconsPickersScene.remove(reference.iconPicker);

	delete this.iconsIdtoLocationId[reference.id];

	/*
	this.settlementsIconsPickers = $.grep(this.settlementsIconsPickers, function(value) {
		return reference.settlementIconPicker != value;
	});

	this.settlementsIcons = $.grep(this.settlementsIcons, function(value) {
		return reference.settlementIcon != value;
	});
	*/

	delete this.iconsRepository[settlementId];

	//this.resetPoliticalMap(this.map.settlements[settlementId].regionIndex);
};

RUINS.MapRenderer.prototype.settlementLevelUpated = function(settlement) {
	var reference = this.iconsRepository[settlement.id];

	this.scene.remove(reference.icon);
	/*
	this.settlementsIcons = $.grep(this.settlementsIcons, function(value) {
		return reference.settlementIcon != value;
	});
	*/

	//TEMP
	var texture;
	switch (settlement.level) {
	case 0:
		texture = THREE.ImageUtils.loadTexture( "imgs/locations/camp.png" );
		break;
	case 1:
		texture = THREE.ImageUtils.loadTexture( "imgs/locations/smallhamlet.png" );
		break;

	default:
		texture = THREE.ImageUtils.loadTexture( "imgs/locations/camp.png" );
		break;
	}
	/////

	var settlementIcon = this.newIcon(texture, new THREE.Vector3(settlement.x, settlement.y, -1.0));
	this.scene.add(settlementIcon);
	
	/*
	var settlementIcon = new THREE.Sprite({
		map: texture,
		useScreenCoordinates: false,
		affectedByDistance: true,
		scaleByViewport: true,
		mergeWith3D: false,
		//color: 0xffffff
	});

	settlementIcon.position.set(settlement.x, settlement.y, -1.0);

	var zoom = 1.0 - this.controls.zoom;
	settlementIcon.scale = new THREE.Vector3(zoom, zoom, zoom);
	//this.settlementsIcons.push(settlementIcon);
	*/

	this.iconsRepository[settlement.id].icon = settlementIcon;
};

RUINS.MapRenderer.prototype.getLocationIdFromIcon = function(selectedLocationIconId) {
	return this.iconsIdtoLocationId[selectedLocationIconId];
};

RUINS.MapRenderer.prototype.switchMapType = function(type) {
	switch (type) {
	case 'political':
		this.mapPlane.material = this.politicalMapMaterial;
		this.mapPlane.material.needsUpdate = true;
		break;
	case 'geographical':
		this.mapBaseMaterial.map = this.geographicalTexture;
		this.mapPlane.material = this.mapBaseMaterial;
		this.mapPlane.material.map.needsUpdate = true;
		break;
	case 'biomes':
		this.mapBaseMaterial.map = this.biomesTexture;
		this.mapPlane.material = this.mapBaseMaterial;
		this.mapPlane.material.map.needsUpdate = true;
		break;
	case 'elevation':
		this.mapBaseMaterial.map = this.elevationTexture;
		this.mapPlane.material = this.mapBaseMaterial;
		this.mapPlane.material.map.needsUpdate = true;
		break;
	case 'temperature':
		this.mapBaseMaterial.map = this.temperatureTexture;
		this.mapPlane.material = this.mapBaseMaterial;
		this.mapPlane.material.map.needsUpdate = true;
		break;
	case 'moisture':
		this.mapBaseMaterial.map = this.moistureTexture;
		this.mapPlane.material = this.mapBaseMaterial;
		this.mapPlane.material.map.needsUpdate = true;
		break;
	}
};

RUINS.MapRenderer.prototype.resetPoliticalMap = function(regionIndex) {
	this.updatePoliticalMap(regionIndex, 0, {r:0, g:0, b:0, a:0});
};

/*
RUINS.MapRenderer.prototype.updatePoliticalMap = function(regionIndex, color) {
	var textureData = this.politicalTexture.image.data;
	for (var i=0, len = this.map.rindexMap.length; i<len; i++) {
		var rindex = this.map.rindexMap[i];
		if (rindex == regionIndex) {
			textureData[i*4+0] = color.r;
			textureData[i*4+1] = color.g;
			textureData[i*4+2] = color.b;
			textureData[i*4+3] = color.a;
		}
	}
	this.politicalTexture.needsUpdate = true;
};
*/

RUINS.MapRenderer.prototype.updatePoliticalMap = function(regionIndex, pattern, colora, colorb) {
	var textureData = this.politicalTexture.image.data;
	
	var len = this.map.size;
	
	for (var y=0; y<len; y++) {
		for (var x=0; x<len; x++) {
			var i = x + y * len;
			var rindex = this.map.rindexMap[i];
			if (rindex == regionIndex) {
				switch (pattern) {
				case 0:
					textureData[i*4+0] = colora.r;
					textureData[i*4+1] = colora.g;
					textureData[i*4+2] = colora.b;
					textureData[i*4+3] = colora.a;
					break;
				case 1:
					var mod = y%8;
					if (mod < 4) {
						textureData[i*4+0] = colora.r;
						textureData[i*4+1] = colora.g;
						textureData[i*4+2] = colora.b;
						textureData[i*4+3] = colora.a;
					}
					else {
						textureData[i*4+0] = colorb.r;
						textureData[i*4+1] = colorb.g;
						textureData[i*4+2] = colorb.b;
						textureData[i*4+3] = colorb.a;
					}
					break;
				case 2:
					var mod = x%8;
					if (mod < 4) {
						textureData[i*4+0] = colora.r;
						textureData[i*4+1] = colora.g;
						textureData[i*4+2] = colora.b;
						textureData[i*4+3] = colora.a;
					}
					else {
						textureData[i*4+0] = colorb.r;
						textureData[i*4+1] = colorb.g;
						textureData[i*4+2] = colorb.b;
						textureData[i*4+3] = colorb.a;
					}
					break;
				case 3:
					var mody = y%3;
					var modx = x%4;
					switch (mody) {
					case 0:
						if (modx < 2) {
							textureData[i*4+0] = colora.r;
							textureData[i*4+1] = colora.g;
							textureData[i*4+2] = colora.b;
							textureData[i*4+3] = colora.a;
						}
						else {
							textureData[i*4+0] = colorb.r;
							textureData[i*4+1] = colorb.g;
							textureData[i*4+2] = colorb.b;
							textureData[i*4+3] = colorb.a;
						}
						break;
					case 1:
						if (modx != 0 && modx != 3) {
							textureData[i*4+0] = colora.r;
							textureData[i*4+1] = colora.g;
							textureData[i*4+2] = colora.b;
							textureData[i*4+3] = colora.a;
						}
						else {
							textureData[i*4+0] = colorb.r;
							textureData[i*4+1] = colorb.g;
							textureData[i*4+2] = colorb.b;
							textureData[i*4+3] = colorb.a;
						}
						break;
					case 2:
						if (modx > 1) {
							textureData[i*4+0] = colora.r;
							textureData[i*4+1] = colora.g;
							textureData[i*4+2] = colora.b;
							textureData[i*4+3] = colora.a;
						}
						else {
							textureData[i*4+0] = colorb.r;
							textureData[i*4+1] = colorb.g;
							textureData[i*4+2] = colorb.b;
							textureData[i*4+3] = colorb.a;
						}
						break;
					}
					break;
				case 4:
					var mody = y%3;
					var modx = x%4;
					switch (mody) {
					case 0:
						if (modx > 1) {
							textureData[i*4+0] = colora.r;
							textureData[i*4+1] = colora.g;
							textureData[i*4+2] = colora.b;
							textureData[i*4+3] = colora.a;
						}
						else {
							textureData[i*4+0] = colorb.r;
							textureData[i*4+1] = colorb.g;
							textureData[i*4+2] = colorb.b;
							textureData[i*4+3] = colorb.a;
						}
						break;
					case 1:
						if (modx != 0 && modx != 3) {
							textureData[i*4+0] = colora.r;
							textureData[i*4+1] = colora.g;
							textureData[i*4+2] = colora.b;
							textureData[i*4+3] = colora.a;
						}
						else {
							textureData[i*4+0] = colorb.r;
							textureData[i*4+1] = colorb.g;
							textureData[i*4+2] = colorb.b;
							textureData[i*4+3] = colorb.a;
						}
						break;
					case 2:
						if (modx < 2) {
							textureData[i*4+0] = colora.r;
							textureData[i*4+1] = colora.g;
							textureData[i*4+2] = colora.b;
							textureData[i*4+3] = colora.a;
						}
						else {
							textureData[i*4+0] = colorb.r;
							textureData[i*4+1] = colorb.g;
							textureData[i*4+2] = colorb.b;
							textureData[i*4+3] = colorb.a;
						}
						break;
					}
					break;
				default:
					textureData[i*4+0] = colora.r;
					textureData[i*4+1] = colora.g;
					textureData[i*4+2] = colora.b;
					textureData[i*4+3] = colora.a;
					break;
				}
			}
		}
	}
	this.politicalTexture.needsUpdate = true;
};

RUINS.MapRenderer.prototype.newRoad = function(road) {
	//TEMP///
	//var tmpCanvas = document.createElement('canvas');
	//var ctx = tmpCanvas.getContext('2d');
	//tmpCanvas.width = this.map.size;
    //tmpCanvas.height = this.map.size;

	//ctx.putImageData(this.overlayTexture.image, 0, 0);

	var cps = road.cps;

	//ctx.lineWidth = 1;

	/*
	ctx.beginPath();
	var center = this.map.regions[path[0]].site.center;
	var x = Math.floor(center.x) + 0.5;
	var y = Math.floor(center.y) + 0.5;

	ctx.moveTo(x, y);
	for (var i = 1, len = path.length; i < len; i++) {
		center = this.map.regions[path[i]].site.center;
		x = Math.floor(center.x) + 0.5;
		y = Math.floor(center.y) + 0.5;

		ctx.lineTo(x, y);
	}

	ctx.strokeStyle = 'rgba(0, 0, 0, 255)';
	ctx.stroke();
	*/

	/*
	var center = this.map.regions[path[0]].site.center;
	var xs = Math.floor(center.x);
	var ys = Math.floor(center.y);

	for (var i = 1, len = path.length; i < len; i++) {
		center = this.map.regions[path[i]].site.center;
		var xd = Math.floor(center.x);
		var yd = Math.floor(center.y);

		RUINS.UTILS.line(xs, ys, xd, yd, this.overlayTexture.image, {r:0, g:0, b:0, a:255});

		xs = xd;
		ys = yd;
	}
	*/

	var mod = (cps.length%3);
	var len = cps.length - mod;

	if (len > 0 && mod > 1) {
		for (var i = 0; i < len; i+=3) {
			var controlPoint = cps[i+0];
			var p0 = {x:Math.floor(controlPoint.x), y:Math.floor(controlPoint.y)};
			controlPoint = cps[i+1];
			var p1 = {x:Math.floor(controlPoint.x), y:Math.floor(controlPoint.y)};
			controlPoint = cps[i+2];
			var p2 = {x:Math.floor(controlPoint.x), y:Math.floor(controlPoint.y)};
			controlPoint = cps[i+3];
			var p3 = {x:Math.floor(controlPoint.x), y:Math.floor(controlPoint.y)};

			RUINS.UTILS.bezierCurve([p0, p1, p2, p3], this.overlayTexture.image, {r:89, g:39, b:32, a:255});
		}

		/*
		var controlPoint = cps[cps.length-4];
		var p0 = {x:Math.floor(controlPoint.x), y:Math.floor(controlPoint.y)};
		controlPoint = cps[cps.length-3];
		var p1 = {x:Math.floor(controlPoint.x), y:Math.floor(controlPoint.y)};
		controlPoint = cps[cps.length-2];
		var p2 = {x:Math.floor(controlPoint.x), y:Math.floor(controlPoint.y)};
		controlPoint = cps[cps.length-1];
		var p3 = {x:Math.floor(controlPoint.x), y:Math.floor(controlPoint.y)};
		*/
	}

	//this.overlayTexture.image = ctx.getImageData(0, 0, tmpCanvas.width, tmpCanvas.height);
	this.overlayTexture.needsUpdate = true;
	////////
};

RUINS.MapRenderer.prototype.newIcon = function(texture, position) {
	var icon = new THREE.Sprite({
		map: texture,
		useScreenCoordinates: false,
		affectedByDistance: true,
		scaleByViewport: true,
		mergeWith3D: false,
		color: 0xffffff,
	});

	icon.position = position;
	
	var zoom = 1.0 - this.controls.zoom;
	icon.scale = new THREE.Vector3(zoom, zoom, zoom);

	return icon;
};