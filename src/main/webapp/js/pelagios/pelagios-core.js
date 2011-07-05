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
	 
	 return new Vector(w, h * 0.6);
}