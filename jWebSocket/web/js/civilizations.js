RUINS.Civilizations = function(civilizationsTemplates) {
	this.civilizationsTemplates = civilizationsTemplates;
	
	this.existingCivilizations = {};
};

RUINS.Civilizations.prototype.addNewCivilization = function(civilization) {
	this.existingCivilizations[civilization.id] = civilization;
};

RUINS.Civilizations.prototype.getCivilization = function(id) {
	return this.existingCivilizations[id];
};