Pelagios.Graph.Dataset = {}

Pelagios.Graph.Dataset.DIV_ID = "graph-canvas";

Pelagios.Graph.Dataset.getInstance = function() {
	if (Pelagios.Graph.Dataset.instance)
		return Pelagios.Graph.Dataset.instance;
	
	// Init force layout system
	var springyGraph = new Graph();
	var locus = springyGraph.newNode();
    var layout = new Layout.ForceDirected(springyGraph, 200, 30, 0.4);
    var renderer = new Renderer(10, layout,
		  function clear() { },
		  
		  function drawEdge(edge, p1, p2) {
			  if (edge.connection)
				  raphael.pelagios.connection(edge.connection);
		  },
		  
		  function drawNode(node, pt) {
			  if (node.set) {
				  var xy = Pelagios.Graph.Dataset.instance.toScreen(pt, layout);
				  raphael.pelagios.dataset(node.set, xy.x, xy.y);
			  }
		  }
    );

    // Init drawing canvas
    var mousedown = false;
    var startX, startY;
    
	var viewport = Pelagios.getViewport();
    var raphael = Raphael(Pelagios.Graph.Dataset.DIV_ID, viewport.x, viewport.y);
    raphael.canvas.onclick = function(event) {
    	if (event.target.tagName == 'svg') {
    		Pelagios.Graph.Dataset.instance.deselectAll();
    	}
    };
    
    raphael.canvas.onmousedown = function(event) {
    	if (event.target.tagName == 'svg') {
    		mousedown = true;
    		startX = event.clientX;
    		startY = event.clientY;
    	}
    };    
    
    raphael.canvas.onmouseup = function(event) {
    	mousedown = false;
    };
    
    raphael.canvas.onmousemove = function(event){
    	if (mousedown) {
    		var dx = startX - event.clientX;
    		var dy = startY - event.clientY;
    		Pelagios.Graph.Dataset.instance.shiftCenter(dx, dy);
       		renderer.graphChanged();
    		startX = event.clientX;
    		startY = event.clientY;
    	}
    };
    
    // Init selection manager
    var selectionManager 
    	= new Pelagios.SelectionManager(raphael);
    
    // Keep track of parent -> child relations
    var children = new Array();
    
    // Keep track of node -> outbound edges relations
    var edges = new Array();
    
    var maxDatasetSize = 0;
    
    // Initialize the graph
    var loadMask = Pelagios.Loadmask.getInstance();
    loadMask.show();
    
    Pelagios.Async.getInstance().datasets(null, function(data) {
		for (var i=0, ii=data.length; i<ii; i++) {
			if (data[i].geoAnnotations > maxDatasetSize)
				maxDatasetSize = data[i].geoAnnotations;
		}

		for (var i=0, ii=data.length; i<ii; i++) {
			Pelagios.Graph.Dataset.instance.addDataset(data[i]);
		}
		
	    loadMask.hide();
    });
    
	function getRadiusFromSize(numberOfGeoAnnotations) {
    	var r = Math.sqrt(numberOfGeoAnnotations) *
    		Pelagios.Const.MAX_DATASET_RADIUS / Math.sqrt(maxDatasetSize);
    		
    	if (r < Pelagios.Const.MIN_DATASET_RADIUS)
    		r = Pelagios.Const.MIN_DATASET_RADIUS;
    	return r;
	}
    
    Pelagios.Graph.Dataset.instance = new Pelagios.Graph.Abstract();

    Pelagios.Graph.Dataset.instance.refresh = function() {
    	var viewport = Pelagios.getViewport();
    	raphael.setSize(viewport.x, viewport.y);
   		renderer.graphChanged();
    }
    
    Pelagios.Graph.Dataset.instance.addDataset = function(dataset, parent) {
        var n = springyGraph.newNode();
        springyGraph.newEdge(n, locus, { length: 0.2 });
        
        n.dataset = dataset;
        n.selected = false;
        n.opened = false;
        
        var size = getRadiusFromSize(dataset.geoAnnotations);
        n.data.mass = size / 10;
        
        var palette = Pelagios.Palette.getInstance();
        var fill = palette.getColor(dataset.rootDataset);
        var stroke = palette.darker(fill);
        
        n.set = raphael.pelagios.dataset(
        		dataset.name,
        		size,
        		dataset.geoAnnotations,
        		dataset.places,
        		fill, stroke);
        
        n.set.drag(
        	this.handler.move,
        	this.handler.drag,
        	this.handler.up);
        
        // This is to enable clicks AND double clicks on the same element 
        // See http://christopherj.us/javascript-click-and-double-click-on-same-element/
        var clickTimeout = null;
        var lastClick = null;
    	n.set.mousedown(function() {
    		lastClick = new Date().getTime();
    	});
    	
    	n.set.click(function(event) { 
    		if (clickTimeout){ return; }
    		
    		if ((new Date().getTime() - lastClick) > Pelagios.Const.MAX_CLICK_TIME) { return; } 
    		
    		clickTimeout = window.setTimeout(function(){
    			clickTimeout = null;  
    			selectionManager.toggleSelect(n);
    		}, Pelagios.Const.CLICK_DELAY);
    	});
        
        n.set.dblclick(function(event) {		
    		if(clickTimeout){
    			clearTimeout(clickTimeout);
    			clickTimeout = null;
    		}
    		
			if (n.opened) {
				Pelagios.Graph.Dataset.getInstance().closeNode(n);
			} else {
				Pelagios.Async.getInstance().datasets(n.dataset, function(data) {
					n.opened = true;
					if (data.length > Pelagios.Const.DATASET_CHILD_NODE_LIMIT) {
						alert("This dataset has " + data.length + " child sets. That's too much " +
							"to display it in the graph view. We're working on it!");
						return;
					}
					
					for (var i=0, ii=data.length; i<ii; i++) {
						Pelagios.Graph.Dataset.getInstance().addDataset(data[i], n);
					}
				});
			}
        });
        
		Pelagios.Async.getInstance().fetchConvexHull(dataset);
        
        var map = Pelagios.Map.getInstance();
        n.set.mouseover(function() { map.showFeature(dataset.name); });
        n.set.mouseout(function() { map.hideFeature(dataset.name); });
        
        // Seems kind of recursive... but we need that in
        // the move handler, which only has access to the
        // individual elements inside the set, not the original
        // set or graph node.
        for (var i=0, ii=n.set.length; i<ii; i++) {
            n.set[i].graphNode = n;    	
        }
        
        if (parent) {
        	var c;
        	if (children[parent.dataset.name]) {
        		c = children[parent.dataset.name];
        	} else {
        		c = new Array();
        		children[parent.dataset.name] = c;
        	}
        	c.push(n);
        	Pelagios.Graph.Dataset.instance.newEdge(parent, n);
        }
        
        return n;
    }

    Pelagios.Graph.Dataset.instance.newEdge = function(from, to, width) {
    	var ed;
    	if (edges[from.dataset.name]) {
    		ed = edges[from.dataset.name];
    	} else {
    		ed = new Array();
    		edges[from.dataset.name] = ed;
    	}
    
        var e = springyGraph.newEdge(from, to, { length: .05 });
        e.connection = raphael.pelagios.connection(from.set[0], to.set[0], "#000", width);
        ed.push(e);
        return e;
    }

    Pelagios.Graph.Dataset.instance.getChildNodes = function(parent) {
    	if (children[parent.dataset.name])
    		return children[parent.dataset.name];
    	return new Array();
    }

    Pelagios.Graph.Dataset.instance.closeNode = function(node) {
    	var children = this.getChildNodes(node);
    	for (var i=0, ii=children.length; i<ii; i++) {
    		this.closeNode(children[i]);
    	}
    	this.removeChildNodes(node);
    }
    
    Pelagios.Graph.Dataset.instance.removeChildNodes = function(parent) {
    	// Remove outbound edges (SVG only - graph edges are handled by Springy)
    	var ed = edges[parent.dataset.name];
    	if (ed) {
    		for (var i=0, ii=ed.length; i<ii; i++) {
    			ed[i].connection.line.remove();
    		}
    		delete edges[parent.dataset.name];
    		
    		// Remove child nodes
    		var ch = children[parent.dataset.name];
    		for (var i=0, ii=ch.length; i<ii; i++) {
    			ch[i].set.remove();
    			springyGraph.removeNode(ch[i]);
    			
    			// Remove selection, if any
    			if (ch[i].selected)
    				selectionManager.toggleSelect(ch[i]);
    		}
    		delete children[parent.dataset.name];
    		parent.opened = false;
    	}
    }

    Pelagios.Graph.Dataset.instance.isSelected = function(name) {
    	return selectionManager.isSelected(name);
    }

    Pelagios.Graph.Dataset.instance.deselectAll = function() {
    	selectionManager.deselectAll();
    }
    
    Pelagios.Graph.Dataset.instance.moveDatasetTo = function(node, x, y) {
		raphael.pelagios.dataset(node.set, x, y);
		var pt = this.fromScreen(new Vector(x, y), layout);
		layout.point(node).p = pt;
		renderer.start();   	
    }

    Pelagios.Graph.Dataset.instance.handler = {

    	drag : function() {
    		this.ox = this.attr("cx");
    		this.oy = this.attr("cy");
    	},
    		
    	move : function(dx, dy) {
    		Pelagios.Graph.Dataset.getInstance()
    			.moveDatasetTo(this.graphNode, this.ox + dx, this.oy + dy);
    	},
    		
    	up : function() {
    		this.animate({
    			"scale" : "1.0, 1.0",
    		}, 350, "bounce");
    	}

    }
    
    return Pelagios.Graph.Dataset.instance;
}



