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
			var dNode = personalGraph.newDataset(data[i].dataset);
			personalGraph.newEdge(pNode, dNode, data[i].references);
		}
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });
}

Pelagios.Async.prototype.findShortestPath = function(from, to, personalGraph) {	
<<<<<<< HEAD
=======
	var pGraph = personalGraph;
>>>>>>> 588206ca517b0ae172efe2856ec028ccab8a32c2
	$.getJSON("places/shortestpath?from=" + encodeURI(from.place.uri) + 
		"&to=" + encodeURI(to.place.uri), function(data) {

		var lastNode = from;
		for (var i=0, ii=data.length; i<ii; i++) {
<<<<<<< HEAD
			newNode = personalGraph.newDataset(data[i].dataset);
			personalGraph.newEdge(lastNode, newNode, data[i].src);
			lastNode = newNode;
		}
		personalGraph.newEdge(lastNode, to, data[data.length - 1].dest);
=======
			lastNode = pGraph.newDataset(data[i]);
			pGraph.newEdge(lastNode, data[i], data[i].src);
		}
		pGraph.newEdge(lastNode, to, data[data.length - 1].dest);
>>>>>>> 588206ca517b0ae172efe2856ec028ccab8a32c2
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

