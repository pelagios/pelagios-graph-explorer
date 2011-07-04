var raphael;
var pGraph;

// TODO move the instantiation of drawing elements into the Graph
window.onload = function () {
	var viewport = Pelagios.getViewport();
    raphael = Raphael("dataset-panel", viewport.x, viewport.y);
    
    pGraph = new Pelagios.Graph(raphael);
    var node1 = pGraph.newNode(12);
    var node2 = pGraph.newNode(8);
    var node3 = pGraph.newNode(6);
    var node4 = pGraph.newNode(9);
    var node5 = pGraph.newNode(9);
    
    pGraph.newEdge(node1, node2, 4);
    pGraph.newEdge(node1, node3, 12);
    pGraph.newEdge(node2, node3, 2);
    pGraph.newEdge(node4, node1, 2);
    pGraph.newEdge(node5, node1, 9);

    // TODO get rid of this
    for (var i=0, ii=datasets.length; i<ii; i++) {
    	datasets[i].attr({fill:"#9C9EDE", stroke:"#777", "fill-opacity": 1, "stroke-width": 1, cursor: "move"});
    	datasets[i].drag(pGraph.handler.move, pGraph.handler.drag, pGraph.handler.up);
    	datasets[i].dblclick(function (event) {
    		fetchDatasets(this);
    	});
    	datasets[i].toFront();
    }
        
    pGraph.renderer.start();
}

function addDataset(dataset, parent) {
	// Add to visual representation
	var blob = raphael.ellipse(10, 10, 20, 20);
	blob.attr({fill:"#ff0000", stroke:"#ff0000", "fill-opacity": 1, "stroke-width": 2, cursor: "move"});
	blob.drag(pGraph.handler.move, pGraph.handler.drag, pGraph.handler.up);
	blob.dblclick(function (event) {
		fetchDatasets(this);
	});
	blob.name = dataset.name;
    datasets.push(blob);
    
	// Add to graph model
	var node = graph.newNode();
	node.dataset = blob;
	blob.node = node;
	
	if (parent)
		connections.push(p.connection(blob, parent, "#000"));
}

function fetchDatasets(parent) {
	$.getJSON("datasets/" + parent.name, function(data) {
		  for (var i=0; i<data.length; i++) {
			  addDataset(data[i], parent);
		  }
	})
	.error(function() { alert("error"); });
}

