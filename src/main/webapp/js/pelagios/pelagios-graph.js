// Graph-related code
Pelagios.Graph = function(raphael) {	
	this.graph = new Graph();
    this.layout = new Layout.ForceDirected(this.graph, 200, 200, 0.4);
    this.raphael = raphael;
    
	var toScreen = this.toScreen;
    this.renderer = new Renderer(10, this.layout,
  		  function clear() { },
  		  
  		  function drawEdge(edge, p1, p2) {
  			  raphael.pelagios.connection(edge.connection);
  		  },
  		  
  		  function drawNode(node, pt) {
  			  var xy = toScreen(pt, this.layout);
  			  raphael.pelagios.dataset(node.set, xy.x, xy.y);
  		  }
    );
    
    // This is ugly... but don't know how to get rid of
    // the global - it's needed in the move event handler
    window.pGraph = this;
}

Pelagios.Graph.prototype.toScreen = function(p, layout) {
	var viewport = Pelagios.getViewport();
	var g = layout.getBoundingBox();
	
	var graphSize = g.topright.subtract(g.bottomleft);
	var sx = p.subtract(g.bottomleft).divide(graphSize.x).x * viewport.x;
	var sy = p.subtract(g.bottomleft).divide(graphSize.y).y * viewport.y;

	return new Vector(sx, sy);
}

Pelagios.Graph.prototype.fromScreen = function(s) {
	var viewport = Pelagios.getViewport();
	var g = this.layout.getBoundingBox();
	
    var graphSize = g.topright.subtract(g.bottomleft);
    var px = (s.x / viewport.x) * graphSize.x + g.bottomleft.x;
    var py = (s.y / viewport.y) * graphSize.y + g.bottomleft.y;
    return new Vector(px, py);
}

Pelagios.Graph.prototype.newNode = function(name, size, records, places, 
		dblclick, mouseover, mouseout) {
	
    var n = this.graph.newNode();
    n.name = name;
    n.set = this.raphael.pelagios.dataset(name, size, records, places);
    n.set.drag(
    	this.handler.move,
    	this.handler.drag,
    	this.handler.up);
    
    if (dblclick)
    	n.set.dblclick(function(event) { new dblclick(n, event) });
    
    if (mouseover)
    	n.set.mouseover(mouseover);
    
    if (mouseout)
    	n.set.mouseout(mouseout);
    
    // Seems kind of recursive... but we need that in
    // the move handler, which only has access to the
    // individual elements inside the set, not the original
    // set or graph node.
    for (var i=0, ii=n.set.length; i<ii; i++) {
        n.set[i].graphNode = n;    	
    }
    
    return n;
}
		
Pelagios.Graph.prototype.newEdge = function(from, to, width) {
    var e = this.graph.newEdge(from, to);
    e.connection = raphael.pelagios.connection(from.set[0], to.set[0], "#000", width);
    return e;
}

Pelagios.Graph.prototype.handler = {

	drag : function() {
		this.ox = this.attr("cx");
		this.oy = this.attr("cy");
	},
		
	move : function(dx, dy) {
		raphael.pelagios.dataset(this.graphNode.set, this.ox + dx, this.oy + dy);
		var pt = window.pGraph.fromScreen(new Vector(this.ox + dx, this.oy + dy));
		window.pGraph.layout.point(this.graphNode).p = pt;
		window.pGraph.renderer.start(); 
	},
		
	up : function() {
		this.animate({"fill-opacity": 1}, 100);
	}

}
