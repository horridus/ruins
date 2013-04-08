var WIDGETS = {}

WIDGETS.createButton = function(value, id, width) {
	var button = $(document.createElement('input'));
	button.attr('type', 'submit');
	button.attr('class', 'button');

	button.attr('value', value);
	if (id)
		button.attr('id', id);
	if (width)
		button.css('width', width);
	
	return button;
};

WIDGETS.createDate = function(day, month, year, season) {
	var date = $(document.createElement('span'));
	date.attr('class', 'date');
	date.text(RUINS.UTILS.padWZeroes(day.toString(), 2) + " / " + RUINS.UTILS.padWZeroes(month.toString(), 2) + " / " + RUINS.UTILS.padWZeroes(year.toString(), 4));
	
	var seasonIcon = '';
	switch(season) {
	case 0:
		seasonIcon = 'winter.png';
		break;
	case 1:
		seasonIcon = 'spring.png';
		break;
	case 2:
		seasonIcon = 'summer.png';
		break;
	case 3:
		seasonIcon = 'autumn.png';
		break;
	}
	
	var seasonImg = $(document.createElement('img'));
	seasonImg.attr('src', main.configuration.imgs_location + '/icons/ui/' + seasonIcon);
	seasonImg.attr('class', 'dateIcon');
	
	var container = $(document.createElement('div'));
	container.append(date);
	container.append(seasonImg);
	container.attr('class', 'dateBox');
	
	return container;
};