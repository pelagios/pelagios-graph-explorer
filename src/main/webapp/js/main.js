window.onload = function () {
	// Init drawing canvas
	var viewport = Pelagios.getViewport();
    var raphael = Raphael("dataset-panel", viewport.x, viewport.y);
    
    // Create Pelagios graph and map objects
    var pGraph = new Pelagios.Graph(raphael);
    var pMap = new Pelagios.Map();

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
    	
    	var node = pGraph.newNode(
    		dataset.name,
    		size, dataset.records, dataset.places,
    		
    		// click
    		function(event) {}, 
    		
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
    	
    	node.convexHull = fetchConvexHull(dataset);
    	
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
    		if (!sFactor) {
    			sFactor = 0;
	    		for (var i=0, ii=data.length; i<ii; i++) {
	    			if (data[i].records > sFactor)
	    				sFactor = data[i].records;
	    		}
	    		sFactor = 20 / Math.sqrt(sFactor);
    		}
    		
    		for (var i=0, ii=data.length; i<ii; i++) {
    			addDataset(data[i], parent);
    		}
    	})
    	.error(function() { parent.opened = false; alert("Error."); });
    }

    function fetchConvexHull(dataset) {
    	$.getJSON("datasets/" + dataset.name + "/places/convexhull", function(data) {
    		pMap.addPolygon(dataset.name, data);
    	})
    	.error(function(data) { alert("Something went wrong: " + data.responseText); });	
    }
}

