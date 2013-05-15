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

UTILS.test = function() {
	java.lang.System.out.println('utils library loaded!');
};

(function() {
	return UTILS;
}());
