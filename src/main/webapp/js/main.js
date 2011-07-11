window.onload = function() {
	// Init drawing canvas
	var viewport = Pelagios.getViewport();
    var raphael = Raphael("dataset-panel", viewport.x, viewport.y);
    
    // Create Pelagios graph, map and palette objects
    var pGraph = new Pelagios.Graph(raphael);
    var pMap = new Pelagios.Map();
    var palette = new Pelagios.Palette();
    var pAsync = new Pelagios.Async(pGraph, pMap);

    // Fetch datasets from server
    fetchDatasets();
    
    // Init the records -> blobsize scaling factor
    // TODO this is currently overwritten as part of the
    // fetchDatasets method. Should be made cleaner/more
    // readable!
    var sFactor;
    
    // Private methods
    function addDataset(dataset, parent) {
    	var size = Math.sqrt(dataset.records) * sFactor;
    	if (size < 5)
    		size = 5;
    	
    	var fill, stroke;
    	if (parent) {
    		fill = parent.fill;
    		stroke = parent.stroke;
    	} else {
    		fill = palette.next();
    		stroke = palette.darker(fill);
    	}
    		
    	
    	var node = pGraph.newNode(
    		dataset.name,
    		size, dataset.records, dataset.places,
    		fill, stroke,
    		
    		// click
    		function(event) {
    			pAsync.computeOverlaps();
    		}, 
    		
    		// dblclick
    		function(node) {
    			if (node.opened) {
    				removeChildNodes(node);
    			} else {
    				fetchDatasets(node);
    			}
    		},
    		
    		// mouseover
    		function() { pMap.showPolygon(dataset.name) },
    		
    		// mouseout
    		function() { pMap.hidePolygon(dataset.name) },
    		
    		parent);
    	
    	node.convexHull = pAsync.fetchConvexHull(node);
    	
    	if (parent)
    		pGraph.newEdge(parent, node, size / 2);
    }
    
    function removeChildNodes(node) {
    	var children = pGraph.getChildNodes(node);
    	for (var i=0, ii=children.length; i<ii; i++) {
    		removeChildNodes(children[i]);
    	}
    	pGraph.removeChildNodes(node);
    }

    function fetchDatasets(parent) {
    	var url = "datasets/"
    	if (parent) {
        	parent.opened = true;
    		url += parent.name;
    	}
    	
    	$.getJSON(url, function(data) {
    		// Init size scaling factor before adding first data set blobs
    		if (!parent) {
	    		if (!sFactor) {
	    			sFactor = 0;
		    		for (var i=0, ii=data.length; i<ii; i++) {
		    			if (data[i].records > sFactor)
		    				sFactor = data[i].records;
		    		}
		    		sFactor = 20 / Math.sqrt(sFactor);
	    		}
    		}
    		
    		// If there are no more subsets -> drill down
    		if (data.length == 0) {
    			// fetchDetails(parent);
    			return;
    		}
    		
    		// If there are too many datasets to display -> alert
    		if (data.length > 50) {
    			alert("There are " + data.length + " sub-sets to this data set. Sorry, you'll have to wait until the detail/document view is implemented");
    			return;
    		}
    		
    		for (var i=0, ii=data.length; i<ii; i++) {
    			addDataset(data[i], parent);
    		}
    	})
    	.error(function() { parent.opened = false; alert("Error."); });
    }
    
    function fetchDetails(parent) {
    	$.getJSON("datasets/" + parent.name + "/places", function(data) {
    	    var raphael = Raphael("drilldown-panel", viewport.x, viewport.y);
    	    var pDrilldown = new Pelagios.DrilldownPanel("drilldown-panel", raphael);
    		pDrilldown.show();
    		
    		for (var i=0, ii=data.length; i<ii; i++) {
	    		pDrilldown.newPlace(data[i].label);
    		}
    	})
    	.error(function() { alert("Error."); });
    }
    
}

