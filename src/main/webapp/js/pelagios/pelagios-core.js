// Core functions of the Pelagios Visualization demo

// Namespace declarations
Pelagios = {};

Pelagios.getViewport = function() {
	 var w, h;
	 
	 if (typeof window.innerWidth != 'undefined') {
	      w = window.innerWidth;
	      h = window.innerHeight;
	 } else if (typeof document.documentElement != 'undefined'
	     && typeof document.documentElement.clientWidth !=
	     'undefined' && document.documentElement.clientWidth != 0) {
	       w = document.documentElement.clientWidth;
	       h = document.documentElement.clientHeight;
	 } else {
	       w = document.getElementsByTagName('body')[0].clientWidth;
	       h = document.getElementsByTagName('body')[0].clientHeight;
	 }
	 
	 return new Vector(w, h);
}

Pelagios.Tooltip = function(text, x, y) {
	this.div = document.createElement("div");
	this.div.innerHTML = text;
	this.div.setAttribute("class", "pelagios-tooltip");
	this.div.style.position = "absolute";
	this.hide();
	document.body.appendChild(this.div);
}

Pelagios.Tooltip.prototype.show = function(x, y) {
	this.div.style.left = x + 15;
	this.div.style.top = y + 5;
	this.div.style.visibility = "visible";
}

Pelagios.Tooltip.prototype.hide = function() {
	this.div.style.visibility = "hidden";
}

Pelagios.Tooltip.prototype.remove = function() {
	// document.body.removeChild(this.div);
}

Pelagios.Palette = function() {
	this.bright = ["#9c9ede", "#cedb9c", "#e7cb94", "#e7969c", "#de9ed6"];
	
	this.dark = new Array();
	this.dark["#9c9ede"] = "#4a5584";
	this.dark["#cedb9c"] = "#637939";
	this.dark["#e7cb94"] = "#8c6d31";
	this.dark["#e7969c"] = "#843c39";
	this.dark["#de9ed6"] = "#7b4173";	
	
	this.colors = new Array();

    this.counter = -1;
}

Pelagios.Palette.prototype.getColor = function(name) {
	// Return color stored for this name
	if (this.colors[name])
		return this.colors[name];
	
	// If not yet in list, pick the next...
	this.counter++;
	if (this.bright > this.bright.length)
		this.counter = 0;
	
	// and store
	this.colors[name] = this.bright[this.counter];
	
	return this.bright[this.counter];
}

Pelagios.Palette.prototype.darker = function(color) {
	return this.dark[color];
}