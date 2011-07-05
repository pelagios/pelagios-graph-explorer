var raphael;
var pGraph;
var pMap;

// TODO move the instantiation of drawing elements into the Graph
window.onload = function () {
	var viewport = Pelagios.getViewport();
    raphael = Raphael("dataset-panel", viewport.x, viewport.y);
    
    pGraph = new Pelagios.Graph(raphael);
    pMap = new Pelagios.Map();

    fetchDatasets();
}

function addDataset(dataset, parent) {
	var node = pGraph.newNode(
		dataset.name,
		12, dataset.records, dataset.places,
		fetchDatasets, // on doubleclick
		function() pMap.showPolygon(dataset.name), // on mouseover
		function() pMap.hidePolygon(dataset.name)); // on mouseout
	
	node.convexHull = fetchConvexHull(dataset);
	
	if (parent)
		pGraph.newEdge(node, parent, 4);
}

function fetchDatasets(parent) {
	var url = "datasets/"
	if (parent)
		url += parent.name;
		
	$.getJSON(url, function(data) {
		  for (var i=0; i<data.length; i++) {
			  addDataset(data[i], parent);
		  }
	})
	.error(function() { alert("Error."); });
}

function fetchConvexHull(dataset) {
	$.getJSON("datasets/" + dataset.name + "/places/convexhull", function(data) {
		pMap.addPolygon(dataset.name, data);
	})
	.error(function(data) { alert("Something went wrong: " + data.responseText); });	
}

