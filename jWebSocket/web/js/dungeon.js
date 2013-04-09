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

	this.levelsTexture = [];

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
	this.levelsTexture[0] = THREE.ImageUtils.loadTexture(texturePath + '/000.png');
	this.levelsTexture[0].magFilter = THREE.NearestFilter;
	this.levelsTexture[0].minFilter = THREE.NearestFilter;
	this.levelsTexture[0].flipY = false;
	
	//dungeon shader material
	this.dungeonMaterial = new THREE.ShaderMaterial({
    	uniforms: {
    		tilesTexture: { type: 't', value: this.tilesTexture },
    		levelTexture: { type: 't', value: this.levelsTexture[0] },
    		viewOffset: { type: 'v2', value: new THREE.Vector2( 0, 0 ) },
    		viewportSize: { type: 'v2', value: new THREE.Vector2( this.width, this.height ) },
    		TileSize: { type: 'f', value: this.tileSize },
    		inverseTileSize: { type: 'f', value: 1/this.tileSize },
    		inverseTilesTextureSize: {type: 'v2', value: new THREE.Vector2(this.tilesTexture.image.width, this.tilesTexture.image.height) },
    		inverseDungeonTextureSize: {type: 'v2', value: new THREE.Vector2(this.levelsTexture[0].image.width, this.levelsTexture[0].image.height) },
    	},
    	vertexShader: RUINS.SHADERS['dungeon'].vertexShader,
    	fragmentShader: RUINS.SHADERS['dungeon'].fragmentShader,
    	wireframe: false,
    	side: THREE.BackSide,
    });
	
	this.mapBaseMaterial = new THREE.MeshBasicMaterial({ map: this.levelsTexture[0], wireframe: true, side: THREE.BackSide});
	
	//create dungeon geometry
	//this.dungeonPlane = new THREE.Mesh(new THREE.PlaneGeometry(this.dungeon.size * this.tileSize, this.dungeon.size * this.tileSize, this.dungeon.size/this.tileSize, this.dungeon.size/this.tileSize), this.dungeonMaterial);
	this.dungeonPlane = new THREE.Mesh(new THREE.PlaneGeometry(this.dungeon.size, this.dungeon.size, this.dungeon.size/32, this.dungeon.size/32), this.dungeonMaterial); //TODO set correct plane division
	this.dungeonPlane.position.x = this.dungeon.size/2;
	this.dungeonPlane.position.y = this.dungeon.size/2;
	//this.mapPlane.position.z = 0.2;
	this.dungeonPlane.updateMatrix();
	this.dungeonPlane.matrixAutoUpdate = false; //no need to transform plane ever again
	this.scene.add(this.dungeonPlane);

	this.scene.add(new THREE.AxisHelper); //TEMP
};

RUINS.DungeonRenderer.prototype.render = function(clockDelta) {
	this.renderer.render(this.scene, this.camera);
};