THREE.MapControls = function ( object, domElement, map,dungeonpRenderer, boundingBox) {
	var that = this;

	this.object = object;
	this.map = map;
	this.mapRenderer = mapRenderer;
	this.domElement = ( domElement !== undefined ) ? $(domElement) : $(document);
	this.canvas = domElement.find('canvas');
	this.canvasWidth = this.canvas.width();
	this.canvasHeight = this.canvas.height();

	this.target = new THREE.Vector3( 0, 0, 0 );
	
	this.boundingBox = (boundingBox !== undefined) ? boundingBox : { min: new THREE.Vector3(), max: new THREE.Vector3() };

	this.movementSpeed = 1.0;
	this.zoomSpeed = 0.01;
	this.zoomMin = 1.0;
	this.zoomMax = 0.01;
	this.zoom = 0.5;

	this.moveForward = false;
	this.moveBackward = false;
	this.moveLeft = false;
	this.moveRight = false;
	this.moveUpward = false;
	this.moveDownward = false;

	this.width = (this.object.right - this.object.left);
	this.height = (this.object.bottom - this.object.top);
	this.halfWidth = this.width/2.0;
	this.halfHeight = this.height/2.0;

	this.centerx = this.object.left + this.halfWidth;
	this.centery = this.object.top + this.halfHeight;

	this.projector = new THREE.Projector();
	this.plane = map.mapPlane;

	this.freeze = false;

	//mouse variables (for picking, selecting regions, etc.)
	this.mouseCanvasXCoord = 0;
	this.mouseCanvasYCoord = 0;
	this.mousemoveTimeout = null;
	this.selectedIcon = 0;
	this.selectedIconType = 0;
	this.mouseWheelDelta = 0;
	this.mouseWheelAcceleration = 10;

	this.onKeyDownHandler = function ( event ) {

		switch( event.keyCode ) {

			case 38: /*up*/
			case 87: /*W*/ event.data.moveForward = true; break;

			case 37: /*left*/
			case 65: /*A*/ event.data.moveLeft = true; break;

			case 40: /*down*/
			case 83: /*S*/ event.data.moveBackward = true; break;

			case 39: /*right*/
			case 68: /*D*/ event.data.moveRight = true; break;

			case 82: /*R*/ event.data.moveUpward = true; break;
			case 70: /*F*/ event.data.moveDownward = true; break;

			case 88: /*x*/ event.data.debug = true; break;
		}

	};

	this.onKeyUpHandler = function ( event ) {

		switch( event.keyCode ) {

			case 38: /*up*/
			case 87: /*W*/ event.data.moveForward = false; break;

			case 37: /*left*/
			case 65: /*A*/ event.data.moveLeft = false; break;

			case 40: /*down*/
			case 83: /*S*/ event.data.moveBackward = false; break;

			case 39: /*right*/
			case 68: /*D*/ event.data.moveRight = false; break;

			case 82: /*R*/ event.data.moveUpward = false; break;
			case 70: /*F*/ event.data.moveDownward = false; break;

			case 88: /*x*/ event.data.debug = false; break;
		}

	};
	
	this.update = function( delta ) {
		var actualMoveSpeed = 0;
		var actualZoomSpeed = 0;

		if ( this.freeze ) {

			return;

		} else {
			actualMoveSpeed = delta * this.movementSpeed * this.zoom;
			actualZoomSpeed = delta * this.zoomSpeed;

			var minx = this.boundingBox.min.x;
			var miny = this.boundingBox.min.y;

			var maxx = this.boundingBox.max.x;
			var maxy = this.boundingBox.max.y;

			var hwz = this.halfWidth * this.zoom;
			var hhz = this.halfHeight * this.zoom;

			if ( this.debug ) {
				this.debugFunc();
			}


			if ( this.moveForward ) {
				this.object.translateY( -actualMoveSpeed );
				this.object.position.y = ((this.object.position.y - hhz) < miny)? miny + hhz : this.object.position.y;
			}
			if ( this.moveBackward ) {
				this.object.translateY( actualMoveSpeed );
				this.object.position.y = ((this.object.position.y + hhz) > maxy)? maxy - hhz : this.object.position.y;
			}

			if ( this.moveLeft ) {
				this.object.translateX( -actualMoveSpeed );
				this.object.position.x = ((this.object.position.x - hwz) < minx)? minx  + hwz : this.object.position.x;
			}
			if ( this.moveRight ) {
				this.object.translateX( actualMoveSpeed );
				this.object.position.x = ((this.object.position.x + hwz) > maxx)? maxx - hwz : this.object.position.x;
			}

			if ( this.moveUpward ) {
				this.zoom += actualZoomSpeed;
				this.zoom = (this.zoom > this.zoomMin)? this.zoomMin : this.zoom;

				this.object.position.y = ((this.object.position.y - hhz) < miny)? miny + hhz : this.object.position.y;
				this.object.position.y = ((this.object.position.y + hhz) > maxy)? maxy - hhz : this.object.position.y;
				this.object.position.x = ((this.object.position.x - hwz) < minx)? minx + hwz : this.object.position.x;
				this.object.position.x = ((this.object.position.x + hwz) > maxx)? maxx - hwz : this.object.position.x;
			}
			if ( this.moveDownward ) {
				this.zoom -= actualZoomSpeed;
				this.zoom = (this.zoom < this.zoomMax)? this.zoomMax : this.zoom;
			}

			if ( this.mouseWheelDelta < 0 ) {
				this.zoom += actualZoomSpeed * this.mouseWheelAcceleration;
				this.zoom = (this.zoom > this.zoomMin)? this.zoomMin : this.zoom;

				this.object.position.y = ((this.object.position.y - hhz) < miny)? miny + hhz : this.object.position.y;
				this.object.position.y = ((this.object.position.y + hhz) > maxy)? maxy - hhz : this.object.position.y;
				this.object.position.x = ((this.object.position.x - hwz) < minx)? minx + hwz : this.object.position.x;
				this.object.position.x = ((this.object.position.x + hwz) > maxx)? maxx - hwz : this.object.position.x;

				this.mouseWheelDelta = 0;
			}
			else if ( this.mouseWheelDelta > 0 ) {
				this.zoom -= actualZoomSpeed * this.mouseWheelAcceleration;
				this.zoom = (this.zoom < this.zoomMax)? this.zoomMax : this.zoom;

				this.mouseWheelDelta = 0;
			}

			this.object.projectionMatrix.makeOrthographic( this.centerx - hwz, this.centerx + hwz, this.centery - hhz, this.centery + hhz, this.object.near, this.object.far );
		}
	};

	this.setBoundingBox = function(boundingBox) {
		this.boundingBox = boundingBox;
	};

	this.getRealCoords = function(mx, my) {
		//get normalized device coordinates
		var x = (mx/this.canvasWidth) * 2.0 - 1.0;
		var y = 1.0 - (my/this.canvasHeight) * 2.0; //y is reversed!

		return this.projector.unprojectVector(new THREE.Vector3(x, y, 0.0), this.object);
	};

	this.onMouseMoveHandler = function(event) {
		var that = event.data;

		that.mouseCanvasXCoord = event.pageX - Math.floor($(this.canvas).offset().left);
		that.mouseCanvasYCoord = event.pageY - Math.floor($(this.canvas).offset().top);

		var coords = that.getRealCoords(that.mouseCanvasXCoord, that.mouseCanvasYCoord);

		var rindex = that.map.rindex(Math.floor(coords.x), Math.floor(coords.y));
		mainui.showRegionInfo(rindex, coords.x, coords.y);

		//show selected location info
		if (that.selectedIconType != 0) {
			mainui.showLocationInfo(that.selectedIconType, that.selectedIcon);
		}

		event.preventDefault();
	};
	
	this.onMouseDownHandler = function( event ) {
		
	};
	
	this.onMouseUpHandler = function( event ) {
		
	};
	
	this.onDblClickHandler = function( event ) {
		var minx = this.boundingBox.min.x;
		var miny = this.boundingBox.min.y;

		var maxx = this.boundingBox.max.x;
		var maxy = this.boundingBox.max.y;

		var hwz = this.halfWidth * this.zoom;
		var hhz = this.halfHeight * this.zoom;
		
		var coords = this.getRealCoords(that.mouseCanvasXCoord, that.mouseCanvasYCoord);
		this.object.position.x = coords.x;
		this.object.position.y = coords.y;
		
		this.object.position.y = ((this.object.position.y - hhz) < miny)? miny + hhz : this.object.position.y;
		this.object.position.y = ((this.object.position.y + hhz) > maxy)? maxy - hhz : this.object.position.y;
		this.object.position.x = ((this.object.position.x - hwz) < minx)? minx + hwz : this.object.position.x;
		this.object.position.x = ((this.object.position.x + hwz) > maxx)? maxx - hwz : this.object.position.x;
	};

	this.domElement.on('contextmenu', null, null, function ( event ) { event.preventDefault(); });

	//BUGFIX: without this, key events won't be fired in canvas
	//The root problem is that by default the browser doesn't make the canvas "focusable". The best workaround I could come up with is to set the tabindex on the canvas
	this.domElement.attr('tabindex', '1');

	//keybord handling
	this.domElement.on('keydown', null, this, this.onKeyDownHandler);
	this.domElement.on('keyup', null, this, this.onKeyUpHandler);

	//mouse handling
	this.canvas.on('mousemove', null, this, function(event) {
		event.data.onMouseMoveHandler(event);
		//FIXME
		/*
		if (event.data.mousemoveTimeout == null) {
			var e = event;
			event.data.mousemoveTimeout = window.setTimeout(function() { e.data.mouseMoveHandler(e); window.clearTimeout(e.data.mousemoveTimeout); e.data.mousemoveTimeout = null; }, 250);
		}
		*/
	});
	
	this.canvas.on('mousedown', null, this, function(event) {
		event.data.onMouseDownHandler(event);
	});
	
	this.canvas.on('mouseup', null, this, function(event) {
		event.data.onMouseUpHandler(event);
	});
	
	this.canvas.on('dblclick', null, this, function(event) {
		event.data.onDblClickHandler(event);
	});
	
	//mouse wheel handling
	this.domElement.on('mousewheel', null, this, function(event, delta) {
		event.data.mouseWheelDelta = delta;
		event.preventDefault();
	});

	//droppable handling
	this.domElement.droppable({
		drop: function(event, ui) {
			var x = event.pageX - Math.floor($(this).offset().left);
			var y = event.pageY - Math.floor($(this).offset().top);

			var coords = that.getRealCoords(x, y);
			var id = ui.draggable[0].id;

			if(id.match("^civtemplate_")) {
				var rindex = that.map.rindex(Math.floor(coords.x), Math.floor(coords.y));
				mainui.addNewCivilization(Number(id.charAt(id.length - 1)), rindex, Math.floor(coords.x), Math.floor(coords.y));
			}
		},
	});
};

THREE.DungeonControls = function(object, domElement, dungeon, dungeonRenderer, boundingBox) {
	var that = this;

	this.object = object;
	this.dungeon = dungeon;
	this.dungeonRenderer = dungeonRenderer;
	this.domElement = ( domElement !== undefined ) ? $(domElement) : $(document);
	this.canvas = domElement.find('canvas');
	this.canvasWidth = this.canvas.width();
	this.canvasHeight = this.canvas.height();

	this.target = new THREE.Vector3( 0, 0, 0 );
	this.viewOffset = new THREE.Vector2( 0, 0 );

	this.boundingBox = (boundingBox !== undefined) ? boundingBox : { min: new THREE.Vector3(), max: new THREE.Vector3() };

	this.movementSpeed = 1.0;
	this.zoomSpeed = 0.1;
	this.zoomMin = 1.0;
	this.zoomMax = 0.01;
	this.zoom = 0.5;

	this.moveForward = false;
	this.moveBackward = false;
	this.moveLeft = false;
	this.moveRight = false;
	this.moveUpward = false;
	this.moveDownward = false;

	this.width = (this.object.right - this.object.left);
	this.height = (this.object.bottom - this.object.top);
	this.halfWidth = this.width/2.0;
	this.halfHeight = this.height/2.0;

	this.centerx = this.object.left + this.halfWidth;
	this.centery = this.object.top + this.halfHeight;

	this.projector = new THREE.Projector();
	this.plane = dungeon.dungeonPlane;

	this.freeze = false;

	//mouse variables (for picking, selecting regions, etc.)
	this.mouseCanvasXCoord = 0;
	this.mouseCanvasYCoord = 0;
	this.mousemoveTimeout = null;
	this.selectedIcon = 0;
	this.selectedIconType = 0;
	this.mouseWheelDelta = 0;
	this.mouseWheelAcceleration = 10;
	
	this.setViewOffset = function(vec2ViewOffset) {
		this.viewOffset.x = vec2ViewOffset.x;
		this.viewOffset.y = vec2ViewOffset.y;
	};

	this.onKeyDownHandler = function ( event ) {

		switch( event.keyCode ) {

			case 38: /*up*/
			case 87: /*W*/ event.data.moveForward = true; break;

			case 37: /*left*/
			case 65: /*A*/ event.data.moveLeft = true; break;

			case 40: /*down*/
			case 83: /*S*/ event.data.moveBackward = true; break;

			case 39: /*right*/
			case 68: /*D*/ event.data.moveRight = true; break;

			case 82: /*R*/ event.data.moveUpward = true; break;
			case 70: /*F*/ event.data.moveDownward = true; break;

			case 88: /*x*/ event.data.debug = true; break;
		}

	};

	this.onKeyUpHandler = function ( event ) {

		switch( event.keyCode ) {

			case 38: /*up*/
			case 87: /*W*/ event.data.moveForward = false; break;

			case 37: /*left*/
			case 65: /*A*/ event.data.moveLeft = false; break;

			case 40: /*down*/
			case 83: /*S*/ event.data.moveBackward = false; break;

			case 39: /*right*/
			case 68: /*D*/ event.data.moveRight = false; break;

			case 82: /*R*/ event.data.moveUpward = false; break;
			case 70: /*F*/ event.data.moveDownward = false; break;

			case 88: /*x*/ event.data.debug = false; break;
		}

	};
	
	this.update = function( delta ) {
		var actualMoveSpeed = 0;
		var actualZoomSpeed = 0;

		if ( this.freeze ) {

			return;

		} else {
			actualMoveSpeed = delta * this.movementSpeed * this.zoom;
			actualZoomSpeed = delta * this.zoomSpeed;

			var minx = this.boundingBox.min.x;
			var miny = this.boundingBox.min.y;

			var maxx = this.boundingBox.max.x;
			var maxy = this.boundingBox.max.y;

			if ( this.moveForward ) {
				this.viewOffset.y -= actualMoveSpeed;
				this.viewOffset.y = (this.viewOffset.y < miny)? miny : this.viewOffset.y;
			}
			if ( this.moveBackward ) {
				this.viewOffset.y += actualMoveSpeed;
				this.viewOffset.y = (this.viewOffset.y + this.height > maxy)? maxy : this.viewOffset.y;
			}

			if ( this.moveLeft ) {
				this.viewOffset.x -= actualMoveSpeed;
				this.viewOffset.x = (this.viewOffset.x < minx)? minx : this.viewOffset.x;
			}
			if ( this.moveRight ) {
				this.viewOffset.x += actualMoveSpeed;
				this.viewOffset.x = (this.viewOffset.x + this.width > maxx)? maxx : this.viewOffset.x;
			}

			if ( this.moveUpward ) {
				
				this.zoom += actualZoomSpeed;
				this.zoom = (this.zoom > this.zoomMin)? this.zoomMin : this.zoom;
			}
			if ( this.moveDownward ) {
				this.zoom -= actualZoomSpeed;
				this.zoom = (this.zoom < this.zoomMax)? this.zoomMax : this.zoom;
			}

			if ( this.mouseWheelDelta < 0 ) {
				this.zoom += actualZoomSpeed * this.mouseWheelAcceleration;
				this.zoom = (this.zoom > this.zoomMin)? this.zoomMin : this.zoom;

				this.mouseWheelDelta = 0;
			}
			else if ( this.mouseWheelDelta > 0 ) {
				this.zoom -= actualZoomSpeed * this.mouseWheelAcceleration;
				this.zoom = (this.zoom < this.zoomMax)? this.zoomMax : this.zoom;

				this.mouseWheelDelta = 0;
			}
			
			this.dungeonRenderer.setViewOffset(this.viewOffset.x, this.viewOffset.y);
			this.dungeonRenderer.setViewScale(this.zoom);
			
		}
	};

	this.setBoundingBox = function(boundingBox) {
		this.boundingBox = boundingBox;
	};

	this.onMouseMoveHandler = function(event) {
		var that = event.data;

		that.mouseCanvasXCoord = event.pageX - Math.floor($(this.canvas).offset().left);
		that.mouseCanvasYCoord = event.pageY - Math.floor($(this.canvas).offset().top);


		event.preventDefault();
	};
	
	this.onMouseDownHandler = function( event ) {
		
	};
	
	this.onMouseUpHandler = function( event ) {
		
	};
	
	this.onDblClickHandler = function( event ) {

	};

	this.domElement.on('contextmenu', null, null, function ( event ) { event.preventDefault(); });

	//BUGFIX: without this, key events won't be fired in canvas
	//The root problem is that by default the browser doesn't make the canvas "focusable". The best workaround I could come up with is to set the tabindex on the canvas
	this.domElement.attr('tabindex', '1');

	//keybord handling
	this.domElement.on('keydown', null, this, this.onKeyDownHandler);
	this.domElement.on('keyup', null, this, this.onKeyUpHandler);

	//mouse handling
	this.canvas.on('mousemove', null, this, function(event) {
		event.data.onMouseMoveHandler(event);
	});
	
	this.canvas.on('mousedown', null, this, function(event) {
		event.data.onMouseDownHandler(event);
	});
	
	this.canvas.on('mouseup', null, this, function(event) {
		event.data.onMouseUpHandler(event);
	});
	
	this.canvas.on('dblclick', null, this, function(event) {
		event.data.onDblClickHandler(event);
	});
	
	//mouse wheel handling
	this.domElement.on('mousewheel', null, this, function(event, delta) {
		event.data.mouseWheelDelta = delta;
		event.preventDefault();
	});
};