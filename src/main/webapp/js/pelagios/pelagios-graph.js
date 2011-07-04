// Graph-related code
Pelagios.Graph = function() {
	this.graph = new Graph();
    this.layout = new Layout.ForceDirected(this.graph, 800, 200, 0.2);

	var toScreen = this.toScreen;
    this.renderer = new Renderer(10, this.layout,
  		  function clear() {
  		    // do nothing
  		  },
  		  function drawEdge(edge, p1, p2) {
  		    // do nothing
  		  },
  		  function drawNode(node, pt) {
  			  var xy = toScreen(pt, this.layout);
  		      for (var i = connections.length; i--;) {
  		    	  p.pelagios.connection(connections[i]);
  		      }
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
};

Pelagios.Graph.prototype.fromScreen = function(s) {
	var viewport = Pelagios.getViewport();
	var g = this.layout.getBoundingBox();
	
    var graphSize = g.topright.subtract(g.bottomleft);
    var px = (s.x / viewport.x) * graphSize.x + g.bottomleft.x;
    var py = (s.y / viewport.y) * graphSize.y + g.bottomleft.y;
    return new Vector(px, py);
};

Pelagios.Graph.prototype.newNode = function(dataset) {
    var n = this.graph.newNode();
    n.dataset = dataset;
    dataset.gnode = n;
    return n;
}

Pelagios.Graph.prototype.newEdge = function(from, to) {
    this.graph.newEdge(from, to);
}


