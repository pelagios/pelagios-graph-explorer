// Client-side code related to the server API calls
Pelagios.Async = function(pGraph, pMap) {
	this.pGraph = pGraph;
	this.pMap = pMap;
}

Pelagios.Async.prototype.getAutoCompleteHint = function(term, callback) {
	$.getJSON("places/search?q=" + term, function(data) {
		callback(data);
	});
}

Pelagios.Async.prototype.fetchConvexHull = function(node) {
	var pMap = this.pMap;
	$.getJSON("datasets/" + node.name + "/places/convexhull", function(data) {
		pMap.addPolygon(node.name, data, node.stroke);
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });	
}

Pelagios.Async.prototype.fetchPlaceReferences = function(place, personalGraph) {
	var pNode = personalGraph.newPlace(place);
	$.getJSON("places/references?place=" + encodeURI(place.uri), function(data) {
		for (var i=0, ii=data.length; i<ii; i++) {
			var dNode = personalGraph.newDataset(data[i].dataset, data[i].rootdataset);
			personalGraph.newEdge(pNode, dNode, data[i].references);
		}
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });
}

Pelagios.Async.prototype.findShortestPath = function(from, to, personalGraph) {	
	$.getJSON("places/shortestpath?from=" + encodeURI(from.place.uri) + 
		"&to=" + encodeURI(to.place.uri), function(data) {

		var lastNode = from;
		for (var i=0, ii=data.length; i<ii; i++) {

			newNode = personalGraph.newDataset(data[i].dataset);
			personalGraph.newEdge(lastNode, newNode, data[i].src);
			lastNode = newNode;
		}
		personalGraph.newEdge(lastNode, to, data[data.length - 1].dest);
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });	
}

/*
Pelagios.Async.prototype.computeOverlaps = function() {
	// Selected nodes come in an associative array
	var selected = new Array();
	for (var node in this.pGraph.getSelected()) {
		selected.push(this.pGraph.selectedNodes[node]);
	}
	
	// Compute pairwise overlaps
	while (selected.length > 1) {
		var srcNode = selected.pop();
		
		for (var i=0, ii=selected.length; i<ii; i++) {	
	    	var pMap = this.pMap;
	    	var pGraph = this.pGraph;
	    	var destNode = selected[i];
	    	fetchOverlap(srcNode, selected[i], this.pGraph, this.pMap);
		}
	}
	
	function fetchOverlap(srcNode, destNode, pGraph, pMap) {
    	var url = "places/intersect/convexhull?set=" +
			srcNode.name + "&set=" + destNode.name;
    	
    	$.getJSON(url, function(data) {	    	
    		pGraph.setLinkWeight(srcNode, destNode, data.commonPlaces);
    		
    		// pMap.addPolygon(srcNode.name + " to " + destNode.name, data.footprint);
    		// pMap.showPolygon("overlap");
    	})
    	.error(function(data) { alert("Something went wrong: " + data.responseText); });			
	}
}
*/

Pelagios.Async.prototype.computeOverlap = function(srcNode, destNode, selectionManager) {
	var url = "places/intersect/convexhull?set=" +
		srcNode.name + "&set=" + destNode.name;
	
	$.getJSON(url, function(data) {
		selectionManager.setLinkWeight(srcNode, destNode, data.commonPlaces);
		
		// pMap.addPolygon(srcNode.name + " to " + destNode.name, data.footprint);
		// pMap.showPolygon("overlap");
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });			
}
