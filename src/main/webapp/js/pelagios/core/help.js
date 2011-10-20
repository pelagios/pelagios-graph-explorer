Pelagios.Help = { }

Pelagios.Help.TEXT_SEARCHFORM =
	"The search form...";

Pelagios.Help.TEXT_SEARCHLIST =
	  "The search list displays the Places currently in the graph. Use the 'CLEAR'"
	+ "button to remove all search results. There will be an option to remove single"
	+ "results later down the road.";

Pelagios.Help.TEXT_DATAPANEL =
	"The data panel...";
	
Pelagios.Help.init = function() {
	var attach = function(id, helpBubble) {
		$('#' + id).mouseover(function() {
			helpBubble.show();
		});
		
		$('#' + id).mouseout(function() {
			helpBubble.hide();
		});
	}
	
	attach('search-form', new Pelagios.HelpBubble(Pelagios.Help.TEXT_SEARCHFORM, 275, 185)); 
    attach('search-list', new Pelagios.HelpBubble(Pelagios.Help.TEXT_SEARCHLIST, 275, 247));
    attach('data-panel', new Pelagios.HelpBubble(Pelagios.Help.TEXT_DATAPANEL, 275, 440));
}

Pelagios.HelpBubble = function(text, x, y) {
	this.container = document.createElement("div");
	this.container.style.position = "absolute";
	this.container.style.top = y;
	this.container.style.left = x;
	this.container.style.backgroundColor = "#ff0000";
	
	var box = document.createElement("div");
	box.setAttribute("class", "pelagios-help-bubble");
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