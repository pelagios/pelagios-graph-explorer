var graph;
var datasets;
var p;
var connections;

var move, dragger, up;

window.onload = function () {
	var viewport = getViewportSize();
    p = Raphael("dataset-panel", "100%", "100%");

    // convert to/from screen coordinates
    var toScreen = function(p) {
    	var viewport = getViewportSize();
    	var graph = layout.getBoundingBox();
    	
    	var graphSize = graph.topright.subtract(graph.bottomleft);
    	var sx = p.subtract(graph.bottomleft).divide(graphSize.x).x * viewport.x;
    	var sy = p.subtract(graph.bottomleft).divide(graphSize.y).y * viewport.y;
    	return new Vector(sx, sy);
    };

    fromScreen = function(s) {
    	var viewport = getViewportSize();
    	var graph = layout.getBoundingBox();
    	
	    var graphSize = graph.topright.subtract(graph.bottomleft);
	    var px = (s.x / viewport.x) * graphSize.x + graph.bottomleft.x;
	    var py = (s.y / viewport.y) * graphSize.y + graph.bottomleft.y;
	    return new Vector(px, py);
    };
    
    move = function (dx, dy) {
        this.attr({cx: this.ox + dx, cy: this.oy + dy});
        for (var i = connections.length; i--;) {
            p.Connection(connections[i]);
        }
        var pt = fromScreen(new Vector(this.ox + dx, this.oy + dy));
        layout.point(this.graphnode).p = pt;
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
    datasets.push(p.Dataset(12));
    datasets.push(p.Dataset(8));
    datasets.push(p.Dataset(6));
    datasets.push(p.Dataset(9));
    datasets.push(p.Dataset(9));
	
    connections = new Array();
    connections.push(p.Connection(datasets[0], datasets[1], "#000", 4));
    connections.push(p.Connection(datasets[1], datasets[2], "#000", 12));
    connections.push(p.Connection(datasets[0], datasets[2], "#000", 2));
    connections.push(p.Connection(datasets[0], datasets[3], "#000", 2));
    connections.push(p.Connection(datasets[0], datasets[4], "#000", 9));
    
    for (var i=0, ii=datasets.length; i<ii; i++) {
    	datasets[i].attr({fill:"#9C9EDE", stroke:"#777", "fill-opacity": 1, "stroke-width": 1, cursor: "move"});
    	datasets[i].drag(move, dragger, up);
    	datasets[i].dblclick(function (event) {
    		fetchDatasets(this);
    	});
    	datasets[i].toFront();
    }
        
    // make a new graph
    graph = new Graph();

    // make some nodes
    var node1 = graph.newNode();
    node1.dataset = datasets[0];
    datasets[0].graphnode = node1;
    
    var node2 = graph.newNode();
    node2.dataset = datasets[1];
    datasets[1].graphnode = node2;
    
    var node3 = graph.newNode();
    node3.dataset = datasets[2];
    datasets[2].graphnode = node3;

    var node4 = graph.newNode();
    node4.dataset = datasets[3];
    datasets[3].graphnode = node4;

    var node5 = graph.newNode();
    node5.dataset = datasets[4];
    datasets[4].graphnode = node5;
    
    // connect them with an edge
    graph.newEdge(node1, node2);
    graph.newEdge(node1, node3);
    graph.newEdge(node2, node3);
    graph.newEdge(node4, node1);
    graph.newEdge(node5, node1);
    
    var layout = new Layout.ForceDirected(graph, 800, 200, 0.2);
    
    var renderer = new Renderer(10, layout,
		  function clear() {
		    // do nothing
		  },
		  function drawEdge(edge, p1, p2) {
		    // do nothing
		  },
		  function drawNode(node, pt) {
			  var xy = toScreen(pt);
		      for (var i = connections.length; i--;) {
		    	  p.Connection(connections[i]);
		      }
			  node.dataset.attr({cx: xy.x, cy: xy.y});
		  });
    renderer.start();
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
	blob.graphnode = node;
	
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

function getViewportSize() {
	 var viewportwidth;
	 var viewportheight;
	 
	 // the more standards compliant browsers (mozilla/netscape/opera/IE7) use window.innerWidth and window.innerHeight
	 
	 if (typeof window.innerWidth != 'undefined')
	 {
	      viewportwidth = window.innerWidth,
	      viewportheight = window.innerHeight
	 }
	 
	// IE6 in standards compliant mode (i.e. with a valid doctype as the first line in the document)

	 else if (typeof document.documentElement != 'undefined'
	     && typeof document.documentElement.clientWidth !=
	     'undefined' && document.documentElement.clientWidth != 0)
	 {
	       viewportwidth = document.documentElement.clientWidth,
	       viewportheight = document.documentElement.clientHeight
	 }
	 
	 // older versions of IE
	 
	 else
	 {
	       viewportwidth = document.getElementsByTagName('body')[0].clientWidth,
	       viewportheight = document.getElementsByTagName('body')[0].clientHeight
	 }	
	 
	 return new Vector(viewportwidth, viewportheight);
}


