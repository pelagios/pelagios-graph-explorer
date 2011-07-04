var graph;
var datasets;
var p;
var connections;
var move, dragger, up;

window.onload = function () {
	var viewport = Pelagios.getViewport();
    p = Raphael("dataset-panel", viewport.x, viewport.y);

    move = function (dx, dy) {
        this.attr({cx: this.ox + dx, cy: this.oy + dy});
        for (var i = connections.length; i--;) {
            p.pelagios.connection(connections[i]);
        }
        var pt = pGraph.fromScreen(new Vector(this.ox + dx, this.oy + dy));
        pGraph.layout.point(this.gnode).p = pt;
        renderer.start(); 
    };
    
    dragger = function () {
        this.ox = this.attr("cx");
        this.oy = this.attr("cy");
        this.animate({"fill-opacity": .2}, 100);
    };
    
    up = function () {
        this.animate({"fill-opacity": 1}, 100);
    };
    
	datasets = new Array();
    datasets.push(p.pelagios.dataset(12));
    datasets.push(p.pelagios.dataset(8));
    datasets.push(p.pelagios.dataset(6));
    datasets.push(p.pelagios.dataset(9));
    datasets.push(p.pelagios.dataset(9));
	
    connections = new Array();
    connections.push(p.pelagios.connection(datasets[0], datasets[1], "#000", 4));
    connections.push(p.pelagios.connection(datasets[1], datasets[2], "#000", 12));
    connections.push(p.pelagios.connection(datasets[0], datasets[2], "#000", 2));
    connections.push(p.pelagios.connection(datasets[0], datasets[3], "#000", 2));
    connections.push(p.pelagios.connection(datasets[0], datasets[4], "#000", 9));
    
    for (var i=0, ii=datasets.length; i<ii; i++) {
    	datasets[i].attr({fill:"#9C9EDE", stroke:"#777", "fill-opacity": 1, "stroke-width": 1, cursor: "move"});
    	datasets[i].drag(move, dragger, up);
    	datasets[i].dblclick(function (event) {
    		fetchDatasets(this);
    	});
    	datasets[i].toFront();
    }
        
    var pGraph = new Pelagios.Graph();
    var node1 = pGraph.newNode(datasets[0]);
    var node2 = pGraph.newNode(datasets[1]);
    var node3 = pGraph.newNode(datasets[2]);
    var node4 = pGraph.newNode(datasets[3]);
    var node5 = pGraph.newNode(datasets[4]);
    
    // connect them with an edge
    pGraph.newEdge(node1, node2);
    pGraph.newEdge(node1, node3);
    pGraph.newEdge(node2, node3);
    pGraph.newEdge(node4, node1);
    pGraph.newEdge(node5, node1);
    
    pGraph.renderer.start();
}

function addDataset(dataset, parent) {
	// Add to visual representation
	var blob = p.ellipse(10, 10, 20, 20);
	blob.attr({fill:"#ff0000", stroke:"#ff0000", "fill-opacity": 0.8, "stroke-width": 2, cursor: "move"});
	blob.drag(move, dragger, up);
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

