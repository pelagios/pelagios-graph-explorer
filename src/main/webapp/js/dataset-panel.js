var vis, force, nodes, links;

window.onload = function () {
	initialize();
	
	nodes = new Array();
	nodes.push({nodeName:"Ptolemy Machine", group:1, idx:0});
    links = new Array();
    links.push({source:0, target:0, value:1});
	
	var w = document.body.clientWidth,
	    h = document.body.clientHeight * 0.6,
	    colors = pv.Colors.category19();
	
	vis = new pv.Panel()
		.width(w)
		.height(h)
	    .fillStyle("white")
	    .event("mousedown", pv.Behavior.pan())
	    .event("mousewheel", pv.Behavior.zoom());
	
	force = vis.add(pv.Layout.Force)
	    .nodes(nodes)
	    .links(links)
	    .chargeConstant(-100)
	    .springConstant(0.09)
	    .springLength(70);
	
	force.link.add(pv.Line);
	
	force.node.add(pv.Dot)
	    .size(function(n)(n.linkDegree * 20) * Math.pow(this.scale, -1.5))
	    .fillStyle(function(n) n.fix ? "brown" : colors(n.group))
	    .strokeStyle(function() this.fillStyle().darker())
	    .lineWidth(1)
	    .title(function(d) d.nodeName)
	    .event("mousedown", pv.Behavior.drag())
	    .event("drag", force)
	    .event("dblclick", function(n) fetchDatasets(n))
    	.add(pv.Label)
    		.textAlign("center")
    		.textBaseline("bottom")
    		.textMargin(function(n) 4 + n.linkDegree / 2)
    		.text(function(n) n.nodeName);
	
	vis.render();
}

function addDataset(dataset, parent) {
	var newNode = {nodeName:dataset.name, group:1, idx:nodes.length};

	nodes.push(newNode);
	links.push({source:parent.idx, target:newNode.idx, value:1});
	
    force.links(links);
    force.nodes(nodes);
    
    force.reset();
    vis.render();
}

function fetchDatasets(parent) {
	$.getJSON("datasets/" + parent.nodeName, function(data) {
		if (data.length == 0) {
			fetchPlaces(parent);
		} else {
			for (var i=0; i<data.length; i++) {
				addDataset(data[i], parent);
			}
		}
	})
	.error(function(response) alert("Something went wrong: " + response.responseText));
}

function fetchPlaces(dataset) {
	$.getJSON("places/" + dataset.nodeName, function(data) {
		
	})
	.error(function(response) 
			alert("Something went wrong: " + response.responseText)
	);
}



