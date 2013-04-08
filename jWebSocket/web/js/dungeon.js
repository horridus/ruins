RUINS.Dungeon = function(id, name, size, data) {
	this.id = id;
	this.name = name;
	this.size = size;
};

/*---------------------------------------------*/
RUINS.DungeonRenderer = function(dungeon, width, height, texturePath, container) {
	this.dungeon = dungeon;
	this.width = width;
	this.height = height;

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
	this.levelsTexture[0] = THREE.ImageUtils.loadTexture(texturePath + '/tiles.png');
	this.levelsTexture[0].magFilter = THREE.NearestFilter;
	this.levelsTexture[0].minFilter = THREE.NearestFilter;
	this.levelsTexture[0].flipY = false;
	
	//dungeon shader material
	this.dungeonMaterial = new THREE.ShaderMaterial({
    	uniforms: {
    		tilesTexture: { type: 't', value: this.tilesTexture },
    		levelTexture: { type: 't', value: this.levelsTexture[0] },
    	},
    	vertexShader: RUINS.SHADERS['dungeon'].vertexShader,
    	fragmentShader: RUINS.SHADERS['dungeon'].fragmentShader,
    	side: THREE.BackSide,
    });
	
	//create dungeon geometry
	this.dungeonPlane = new THREE.Mesh(new THREE.PlaneGeometry(this.dungeon.size, this.dungeon.size, this.dungeon.size/100, this.dungeon.size/100), this.dungeonMaterial);
	this.dungeonPlane.position.x = this.dungeon.size/2;
	this.dungeonPlane.position.y = this.dungeon.size/2;
	//this.mapPlane.position.z = 0.2;
	this.dungeonPlane.updateMatrix();
	this.dungeonPlane.matrixAutoUpdate = false; //no need to transform plane ever again
	this.scene.add(this.dungeonPlane);

	this.scene.add(new THREE.AxisHelper); //TEMP
};
