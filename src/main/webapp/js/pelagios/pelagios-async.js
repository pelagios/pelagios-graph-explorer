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
	$.getJSON("places/occurences?place=" + encodeURI(place.uri), function(data) {
		for (var i=0, ii=data.length; i<ii; i++) {
			var dNode = personalGraph.newDataset(data[i].dataset, data[i].datasetSize, data[i].rootDataset);
			personalGraph.setEdge(pNode, dNode, data[i].occurences);
		}
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });
}

Pelagios.Async.prototype.findShortestPath = function(from, to, personalGraph) {	
	$.getJSON("places/shortestpaths?from=" + encodeURI(from.place.uri) + 
		"&to=" + encodeURI(to.place.uri), function(data) {

		var lastNode = from;
		for (var i=0, ii=data.length; i<ii; i++) {

			newNode = personalGraph.newDataset(data[i].dataset);
			personalGraph.setEdge(lastNode, newNode, data[i].src);
			lastNode = newNode;
		}
		personalGraph.setEdge(lastNode, to, data[data.length - 1].dest);
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });	
}

Pelagios.Async.prototype.computeOverlap = function(srcNode, destNode, selectionManager) {
	Pelagios.Loadmask.getInstance().show();
	var url = "places/intersect?set1=" +
		srcNode.name + "&set2=" + destNode.name;
	
	$.getJSON(url, function(data) {
		selectionManager.setLink(srcNode, destNode, data);
		Pelagios.Loadmask.getInstance().hide();
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });			
}
