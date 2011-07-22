// Code related to a place's 'personal graph'
Pelagios.PersonalGraph = function(id, raphael, map) {
	this.id = id;	
	this.raphael = raphael;
	this.map = map;
	this.locus = null;
	
	this.graph = new Graph();
    this.layout = new Layout.ForceDirected(this.graph, 400, 30, 0.3);
    
    this.pAsync = Pelagios.Async.getInstance();
    
    // Keep track of places, datasets and edges in the graph
    this.places = new Array();
    this.datasets = new Array();
    this.edges = new Array();
    this.maxEdgeWeight = 0;
    this.maxDatasetSize = 0;
    
	this.getWidthFromWeight = function(weight) {
		var w = 12 * weight / this.maxEdgeWeight;
		if (w < 2)
			w = 2;
		return w;
	}
	
	this.getRadiusFromSize= function(size) {
		var r = 25 * size / this.maxDatasetSize;
		if (r < 5)
			r = 5;
		return r;
	}
    
	var toScreen = this.toScreen;
    this.renderer = new Renderer(10, this.layout,
	  function clear() { },
	  
	  function drawEdge(edge, p1, p2) {
		  if (edge.connection)
			  raphael.pelagios.connection(edge.connection);
	  },
	  
	  function drawNode(node, pt) {
		  var xy = toScreen(pt, this.layout);
		  
		  if (node.place) {
			  raphael.pelagios.placeLabel(node.set, xy.x, xy.y);
		  } else {
			  raphael.pelagios.datasetLabel(node.set, xy.x, xy.y);
		  }
	  });
	
    // This is ugly... but don't know how to get rid of
    // the global - it's needed in the move event handler
    window.personalGraph = this;
}

Pelagios.PersonalGraph.prototype = new Pelagios.AbstractGraph();

Pelagios.PersonalGraph.prototype.graphChanged = function() {
	var viewport = Pelagios.getViewport();
    this.raphael.setSize(viewport.x, viewport.y);
	this.renderer.graphChanged();
}

Pelagios.PersonalGraph.prototype.hide = function() {
	setTimeout(function(){ 
		this.clear();
		document.getElementById(this.id).style.visibility = "hidden";
	}.bind(this), 500);
	document.getElementById(this.id).style.opacity = 0;
	this.map.clear();
}

Pelagios.PersonalGraph.prototype.show = function() {
	this.map.clear();
	document.getElementById(this.id).style.visibility = "visible";
	document.getElementById(this.id).style.opacity = 1;
}

Pelagios.PersonalGraph.prototype.newPlace = function(place) {
	var n;
	if (this.places[place]) {
		n = this.places[place];
	} else {
	    n = this.graph.newNode();
	    
	    n.set = this.raphael.pelagios.placeLabel(place.label);
	    n.place = place;
	    n.name = place.label;
	    n.data.mass = 0.8;
	    
	    for (var i=0, ii=this.places.length; i<ii; i++) {
	    	this.graph.newEdge(n, this.places[i], { length: 2.5 });
	    }
	
	    // Seems kind of recursive... but we need that in
	    // the move handler, which only has access to the
	    // individual elements inside the set, not the original
	    // set or graph node.
	    for (var i=0, ii=n.set.length; i<ii; i++) {
	        n.set[i].graphNode = n;    	
	    }
	    
	    var map = this.map;
		n.set[0].mouseover(function(event) {
			map.highlight(place.label, true);
		});
		n.set[0].mouseout(function (event) {
			map.highlight(place.label, false);			
		});
		n.set[0].click(function (event) {
			map.zoomTo(place.label);			
		});
	    
	    n.set.drag(
	        	this.handler.move,
	        	this.handler.drag,
	        	this.handler.up);
	    
	    // Find paths between places
	    for (var i=0, ii=this.places.length; i<ii; i++) {
	        this.pAsync.findShortestPaths(n, this.places[i], this);    	
	    }
	    
	    this.places.push(n);
	}
    return n;
}

Pelagios.PersonalGraph.prototype.newDataset = function(datasetLabel, datasetSize, rootLabel) {
	var n;
	if (this.datasets[datasetLabel]) {
		n = this.datasets[datasetLabel];
		var r = this.getRadiusFromSize(n.size);
		n.set[0].animate({rx:r, ry:r}, 500);
	} else {
		if (datasetSize > this.maxDatasetSize) {
			this.maxDatasetSize = datasetSize;
			this.normalizeDatasetSizes();
		}
		
	    var fill = Pelagios.Palette.getInstance().getColor(rootLabel);
	    
	    n = this.graph.newNode();
	    n.name = datasetLabel;
	    n.size = datasetSize;
	    n.set = this.raphael.pelagios.datasetLabel(
	    		datasetLabel + "\n" + datasetSize + " Geoannotations",
	    		fill,
	    		Pelagios.Palette.getInstance().darker(fill));
	    
		var r = this.getRadiusFromSize(datasetSize);
	    n.set[0].attr({rx:r, ry:r});
	    
	    var map = this.map;
		n.set[0].mouseover(function(event) {
		    n.set[1].animate({
		    	"opacity" : 1,
		    }, 200);
		    map.showFeature(datasetLabel);
		});
		n.set[0].mouseout(function (event) {
		    n.set[1].animate({
		    	"opacity" : 0,
		    }, 200);
		    map.hideFeature(datasetLabel);
		});
		n.set[0].click(function (event) {
			map.zoomTo(datasetLabel);			
		});
	
	    // Seems kind of recursive... but we need that in
	    // the move handler, which only has access to the
	    // individual elements inside the set, not the original
	    // set or graph node.
	    for (var i=0, ii=n.set.length; i<ii; i++) {
	        n.set[i].graphNode = n;    	
	    }
	    
	    n.set[0].drag(
	        	this.handler.move,
	        	this.handler.drag,
	        	this.handler.up);

	    this.datasets[datasetLabel] = n;
	}
    return n;
}

Pelagios.PersonalGraph.prototype.findEdgesFor = function(dataset) {
	var dsEdges = new Array();
	for (var e in this.edges) {
		var edge = this.edges[e];
		if ((edge.to == dataset) || (edge.from == dataset)) {
			dsEdges.push(edge);	
		}
	}
	return dsEdges;
}

Pelagios.PersonalGraph.prototype.purgeGraph = function() {
	if (this.places.length > 1) {
		var maxDatasetSize = 0;
		var maxEdgeWeight = 0;
		
		for (var d in this.datasets) {
			var dataset = this.datasets[d];			
			var edges = this.findEdgesFor(dataset);
	
			if (edges.length < 2) { 
				delete this.edges[edges[0].from.name];
				edges[0].connection.line.remove();
				dataset.set.remove();
				this.graph.removeNode(dataset);
				delete this.datasets[dataset.name];
			} else {
				if (dataset.size > maxDatasetSize)
					maxDatasetSize = dataset.size;
				
				for (var i=0, ii=edges.length; i<ii; i++) {
					if (edges[i].connection.weight > maxEdgeWeight)
						maxEdgeWeight = edges[i].connection.weight;
				}
			}
		}
		
	    this.maxDatasetSize = maxDatasetSize;
	    this.maxEdgeWeight = maxEdgeWeight;
		this.normalizeLineWidths();
		this.normalizeDatasetSizes();
	}
}

Pelagios.PersonalGraph.prototype.edgeExists = function(from, to) {
	for (var e in this.edges) {
		var edge = this.edges[e];
		if (edge.from == from && edge.to == to)
			return edge;
		
		if (edge.to == from && edge.from == to)
			return e;
	}
	
	return false;
}

Pelagios.PersonalGraph.prototype.setEdge = function(arg0, arg1, arg2) {	
	if (arg1) {
		// arg0 -> from, arg1 -> to, arg2 -> weight
		var exists = this.edgeExists(arg0, arg1); 
		if (exists)
			return exists;
		
		if (arg2 > this.maxEdgeWeight) {
			this.maxEdgeWeight = arg2;
			this.normalizeLineWidths();
		}
		
		var sizeNorm = this.getWidthFromWeight(arg2);
		
	    var e = this.graph.newEdge(arg0, arg1, { length: 1 });
	    e.connection = this.raphael.pelagios.connection(arg0.set[0], arg1.set[0], "#000", sizeNorm);
	    e.connection.weight = arg2;
	    e.from = arg0;
	    e.to = arg1;
		e.connection.tooltip = new Pelagios.Tooltip(arg2 + " occurences in " + arg1.name);
	    
	    e.connection.line.mouseover(function(event) {
	    	e.connection.tooltip.show(event.clientX, event.clientY);
			// map.showPolygon(arg0.name + "-" + arg1.name);
		});
		
	    e.connection.line.mouseout(function (event) {
	    	e.connection.tooltip.hide();
			// map.hidePolygon(arg0.name + "-" + arg1.name);
		});
	    
	    this.edges[e.from.name + "-" + e.to.name] = e;
	    return e;
	} else {
		// arg0 -> edge
		arg0.connection.line
			.animate({ "stroke-width" : this.getWidthFromWeight(arg0.connection.weight) }, 500);
		return arg0;
	}
}

Pelagios.PersonalGraph.prototype.normalizeLineWidths = function() {
	for (var e in this.edges) {
		this.setEdge(this.edges[e]);
	}
}

Pelagios.PersonalGraph.prototype.normalizeDatasetSizes = function() {
	for (var d in this.datasets) {
		this.newDataset(d);
	}
}

Pelagios.PersonalGraph.prototype.clear = function() {
    this.maxDatasetSize = 0;
    this.maxEdgeWeight = 0;
    
	for (var d in this.datasets) {
		var dataset = this.datasets[d];			
		var edges = this.findEdgesFor(dataset);
		
		for (var i=0, ii=edges.length; i<ii; i++) {
			edges[i].connection.line.remove();
			delete this.edges[edges[i].from.name];
		}
		this.graph.removeNode(dataset);
		dataset.set.remove();
		delete this.datasets[dataset.name];
	}
	
	for (var i=0, ii=this.places.length; i<ii; i++) {
		this.places[i].set.remove();
	}
	this.places.length = 0;
}

Pelagios.PersonalGraph.prototype.handler = {

	drag : function() {
		if (this.graphNode.place) {
			this.ox = this.attr("x");
			this.oy = this.attr("y");
		} else {
			this.ox = this.attr("cx");
			this.oy = this.attr("cy");			
		}
	},
		
	move : function(dx, dy) {
		if (this.graphNode.place) {
			window.personalGraph.raphael.pelagios.placeLabel(this.graphNode.set, this.ox + dx, this.oy + dy);
		} else {
			window.personalGraph.raphael.pelagios.datasetLabel(this.graphNode.set, this.ox + dx, this.oy + dy);			
		}
		var pt = window.personalGraph.fromScreen(new Vector(this.ox + dx, this.oy + dy));
		window.personalGraph.layout.point(this.graphNode).p = pt;
		window.personalGraph.renderer.start();
	},
		
	up : function() {

	}

}