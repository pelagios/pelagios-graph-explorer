Pelagios.Help = { }

Pelagios.Help.TEXT_SEARCHFORM =
	  "Start typing to search the Pleiades directory ancient places.";

Pelagios.Help.TEXT_SEARCHLIST =
	  "Displays the list of Places currently being searched. "
	+ "Click the 'CLEAR' button below to remove all places.";

Pelagios.Help.TEXT_DATAPANEL =
	  "Lists the references to the place in the selected data set. "
	+ "Clicking on a reference will take you to the original data "
	+ "provider's page.";
	
Pelagios.Help.init = function() {
	var attach = function(id, helpBubble) {
		$('#' + id).mouseover(function() {
			helpBubble.show();
		});
		
		$('#' + id).mouseout(function() {
			helpBubble.hide();
		});
	}
	
	attach('search-form', new Pelagios.HelpBubble(Pelagios.Help.TEXT_SEARCHFORM, 255, 215)); 
    attach('search-list', new Pelagios.HelpBubble(Pelagios.Help.TEXT_SEARCHLIST, 255, 300));
    attach('data-panel', new Pelagios.HelpBubble(Pelagios.Help.TEXT_DATAPANEL, 255, 500));
}

Pelagios.HelpBubble = function(text, x, y) {
	this.container = document.createElement("div");
	this.container.setAttribute("class", "pelagios-help-bubble");
	this.container.style.position = "absolute";
	this.container.style.top = y;
	this.container.style.left = x;
	
	var box = document.createElement("div");
	box.setAttribute("class", "pelagios-help-bubble-inner");
	box.innerHTML = text;
	box.style.marginLeft = "12px";
	box.style.width = "220px";
	this.container.appendChild(box);
	
	this.hide();
	document.body.appendChild(this.container);
}

Pelagios.HelpBubble.prototype.show = function() {
	this.container.style.visibility = 'visible';
}

Pelagios.HelpBubble.prototype.hide = function() {
	this.container.style.visibility = 'hidden';
}