Pelagios.Graph.Local = {}

Pelagios.Graph.Local.DIV_ID = "personal-graph";

Pelagios.Graph.Local.getInstance = function() {
	if (Pelagios.Graph.Local.instance)
		return Pelagios.Graph.Local.instance;
	
	// Init force layout system	
	var springyGraph = new Graph();
    var layout = new Layout.ForceDirected(springyGraph, 400, 30, 0.3);
    var renderer = new Renderer(10, layout,
		  function clear() { },
		  
		  function drawEdge(edge, p1, p2) {
			  if (edge.connection)
				  raphael.pelagios.connection(edge.connection);
		  },
		  
		  function drawNode(node, pt) {
			  var xy = Pelagios.Graph.Local.instance.toScreen(pt, layout);
			  
			  if (node.place) {
				  raphael.pelagios.placeLabel(node.set, xy.x, xy.y);
			  } else {
				  raphael.pelagios.datasetLabel(node.set, xy.x, xy.y);
			  }
		  }
	);

    // Init drawing canvas
	var viewport = Pelagios.getViewport();
    var raphael = Raphael(Pelagios.Graph.Local.DIV_ID, viewport.x, viewport.y);
    
    // Keep track of places, datasets and edges in the graph
    var places = new Array();
    var datasets = new Array();
    var edges = new Array();
    
    var maxEdgeWeight = 0;
    var maxDatasetSize = 0;
    
	function getWidthFromWeight(weight) {
		var w = Pelagios.Const.MAX_CONNECTION_WIDTH * weight / maxEdgeWeight;
		if (w < Pelagios.Const.MIN_CONNECTION_WIDTH)
			w = Pelagios.Const.MIN_CONNECTION_WIDTH;
		return w;
	}
	
	function getRadiusFromSize(size) {
		var r = Pelagios.Const.MAX_DATASET_RADIUS * size / maxDatasetSize;
		if (r < Pelagios.Const.MIN_DATASET_RADIUS)
			r = Pelagios.Const.MIN_DATASET_RADIUS;
		return r;
	}
	
	function redrawEdges() {
		for (var e in edges) {
			Pelagios.Graph.Local.instance.setEdge(edges[e]);
		}
	}

	function redrawDatasets() {
		for (var d in datasets) {
			Pelagios.Graph.Local.instance.newDataset(d);
		}
	}

    Pelagios.Graph.Local.instance = new Pelagios.Graph.Abstract();

    Pelagios.Graph.Local.instance.refresh = function() {
    	var viewport = Pelagios.getViewport();
    	raphael.setSize(viewport.x, viewport.y);
   		renderer.graphChanged();
    }
    
    Pelagios.Graph.Local.instance.close = function() {
		Pelagios.Map.getInstance().clear();
		setTimeout(function(){ 
			Pelagios.Graph.Local.getInstance().clear();
			document.getElementById(Pelagios.Graph.Local.DIV_ID).style.visibility = "hidden";
		}, 500);
		document.getElementById(Pelagios.Graph.Local.DIV_ID).style.opacity = 0;
	}

    Pelagios.Graph.Local.instance.show = function() {
		Pelagios.Map.getInstance().clear();
		var el = document.getElementById(Pelagios.Graph.Local.DIV_ID);
		el.style.visibility = "visible";
		el.style.opacity = 1;
	}

	Pelagios.Graph.Local.instance.newPlace = function(place) {
		var n;
		if (places[place]) {
			n = places[place];
		} else {
		    n = springyGraph.newNode();
		    
		    n.set = raphael.pelagios.placeLabel(place.label);
		    n.place = place;
		    n.name = place.label;
		    n.data.mass = 0.8;
		    
		    for (var i=0, ii=places.length; i<ii; i++) {
		    	springyGraph.newEdge(n, places[i], { length: 2.5 });
		    }
		
		    // Seems kind of recursive... but we need that in
		    // the move handler, which only has access to the
		    // individual elements inside the set, not the original
		    // set or graph node.
		    for (var i=0, ii=n.set.length; i<ii; i++) {
		        n.set[i].graphNode = n;    	
		    }
		    
		    var map = Pelagios.Map.getInstance();
			n.set[0].mouseover(function(event) {
				map.highlight(place.label, true);
			});
			n.set[0].mouseout(function (event) {
				map.highlight(place.label, false);			
			});
			n.set[0].click(function (event) {
				map.zoomToFeature(place.label);			
			});
		    
		    n.set.drag(
		        	this.handler.move,
		        	this.handler.drag,
		        	this.handler.up);
		    
		    // Find paths between places
		    for (var i=0, ii=places.length; i<ii; i++) {
		        Pelagios.Async.getInstance().findShortestPaths(n, places[i], this);    	
		    }
		    
		    places.push(n);
		}
	    return n;
	}

	Pelagios.Graph.Local.instance.newDataset = function(datasetLabel, datasetSize, rootLabel) {
		var n;
		if (datasets[datasetLabel]) {
			n = datasets[datasetLabel];
			var r = getRadiusFromSize(n.size);
			n.set[0].animate({rx:r, ry:r}, 500);
		} else {
			if (datasetSize > maxDatasetSize) {
				maxDatasetSize = datasetSize;
				redrawDatasets();
			}
			
		    var fill = Pelagios.Palette.getInstance().getColor(rootLabel);
		    
		    n = springyGraph.newNode();
		    n.name = datasetLabel;
		    n.size = datasetSize;
		    n.set = raphael.pelagios.datasetLabel(
		    		datasetLabel + "\n" + datasetSize + " Geoannotations",
		    		fill,
		    		Pelagios.Palette.getInstance().darker(fill));
		    
			var r = getRadiusFromSize(datasetSize);
		    n.set[0].attr({rx:r, ry:r});
		    
		    var map = Pelagios.Map.getInstance();
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
				map.zoomToFeature(datasetLabel);			
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

		    datasets[datasetLabel] = n;
		}
	    return n;
	}

	Pelagios.Graph.Local.instance.findEdgesFor = function(dataset) {
		var dsEdges = new Array();
		for (var e in edges) {
			var edge = edges[e];
			if ((edge.to == dataset) || (edge.from == dataset)) {
				dsEdges.push(edge);	
			}
		}
		return dsEdges;
	}

	Pelagios.Graph.Local.instance.purgeGraph = function() {
		if (places.length > 1) {
			var maxSize = 0;
			var maxWeight = 0;
			
			for (var d in datasets) {
				var dataset = datasets[d];			
				var lEdges = this.findEdgesFor(dataset);
		
				if (lEdges.length < 2) { 
					delete edges[lEdges[0].from.name];
					lEdges[0].connection.line.remove();
					dataset.set.remove();
					springyGraph.removeNode(dataset);
					delete datasets[dataset.name];
				} else {
					if (dataset.size > maxSize)
						maxSize = dataset.size;
					
					for (var i=0, ii=lEdges.length; i<ii; i++) {
						if (lEdges[i].connection.weight > maxWeight)
							maxWeight = lEdges[i].connection.weight;
					}
				}
			}
			
		    maxDatasetSize = maxSize;
		    maxEdgeWeight = maxWeight;
			redrawEdges();
			redrawDatasets();
		}
	}

	Pelagios.Graph.Local.instance.edgeExists = function(from, to) {
		for (var e in this.edges) {
			var edge = this.edges[e];
			if (edge.from == from && edge.to == to)
				return edge;
			
			if (edge.to == from && edge.from == to)
				return e;
		}
		
		return false;
	}

	Pelagios.Graph.Local.instance.setEdge = function(arg0, arg1, arg2) {	
		if (arg1) {
			// arg0 -> from, arg1 -> to, arg2 -> weight
			var exists = this.edgeExists(arg0, arg1); 
			if (exists)
				return exists;
			
			if (arg2 > maxEdgeWeight) {
				maxEdgeWeight = arg2;
				redrawEdges();
			}
			
			var sizeNorm = getWidthFromWeight(arg2);
			
		    var e = springyGraph.newEdge(arg0, arg1, { length: 1 });
		    e.connection = raphael.pelagios.connection(arg0.set[0], arg1.set[0], "#000", sizeNorm);
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
		    
		    edges[e.from.name + "-" + e.to.name] = e;
		    return e;
		} else {
			// arg0 -> edge
			arg0.connection.line
				.animate({ "stroke-width" : getWidthFromWeight(arg0.connection.weight) }, 500);
			return arg0;
		}
	}

	Pelagios.Graph.Local.instance.clear = function() {
	    this.maxDatasetSize = 0;
	    this.maxEdgeWeight = 0;
	    
		for (var d in datasets) {
			var dataset = datasets[d];			
			var lEdges = this.findEdgesFor(dataset);
			
			for (var i=0, ii=lEdges.length; i<ii; i++) {
				lEdges[i].connection.line.remove();
				delete edges[lEdges[i].from.name];
			}
			springyGraph.removeNode(dataset);
			dataset.set.remove();
			delete datasets[dataset.name];
		}
		
		for (var i=0, ii=places.length; i<ii; i++) {
			places[i].set.remove();
		}
		places.length = 0;
	}
	
    Pelagios.Graph.Local.instance.moveNodeTo = function(node, x, y) {
    	if (node.place) {
			raphael.pelagios.placeLabel(node.set, x, y);
		} else {
			raphael.pelagios.datasetLabel(node.set, x, y);			
		}
		var pt = this.fromScreen(new Vector(x, y), layout);
		layout.point(node).p = pt;
		renderer.start();
    }

	Pelagios.Graph.Local.instance.handler = {

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
    		Pelagios.Graph.Local.getInstance()
				.moveNodeTo(this.graphNode, this.ox + dx, this.oy + dy);
		},
			
		up : function() {

		}

	}
	
    return Pelagios.Graph.Local.instance;
}