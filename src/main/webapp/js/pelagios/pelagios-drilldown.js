// Code related to the 'drill-down' view of the PELAGIOS Graph
Pelagios.DrilldownPanel = function(id, raphael) {
	this.id = id;	
	this.raphael = raphael;
	this.graph = new Graph();
    this.layout = new Layout.ForceDirected(this.graph, 800, 25, 0.3);
    
	var toScreen = this.toScreen;
    this.renderer = new Renderer(10, this.layout,
    		  function clear() { },
    		  
    		  function drawEdge(edge, p1, p2) {
      			  raphael.pelagios.connection(edge.connection);
    		  },
    		  
    		  function drawNode(node, pt) {
      			  var xy = toScreen(pt, this.layout);
      			  raphael.pelagios.placeLabel(node.set, xy.x, xy.y);
    		  }
      );
}

Pelagios.DrilldownPanel.prototype.toScreen = function(p, layout) {
	var viewport = Pelagios.getViewport();
	var g = layout.getBoundingBox();
		
	var graphSize = g.topright.subtract(g.bottomleft);
	var sx = p.subtract(g.bottomleft).divide(graphSize.x).x * viewport.x;
	var sy = p.subtract(g.bottomleft).divide(graphSize.y).y * viewport.y;

	return new Vector(sx, sy);
}

Pelagios.DrilldownPanel.prototype.fromScreen = function(s) {
	var viewport = Pelagios.getViewport();
	var g = this.layout.getBoundingBox();
	
    var graphSize = g.topright.subtract(g.bottomleft);
    var px = (s.x / viewport.x) * graphSize.x + g.bottomleft.x;
    var py = (s.y / viewport.y) * graphSize.y + g.bottomleft.y;
    return new Vector(px, py);
}

Pelagios.DrilldownPanel.prototype.hide = function() {
	document.getElementById(this.id).style.visibility = "hidden";
	document.getElementById(this.id).style.opacity = 0;
}

Pelagios.DrilldownPanel.prototype.show = function() {
	document.getElementById(this.id).style.visibility = "visible";
	document.getElementById(this.id).style.opacity = 1;
}

Pelagios.DrilldownPanel.prototype.newPlace = function(name) {
    var n = this.graph.newNode();
    n.set = this.raphael.pelagios.placeLabel(name, "#ff3333", "#000000");
    n.set.drag(
        	this.handler.move,
        	this.handler.drag,
        	this.handler.up);
    return n;
}

Pelagios.DrilldownPanel.prototype.newEdge = function(from, to) {
    var e = this.graph.newEdge(from, to, { length: 0.2 });
    e.connection = this.raphael.pelagios.connection(from.set[1], to.set[1], "#000", 2);
    return e;
}

Pelagios.DrilldownPanel.prototype.handler = {

		drag : function() {
			this.ox = this.attr("x");
			this.oy = this.attr("y");
		},
			
		move : function(dx, dy) {
			/*
			window.pGraph.raphael.pelagios.dataset(this.graphNode.set, this.ox + dx, this.oy + dy);
			var pt = window.pGraph.fromScreen(new Vector(this.ox + dx, this.oy + dy));
			window.pGraph.layout.point(this.graphNode).p = pt;
			window.pGraph.renderer.start();
			*/
		},
			
		up : function() {
			/*
			this.animate({
				"scale" : "1.0, 1.0",
			}, 350, "bounce");
			*/
		}

	}