// Graph-related code
Pelagios.Graph = function(raphael) {	
	this.graph = new Graph();
	this.locus = this.graph.newNode();
    this.layout = new Layout.ForceDirected(this.graph, 200, 30, 0.4);
    this.raphael = raphael;
    
    // Keep track of parent->child relations
    this.children = new Array();
    
    // Keep track of node->outbound edges relations
    this.edges = new Array();
    
    // Keep track of selected nodes
    this.selectedNodes = new Array();
    this.maxOverlapWeight = 0;
    
	var toScreen = this.toScreen;
    this.renderer = new Renderer(10, this.layout,
  		  function clear() { },
  		  
  		  function drawEdge(edge, p1, p2) {
  			  if (edge.connection)
  				  raphael.pelagios.connection(edge.connection);
  		  },
  		  
  		  function drawNode(node, pt) {
  			  if (node.set) {
	  			  var xy = toScreen(pt, this.layout);
	  			  raphael.pelagios.dataset(node.set, xy.x, xy.y);
  			  }
  		  }
    );
    
    this.linkNodes = function(from, to, weight) {
		var fromX = from.set[0].attr("cx");
		var fromY = from.set[0].attr("cy");
		var toX = to.set[0].attr("cx");
		var toY = to.set[0].attr("cy");
    	
		var link = {
			"from" : from,
			"to" : to,
			"line" : this.raphael.path(
				"M" + fromX + " " + fromY + 
				"L" + toX + " " + toY)
			.attr({
				"stroke" : "#FF8000",
				"stroke-width" : 0,
				"opacity" : 0.8,
				"stroke-dasharray" : "-"
			}).toBack(),
			"weight" : weight
		}
		
		// Links are always attached to the source nodes!
		if (!from.set.links)
			from.set.links = new Array();
		
		from.set.links.push(link);
    }
    
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
    this.graph.newEdge(n, this.locus, { length: 0.2 });
    
    n.size = size;
    n.name = name;
    n.selected = false;
    n.opened = false;
    n.fill = fill;
    n.stroke = stroke
    n.data.mass = size / 10;
    
    n.set = this.raphael.pelagios.dataset(name, size, records, places, fill, stroke);
    n.set.drag(
    	this.handler.move,
    	this.handler.drag,
    	this.handler.up);
    
    // This is to enable clicks AND double clicks on the same element 
    // See http://christopherj.us/javascript-click-and-double-click-on-same-element/
    var clickTimeout = null;
    var clickDelay = 200;
    
    var lastClick = null;
    var maxClickTime = 300;
    
    var self = this;
    
    if (click) {
    	n.set.mousedown(function() {
    		lastClick = new Date().getTime();
    	});
    	
    	n.set.click(function(event) { 
    		if (clickTimeout){ return; }
    		
    		if ((new Date().getTime() - lastClick) > maxClickTime) { return; } 
    		
    		clickTimeout = window.setTimeout(function(){
    			clickTimeout = null;  
    			self.toggleSelect(n);
    			if (n.selected)
    				new click(event); 
    		}, clickDelay);
    	});
    }
    
    if (dblclick)
    	n.set.dblclick(function(event) {
    		
    		if(clickTimeout){
    			clearTimeout(clickTimeout);
    			clickTimeout = null;
    		}
    		
    		new dblclick(n, event); 
    	});
    
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
	
    var e = this.graph.newEdge(from, to, { length: .05 });
    e.connection = this.raphael.pelagios.connection(from.set[0], to.set[0], "#000", width);
    ed.push(e);
    return e;
}

Pelagios.Graph.prototype.toggleSelect = function(node) {
	node.selected = !node.selected;
	if (node.selected) {
		var cx = node.set[0].attr("cx");
		var cy = node.set[0].attr("cy");
		var size = node.set.size + 8;
		node.set.selection = this.raphael.ellipse(cx, cy, size, size)
			.attr({
				"stroke" : "#FF8000",
				"stroke-width" : 4,
				"opacity" : 0.8,
				"stroke-dasharray" : "-"
			});
		this.selectedNodes[node.name] = node;
	} else {		
		node.set.selection.remove();
		
		var links = this.findLinks(node);
		for (var i=0, ii=links.length; i<ii; i++) {
			links[i].line.remove;
		}
		// if (node.set.links) {
			//for (var i=0, ii=node.set.links.length; i<ii; i++) {
				//node.set.links[i].line.remove();
			//}	
		// }
		delete this.selectedNodes[node.name];
	}
	
	/* Recompute links
	var allSelectedNodes = new Array();
	for (var node in this.selectedNodes) {
		allSelectedNodes.push(node);
	}
	
	while (allSelectedNodes.length > 0) {
		var src = this.selectedNodes[allSelectedNodes.pop()];

		if (src.set.links) {
			for (var i=0, ii=src.set.links.length; i<ii; i++) {
				src.set.links[i].line.remove();
			}	
		}
		
		src.set.links = new Array();
		for (var i=0, ii=allSelectedNodes.length; i<ii; i++) {
			this.linkNodes(
					src,
					this.selectedNodes[allSelectedNodes[i]],
					0);	
		}
	}
	*/
}

Pelagios.Graph.prototype.getChildNodes = function(parent) {
	if (this.children[parent.name])
		return this.children[parent.name];
	return new Array();
}

Pelagios.Graph.prototype.getSelected = function() {
	return this.selectedNodes;
}

Pelagios.Graph.prototype.findLinks = function(node) {
	if (!this.selectedNodes[node.name])
		return;
	
	var allLinks = new Array();
	
	// Outbound links
	var outbound = node.set.links;
	for (var i=0, ii=outbound.length; i<ii; i++) {
		allLinks.push(outbound[i]);
	}
	
	// Inbound links
	var otherNodes = new Array()
	for (var n in this.selectedNodes) {
		if (n != node.name)
			otherNodes.push(this.selectedNodes[n]);
	}
	
	for (var i=0, ii=otherNodes.length; i<ii; i++) {
		if (otherNodes[i].links) {
			for (var j=0, jj=otherNodes[i].links.length; j<jj; j++) {
				if (otherNodes[i].links[j].to == node) {
					allLinks.push(otherNodes[i].links[j]);
				}	
			}
		}
	}
	
	return allLinks;
}

Pelagios.Graph.prototype.deselectAll = function() {
	for (var sel in this.selectedNodes) {
		this.toggleSelect(this.selectedNodes[sel]);
	}
}

Pelagios.Graph.prototype.setLinkWeight = function(srcNode, destNode, width) {
	if (width > this.maxOverlapWeight) {
		this.maxOverlapWeight = width;
		this.recomputeLinkWeights();
	}
	
	var srcLinks = srcNode.set.links;
	var destLinks = destNode.set.links;
	var link = null;
	
	if (srcLinks) {
		for (var i=0, ii=srcLinks.length; i<ii; i++) {
			if ((srcLinks[i].from == srcNode) && (srcLinks[i].to == destNode)) {
				link = srcLinks[i];
				break;
			}
		}
	}
	
	if ((link == null) && (destLinks)) {
		for (var i=0, ii=destLinks.length; i<ii; i++) {
			if ((destLinks[i].from == srcNode) && (destLinks[i].to == destNode)) {
				link = destLinks[i];
				break;
			}
		}		
	}
	
	if (link) {
		link.weight = width;
		var strokeWidth = 12 * link.weight / this.maxOverlapWeight;
		if (strokeWidth < 2)
			strokeWidth = 2;
		link.line.attr({ "stroke-width" : strokeWidth });
	} else {
		this.linkNodes(srcNode, destNode, width);
	}
}

Pelagios.Graph.prototype.recomputeLinkWeights = function() {
	var allSelectedNodes = new Array();
	for (var node in this.selectedNodes) {
		allSelectedNodes.push(node);
	}
	
	while (allSelectedNodes.length > 0) {
		var src = this.selectedNodes[allSelectedNodes.pop()];
		
		if (src.set.links) {
			for (var i=0, ii=src.set.links.length; i<ii; i++) {
				var link = src.set.links[i];
				var strokeWidth = 12 * link.weight / this.maxOverlapWeight;
				if (strokeWidth < 2)
					strokeWidth = 2;
				link.line.attr({ "stroke-width" :  strokeWidth });
			}	
		}
	}
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
		this.graphNode.data.mass = 100000;
		this.ox = this.attr("cx");
		this.oy = this.attr("cy");
	},
		
	move : function(dx, dy) {
		this.graphNode.data.mass = 100000;
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
