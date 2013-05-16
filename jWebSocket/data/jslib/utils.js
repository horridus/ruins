UTILS = {};

UTILS.shuffle = function(array) {
  var m = array.length, t, i;

  // While there remain elements to shuffle…
  while (m) {
	  //Pick a remaining element…
	  i = Math.floor(Math.random() * m--);

	  //And swap it with the current element.
	  t = array[m];
	  array[m] = array[i];
	  array[i] = t;
  }

  return array;
};

UTILS.padWZeroes = function(str, max) {
	return str.length < max ? RUINS.UTILS.padWZeroes("0" + str, max) : str;
};

UTILS.line = function(x0, y0, x1, y1) {
	var xa = x0;
	var ya = y0;
	var xb = x1;
	var yb = y1;
	
	var coordinatesArray = new Array();
	// Define differences and error check
	var dx = Math.abs(xb - xa);
	var dy = Math.abs(yb - ya);
	var sx = (xa < xb) ? 1 : -1;
	var sy = (ya < yb) ? 1 : -1;
	var err = dx - dy;
	
	// Set first coordinates
	coordinatesArray.push({x:xa, y:ya});
	
	// Main loop
	while (!((xa == xb) && (ya == yb))) {
		var e2 = err << 1;
		if (e2 > -dy) {
		  err -= dy;
		  xa += sx;
		}
		if (e2 < dx) {
		  err += dx;
		  ya += sy;
		}
		// Set coordinates
		coordinatesArray.push({x:xa, y:ya});
	}
	// Return the result
	return coordinatesArray;
};

UTILS.log = function(msg) {
	java.lang.System.out.println(msg);
};

UTILS.test = function() {
	java.lang.System.out.println('utils library loaded!');
};

(function() {
	return UTILS;
}());
