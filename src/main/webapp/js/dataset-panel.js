var vis, force, nodes, links;

window.onload = function () {
	initialize();
	fetchDatasets();

	nodes = new Array();
    links = new Array();
	
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
	if (parent) {
		links.push({source:parent.idx, target:newNode.idx, value:1});
	} else {
		links.push({source:newNode.idx, target:newNode.idx, value:1});
	}
	
    force.links(links);
    force.nodes(nodes);
    
    force.reset();
    vis.render();
}

function fetchDatasets(parent) {
	var url = "datasets/";
	if (parent)
		url += parent.nodeName;
		
	$.getJSON(url, function(data) {
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
	$.getJSON("datasets/" + dataset.nodeName + "/places", function(data) {
		for (var i=0; i<data.length; i++) {
			addPoint(data[i].lat, data[i].lon, data.label);
		}
	})
	.error(function(response) 
			alert("Something went wrong: " + response.responseText)
	);
}



