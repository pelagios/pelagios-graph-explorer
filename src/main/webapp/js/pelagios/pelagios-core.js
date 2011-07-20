// Core functions of the Pelagios Visualization demo

// Namespace declarations
Pelagios = {};

// Utility function for querying current viewport size
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

// AJAX load spinner singleton
Pelagios.Loadmask = function() {
	this.div = document.createElement("div");
	this.div.style.visibility = "visible";
	this.div.innerHTML = "<img class=\"ajax-loader\" src=\"img/ajax-loader.gif\"/>";
	document.body.appendChild(this.div);
	this.ctr = 0;
}

Pelagios.Loadmask.instance = null;

Pelagios.Loadmask.getInstance = function() {
	if (Pelagios.Loadmask.instance == null) {
		Pelagios.Loadmask.instance = new Pelagios.Loadmask();
	}
	return Pelagios.Loadmask.instance;
}
 
Pelagios.Loadmask.prototype.show = function() {
	this.ctr++;
	this.div.style.visibility = "visible";
}

Pelagios.Loadmask.prototype.hide = function() {
	this.ctr--;
	if (this.ctr == 0)
		this.div.style.visibility = "hidden";
}

// A small CSS-stylable mouseover tooltip
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

// The color palette singleton
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

Pelagios.Palette.instance = null;

Pelagios.Palette.getInstance = function() {
	if (Pelagios.Palette.instance == null) {
		Pelagios.Palette.instance = new Pelagios.Palette();
	}
	return Pelagios.Palette.instance;
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

// Google Map extensions (from http://code.google.com/p/google-maps-extensions/)
if (!google.maps.Polygon.prototype.getBounds) {
	google.maps.Polygon.prototype.getBounds = function(latLng) {
	    var bounds = new google.maps.LatLngBounds();
	    var paths = this.getPaths();
	    var path;
	    
	    for (var p = 0; p < paths.getLength(); p++) {
	        path = paths.getAt(p);
	        for (var i = 0; i < path.getLength(); i++) {
	                bounds.extend(path.getAt(i));
	        }
	    }
	
	    return bounds;
	}
}
