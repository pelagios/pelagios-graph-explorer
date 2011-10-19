// Core functions of the Pelagios Visualization demo

// Namespace declarations
Pelagios = {};

Pelagios.Const = {};
Pelagios.Const.MAX_CONNECTION_WIDTH = 12; 
Pelagios.Const.MIN_CONNECTION_WIDTH = 3;  
Pelagios.Const.MAX_DATASET_RADIUS = 20;  
Pelagios.Const.MIN_DATASET_RADIUS = 5;

Pelagios.Const.DATASET_CHILD_NODE_LIMIT = 20;

Pelagios.Const.CLICK_DELAY = 200;
Pelagios.Const.MAX_CLICK_TIME = 300;

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
	 
	 return new Vector(w - 250, (h - 165) * 0.6);
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
