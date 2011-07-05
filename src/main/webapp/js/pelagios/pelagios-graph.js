// Graph-related code
Pelagios.Graph = function(raphael) {	
	this.graph = new Graph();
	this.edges = {};
    this.layout = new Layout.ForceDirected(this.graph, 800, 200, 0.2);
    this.raphael = raphael;
    
	var toScreen = this.toScreen;
    this.renderer = new Renderer(10, this.layout,
  		  function clear() { },
  		  
  		  function drawEdge(edge, p1, p2) {
  			  raphael.pelagios.connection(edge.connection);
  		  },
  		  
  		  function drawNode(node, pt) {
  			  var xy = toScreen(pt, this.layout);
   			  node.el.attr({cx: xy.x, cy: xy.y});
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

Pelagios.Graph.prototype.newNode = function(name, size, dblClickCallback) {
    var n = this.graph.newNode();
    n.name = name;
    n.el = this.raphael.pelagios.dataset(size);
        
    n.el.attr({
    	"fill" : "#9C9EDE",
    	"stroke" : "#777",
    	"fill-opacity" : 1,
    	"stroke-width" : 1,
    	"cursor" : "move"});
    
    n.el.drag(
    	this.handler.move,
    	this.handler.drag,
    	this.handler.up);
    
    if (dblClickCallback)
    	n.el.dblclick(function(event) { new dblClickCallback(n, event) });
    
    // Seems kind of recursive... but we need that in
    // the move handler, which only has access to the
    // Raphael el.
    n.el.graphNode = n;
    
    return n;
}
		
Pelagios.Graph.prototype.newEdge = function(from, to, width) {
    var e = this.graph.newEdge(from, to);
    e.connection = raphael.pelagios.connection(from.el, to.el, "#000", width);
    return e;
}

Pelagios.Graph.prototype.handler = {

	drag : function() {
		this.ox = this.attr("cx");
		this.oy = this.attr("cy");
		this.animate({"fill-opacity": .2}, 100);
	},
		
	move : function(dx, dy) {
		this.attr({cx: this.ox + dx, cy: this.oy + dy});
		var pt = window.pGraph.fromScreen(new Vector(this.ox + dx, this.oy + dy));
		window.pGraph.layout.point(this.graphNode).p = pt;
		window.pGraph.renderer.start(); 
	},
		
	up : function() {
		this.animate({"fill-opacity": 1}, 100);
	}

}
