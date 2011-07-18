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

Pelagios.Async.prototype.computeOverlap = function(srcNode, destNode, selectionManager) {
	var url = "places/intersect?set1=" +
		srcNode.name + "&set2=" + destNode.name;
	
	$.getJSON(url, function(data) {
		selectionManager.setLink(srcNode, destNode, data);
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });			
}
