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

UTILS.hexToRgb = function(hex) {
    var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16),
        a: 255,
    } : null;
};
