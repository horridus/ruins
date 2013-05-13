RUINS.Dungeon = function(id, name, size, data) {
	this.id = id;
	this.name = name;
	this.size = size;
};

/*---------------------------------------------*/
RUINS.DungeonRenderer = function(dungeon, width, height, texturePath, container, tileSize) {
	this.dungeon = dungeon;
	this.width = width;
	this.height = height;
	this.tileSize = tileSize;
	this.texturePath = texturePath;
	
	this.viewOffset = new THREE.Vector2( 0, 0 );
	this.scale = 1.0;

	this.levelsTexture = [];
	this.currentLevel = 0;

	this.renderer = new THREE.WebGLRenderer();
	this.renderer.setSize(width, height);
	container.append(this.renderer.domElement);

	this.scene = new THREE.Scene();

	this.camera = new THREE.OrthographicCamera(-width/2.0, width/2.0, -height/2.0, height/2.0, 0.1, 100);

	this.camera.position.x = this.dungeon.size/2.0;
	this.camera.position.y = this.dungeon.size/2.0;
	this.camera.position.z = 1.0;
	this.camera.lookAt(new THREE.Vector3(this.dungeon.size/2.0, this.dungeon.size/2.0, 0));
	this.scene.add(this.camera);
	
	//dungeon's tiles textures
	this.tilesTexture = THREE.ImageUtils.loadTexture(main.configuration.imgs_location + '/dungeons/tiles.png');
	this.tilesTexture.magFilter = THREE.NearestFilter;
	this.tilesTexture.minFilter = THREE.NearestFilter;
	this.tilesTexture.flipY = false;
	
	//dungeon's level 0 texture
	this.levelsTexture[this.currentLevel] = THREE.ImageUtils.loadTexture(this.texturePath + '/000.png');
	this.levelsTexture[this.currentLevel].magFilter = THREE.NearestFilter;
	this.levelsTexture[this.currentLevel].minFilter = THREE.NearestFilter;
	this.levelsTexture[this.currentLevel].flipY = false;
	
	//dungeon shader material
	this.dungeonMaterial = new THREE.ShaderMaterial({
    	uniforms: {
    		tilesTexture: { type: 't', value: this.tilesTexture },
    		levelTexture: { type: 't', value: this.levelsTexture[this.currentLevel] },
    		viewOffset: { type: 'v2', value:  this.viewOffset },
    		viewportSize: { type: 'v2', value: new THREE.Vector2( this.width/this.scale, this.height/this.scale ) },
    		tileSize: { type: 'f', value: this.tileSize },
    		inverseTileSize: { type: 'f', value: 1.0/this.tileSize },
    		inverseTilesTextureSize: { type: 'v2', value: new THREE.Vector2(1.0/this.tilesTexture.image.width, 1.0/this.tilesTexture.image.height) },
    		inverseDungeonTextureSize: { type: 'v2', value: new THREE.Vector2(1.0/this.dungeon.size, 1.0/this.dungeon.size) },
    		gridColor: { type: 'v4', value: new THREE.Vector4(0.2, 0.2, 0.2, 1.0) },
    		scale: { type: 'f', value: this.scale },
    	},
    	vertexShader: RUINS.SHADERS['dungeon'].vertexShader,
    	fragmentShader: RUINS.SHADERS['dungeon'].fragmentShader,
    	wireframe: false,
    	side: THREE.BackSide,
    });
	
	this.mapBaseMaterial = new THREE.MeshBasicMaterial({ map: this.levelsTexture[0], wireframe: true, side: THREE.BackSide});
	
	//create dungeon geometry
	this.dungeonPlane = new THREE.Mesh(new THREE.PlaneGeometry(this.width, this.height, 100, 100), this.dungeonMaterial);
	this.dungeonPlane.position.x = this.dungeon.size/2;
	this.dungeonPlane.position.y = this.dungeon.size/2;
	//this.mapPlane.position.z = 0.2;
	this.dungeonPlane.updateMatrix();
	this.dungeonPlane.matrixAutoUpdate = false; //no need to transform plane ever again
	this.scene.add(this.dungeonPlane);

	this.scene.add(new THREE.AxisHelper); //TEMP
	
	//add map controls to scene
	this.controls = new THREE.DungeonControls(this.camera, container, this.dungeon, this);
	this.controls.movementSpeed = 200.0;
	this.controls.setBoundingBox({min: new THREE.Vector3(0, 0, 0), max: new THREE.Vector3(this.dungeon.size * this.tileSize, this.dungeon.size * this.tileSize, this.dungeon.size * this.tileSize)});
	
	
	//TEMP/////////////////////
	var texture = THREE.ImageUtils.loadTexture(main.configuration.imgs_location + '/dngn_closed_door.png');
	this.icon = new THREE.Sprite({
		map: texture,
		useScreenCoordinates: true,
		affectedByDistance: false,
		scaleByViewport: true,
		mergeWith3D: false,
		color: 0xffffff,
		alignment: THREE.SpriteAlignment.topLeft,
	});

	this.icon.position = new THREE.Vector3((5 * (this.tileSize * this.scale)) - (this.viewOffset.x), (10 * (this.tileSize * this.scale)) - (this.viewOffset.y), 0.5);
	this.icon.scale = new THREE.Vector3(this.scale, this.scale, this.scale);
	this.scene.add(this.icon);
	///////////////////////////
};

RUINS.DungeonRenderer.prototype.render = function(clockDelta) {
	this.renderer.render(this.scene, this.camera);
};

RUINS.DungeonRenderer.prototype.update = function(clockDelta) {
	this.controls.update(clockDelta);
};

RUINS.DungeonRenderer.prototype.setViewOffset = function(offsetx, offsety) {
	this.viewOffset.x = offsetx;
	this.viewOffset.y = offsety;
	
	this.dungeonMaterial.uniforms.viewOffset.value = this.viewOffset;
	this.dungeonMaterial.needsUpdate = true;
	
	//TEMP/////////////////////
	this.icon.position = new THREE.Vector3((5 * (this.tileSize * this.scale)) - (this.viewOffset.x), (10 * (this.tileSize * this.scale)) - (this.viewOffset.y), 0.5);
	this.icon.scale = new THREE.Vector3(this.scale, this.scale, this.scale);
	///////////////////////////
};

RUINS.DungeonRenderer.prototype.setViewScale = function(scale) {
	this.scale = scale;
	
	this.dungeonMaterial.uniforms.viewportSize.value = new THREE.Vector2( this.width/this.scale, this.height/this.scale );
	this.dungeonMaterial.uniforms.scale.value = this.scale;
	this.dungeonMaterial.needsUpdate = true;
};

RUINS.DungeonRenderer.prototype.showShallowerLevel = function() {
	(this.currentLevel > 0)? this.currentLevel-- : this.currentLevel; 
	this.showLevel(this.currentLevel);
};

RUINS.DungeonRenderer.prototype.showDeeperLevel = function() {
	//(this.currentLevel < )? this.currentLevel-- : this.currentLevel;
	//FIXME
	this.currentLevel++;
	this.showLevel(this.currentLevel);
};

RUINS.DungeonRenderer.prototype.showLevel = function(depth) {
	//dungeon's level texture
	if (this.levelsTexture[depth] == undefined) {
		this.levelsTexture[depth] = THREE.ImageUtils.loadTexture(this.texturePath + '/' + RUINS.UTILS.padWZeroes('' + depth, 3) + '.png');
		this.levelsTexture[depth].magFilter = THREE.NearestFilter;
		this.levelsTexture[depth].minFilter = THREE.NearestFilter;
		this.levelsTexture[depth].flipY = false;
	}
	
	this.dungeonMaterial.uniforms['levelTexture'].value = this.levelsTexture[depth]; 
};