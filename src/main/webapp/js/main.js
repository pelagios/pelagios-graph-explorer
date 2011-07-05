var raphael;
var pGraph;

// TODO move the instantiation of drawing elements into the Graph
window.onload = function () {
	var viewport = Pelagios.getViewport();
    raphael = Raphael("dataset-panel", viewport.x, viewport.y);
    
    pGraph = new Pelagios.Graph(raphael);
    
    fetchDatasets();
}

function addDataset(dataset, parent) {
	var node = pGraph.newNode(dataset.name, 12, fetchDatasets);
	
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

