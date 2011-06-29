var graph;
var nodes;
var links;
var force;
var vis;

var move, dragger, up;

window.onload = function () {
	nodes = new Array();
	nodes.push({nodeName:"Ptolemy Machine", group:1, idx:0});
	// nodes.push({nodeName:"Test2", group:1, idx:1});
	// nodes.push({nodeName:"Test3", group:2, idx:2});
	// nodes.push({nodeName:"Test4", group:2, idx:3});
    
    links = new Array();
    links.push({source:0, target:0, value:1});
 //  links.push({source:0, target:2, value:1});
 //  links.push({source:0, target:3, value:5});
 //  links.push({source:2, target:3, value:20});
	
	var w = document.body.clientWidth,
	    h = document.body.clientHeight,
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
	    .size(function(n)(n.linkDegree * 12) * Math.pow(this.scale, -1.5))
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


