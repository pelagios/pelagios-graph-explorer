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
  			  
  			  /*
  		      for (var i = connections.length; i--;) {
  		    	  raphael.pelagios.connection(connections[i]);
  		      }
  		      */
  		      
  			  node.dataset.attr({cx: xy.x, cy: xy.y});
  		  }
    );
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

Pelagios.Graph.prototype.newNode = function(size) {
    var n = this.graph.newNode();
    n.dataset = this.raphael.pelagios.dataset(size);
    return n;
}
		
Pelagios.Graph.prototype.newEdge = function(from, to, width) {
    var e = this.graph.newEdge(from, to);
    e.connection = raphael.pelagios.connection(from.dataset, to.dataset, "#000", width);
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
		for (var i = connections.length; i--;) {
			p.pelagios.connection(connections[i]);
		}
		var pt = pGraph.fromScreen(new Vector(this.ox + dx, this.oy + dy));
		this.layout.point(this.gnode).p = pt;
		this.renderer.start(); 
	},
		
	up : function() {
		this.animate({"fill-opacity": 1}, 100);
	}

}
