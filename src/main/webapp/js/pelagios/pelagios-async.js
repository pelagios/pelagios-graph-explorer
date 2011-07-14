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
	var pNode = personalGraph.newPlace(place.label);
	$.getJSON("places/references?place=" + encodeURI(place.uri), function(data) {
		for (var i=0, ii=data.length; i<ii; i++) {
			var dNode = personalGraph.newPlace(data[i].dataset);
			personalGraph.newEdge(pNode, dNode, data[i].references);
		}
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });
}

Pelagios.Async.prototype.computeOverlaps = function() {
	// Selected nodes come in an associative array
	var selected = new Array();
	for (var node in this.pGraph.getSelected()) {
		selected.push(node);
	}
	
	if (selected.length > 1) {
    	var url = "places/intersect?";
    	for (var i=0, ii=selected.length; i<ii; i++) {
    		url += "set=" + selected[i] + "&";
    	}
    	/*
    	$.getJSON(url, function(data) {
    		alert(data.length + " shared places");
    	})
    	.error(function(data) { alert("Something went wrong: " + data.responseText); });
    	*/
	}
}

