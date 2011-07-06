// Graph-related code
Pelagios.Graph = function(raphael) {	
	this.graph = new Graph();
    this.layout = new Layout.ForceDirected(this.graph, 400, 20, 0.4);
    this.raphael = raphael;
    
    // Keep track of parent->child relations
    this.children = new Array();
    
    // Keep track of node->outbound edges relations
    this.edges = new Array();
    
    // Keep track of selected nodes
    this.selectedNodes = new Array();
    
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
		fill, stroke, click, dblclick, mouseover, mouseout, parent) {
	
    var n = this.graph.newNode();
    n.size = size;
    n.name = name;
    n.selected = false;
    n.opened = false;
    n.fill = fill;
    n.stroke = stroke
    n.data.mass = size / 3;
    
    n.set = this.raphael.pelagios.dataset(name, size, records, places, fill, stroke);
    n.set.drag(
    	this.handler.move,
    	this.handler.drag,
    	this.handler.up);
    
    if (click)
    	n.set.click(function(event) { this.toggleSelect(n); new click(event) }.bind(this));

    if (dblclick)
    	n.set.dblclick(function(event) { new dblclick(n, event); });
    
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
    
    if (parent) {
    	var c;
    	if (this.children[parent.name]) {
    		c = this.children[parent.name];
    	} else {
    		c = new Array();
    		this.children[parent.name] = c;
    	}
    	c.push(n);
    }
    
    return n;
}

Pelagios.Graph.prototype.newEdge = function(from, to, width) {
	var ed;
	if (this.edges[from.name]) {
		ed = this.edges[from.name];
	} else {
		ed = new Array();
		this.edges[from.name] = ed;
	}
	
    var e = this.graph.newEdge(from, to, { length: 0 });
    e.connection = this.raphael.pelagios.connection(from.set[0], to.set[0], "#000", width);
    ed.push(e);
    return e;
}

Pelagios.Graph.prototype.toggleSelect = function(node) {
	node.selected = !node.selected;
	if (node.selected) {
		var cx = node.set[0].attr("cx");
		var cy = node.set[0].attr("cy");
		var size = node.set.size + 5;
		node.set.selection = this.raphael.ellipse(cx, cy, size, size)
			.attr({
				"stroke" : "#000",
				"stroke-width" : 2,
				"opacity" : 0.3,
				"stroke-dasharray" : "-"
			});
		this.selectedNodes[node.name] = node;
	} else {
		delete this.selectedNodes[node.name];
		node.set.selection.remove();
	}
}

Pelagios.Graph.prototype.getChildNodes = function(parent) {
	if (this.children[parent.name])
		return this.children[parent.name];
	return new Array();
}

Pelagios.Graph.prototype.getSelected = function() {
	return this.selectedNodes;
}

Pelagios.Graph.prototype.removeChildNodes = function(parent) {
	// Remove outbound edges (SVG only - graph edges are handled by Springy)
	var ed = this.edges[parent.name];
	if (ed) {
		for (var i=0, ii=ed.length; i<ii; i++) {
			ed[i].connection.line.remove();
		}
		delete this.edges[parent.name];
		
		// Remove child nodes
		var ch = this.children[parent.name];
		for (var i=0, ii=ch.length; i<ii; i++) {
			ch[i].set.remove();
			this.graph.removeNode(ch[i]);
			
			// Remove selection, if any
			if (ch[i].selected)
				this.toggleSelect(ch[i]);
			delete this.selectedNodes[ch[i].name];
		}
		delete this.children[parent.name];
		parent.opened = false;
	}
}

Pelagios.Graph.prototype.handler = {

	drag : function() {
		this.graphNode.data.mass = 10000;
		this.ox = this.attr("cx");
		this.oy = this.attr("cy");
	},
		
	move : function(dx, dy) {
		this.graphNode.data.mass = 10000;
		window.pGraph.raphael.pelagios.dataset(this.graphNode.set, this.ox + dx, this.oy + dy);
		var pt = window.pGraph.fromScreen(new Vector(this.ox + dx, this.oy + dy));
		window.pGraph.layout.point(this.graphNode).p = pt;
		window.pGraph.renderer.start();
	},
		
	up : function() {
		this.animate({
			"scale" : "1.0, 1.0",
		}, 350, "bounce");
		this.graphNode.data.mass = this.graphNode.data.size / 3;
	}

}
