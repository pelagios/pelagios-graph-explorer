// Code related to a place's 'personal graph'
Pelagios.PersonalGraph = function(id, raphael) {
	this.id = id;	
	this.raphael = raphael;
	
	this.graph = new Graph();
    this.layout = new Layout.ForceDirected(this.graph, 800, 25, 0.3);
    
    this.pAsync = new Pelagios.Async();
    
    // Keep track of places and datasets in the graph
    this.places = new Array();
    this.datasets = new Array();
    
	var toScreen = this.toScreen;
    this.renderer = new Renderer(10, this.layout,
	  function clear() { },
	  
	  function drawEdge(edge, p1, p2) {
		  raphael.pelagios.connection(edge.connection);
	  },
	  
	  function drawNode(node, pt) {
		  var xy = toScreen(pt, this.layout);
		  raphael.pelagios.placeLabel(node.set, xy.x, xy.y);
	  });
	
    // This is ugly... but don't know how to get rid of
    // the global - it's needed in the move event handler
    window.personalGraph = this;
}

Pelagios.PersonalGraph.prototype.toScreen = function(p, layout) {
	var viewport = Pelagios.getViewport();
	var g = layout.getBoundingBox();
		
	var graphSize = g.topright.subtract(g.bottomleft);
	var sx = p.subtract(g.bottomleft).divide(graphSize.x).x * viewport.x;
	var sy = p.subtract(g.bottomleft).divide(graphSize.y).y * viewport.y;

	return new Vector(sx, sy);
}

Pelagios.PersonalGraph.prototype.fromScreen = function(s) {
	var viewport = Pelagios.getViewport();
	var g = this.layout.getBoundingBox();
	
    var graphSize = g.topright.subtract(g.bottomleft);
    var px = (s.x / viewport.x) * graphSize.x + g.bottomleft.x;
    var py = (s.y / viewport.y) * graphSize.y + g.bottomleft.y;
    return new Vector(px, py);
}

Pelagios.PersonalGraph.prototype.hide = function() {
	document.getElementById(this.id).style.visibility = "hidden";
	document.getElementById(this.id).style.opacity = 0;
}

Pelagios.PersonalGraph.prototype.show = function() {
	document.getElementById(this.id).style.visibility = "visible";
	document.getElementById(this.id).style.opacity = 1;
}

Pelagios.PersonalGraph.prototype.newPlace = function(place) {
    var n = this.graph.newNode();
    n.set = this.raphael.pelagios.placeLabel(place.label, "#ff3333", "#000000");
    n.place = place;

    // Seems kind of recursive... but we need that in
    // the move handler, which only has access to the
    // individual elements inside the set, not the original
    // set or graph node.
    for (var i=0, ii=n.set.length; i<ii; i++) {
        n.set[i].graphNode = n;    	
    }
    
    n.set.drag(
        	this.handler.move,
        	this.handler.drag,
        	this.handler.up);
    
    // Find paths between places
    for (var i=0, ii=this.places.length; i<ii; i++) {
        this.pAsync.findShortestPath(n, this.places[i], this);    	
    }
    
    this.places.push(n);
    return n;
}

Pelagios.PersonalGraph.prototype.newDataset = function(label) {
	var n;
	if (this.datasets[label]) {
		n = this.datasets[label];
	} else {
	    n = this.graph.newNode();
	    n.set = this.raphael.pelagios.datasetLabel(label, "#7777ff", "#000000");
	
	    // Seems kind of recursive... but we need that in
	    // the move handler, which only has access to the
	    // individual elements inside the set, not the original
	    // set or graph node.
	    for (var i=0, ii=n.set.length; i<ii; i++) {
	        n.set[i].graphNode = n;    	
	    }
	    
	    n.set.drag(
	        	this.handler.move,
	        	this.handler.drag,
	        	this.handler.up);
	        
	    this.datasets[label] = n;
	}
    return n;
}

Pelagios.PersonalGraph.prototype.newEdge = function(from, to, size) {
    var e = this.graph.newEdge(from, to, { length: 1 });
    e.connection = this.raphael.pelagios.connection(from.set[1], to.set[1], "#000", size);
    return e;
}

Pelagios.PersonalGraph.prototype.handler = {

	drag : function() {
		this.ox = this.attr("x");
		this.oy = this.attr("y");
	},
		
	move : function(dx, dy) {
		window.personalGraph.raphael.pelagios.placeLabel(this.graphNode.set, this.ox + dx, this.oy + dy);
		var pt = window.personalGraph.fromScreen(new Vector(this.ox + dx, this.oy + dy));
		window.personalGraph.layout.point(this.graphNode).p = pt;
		window.personalGraph.renderer.start();
	},
		
	up : function() {

	}

}