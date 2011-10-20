Pelagios.Help = { }

Pelagios.Help.TEXT_SEARCHFORM =
	"The search form...";

Pelagios.Help.TEXT_SEARCHLIST =
	"The search list...";

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
	this.div = document.createElement("div");
	this.div.innerHTML = text;
	this.div.setAttribute("class", "pelagios-helpbubble");
	this.div.style.position = "absolute";
	this.div.style.top = y;
	this.div.style.left = x;
	this.hide();
	document.body.appendChild(this.div);
}

Pelagios.HelpBubble.prototype.show = function() {
	this.div.style.visibility = 'visible';
}

Pelagios.HelpBubble.prototype.hide = function() {
	this.div.style.visibility = 'hidden';
}