// An attempt to clean up the selection code mess in the dataset graph
Pelagios.SelectionManager = function(raphael, async) {
	this.raphael = raphael;
	this.async = async;
	this.selectedNodes = new Array();
	this.maxOverlapWeight = 0;
}

Pelagios.SelectionManager.prototype.toggleSelect = function(node) {
	node.selected = !node.selected;
	if (node.selected) {
		// Hightlight node on screen
		var cx = node.set[0].attr("cx");
		var cy = node.set[0].attr("cy");
		var size = node.set.size + 8;
		node.set.selection = this.raphael.ellipse(cx, cy, size, size)
			.attr({
				"stroke" : "#FF8000",
				"stroke-width" : 4,
				"opacity" : 0.8,
				"stroke-dasharray" : "-"
			});
		
		// Fetch link data for this node
		this.fetchLinkData(node);
		
		// Store in 'selectedNodes' array
		this.selectedNodes[node.name] = node;
	} else {		
		// Remove highlight on screen
		node.set.selection.remove();
		
		// Remove links connections on screen
		var links = this.getLinksFor(node);
		for (var i=0, ii=links.length; i<ii; i++) {
			links[i].line.remove;
		}
		
		// Remove from 'selectedNodes' array
		delete this.selectedNodes[node.name];
	}
}

Pelagios.SelectionManager.prototype.deselectAll = function() {
	for (var sel in this.selectedNodes) {
		this.toggleSelect(this.selectedNodes[sel]);
	}
}

Pelagios.SelectionManager.prototype.getLinksFor = function(node) {
	if (!this.selectedNodes[node.name])
		return;
	
	var allLinks = new Array();
	
	// Outbound links
	var outbound = node.set.links;
	for (var i=0, ii=outbound.length; i<ii; i++) {
		allLinks.push(outbound[i]);
	}
	
	// Inbound links
	var otherNodes = new Array()
	for (var n in this.selectedNodes) {
		if (n != node.name)
			otherNodes.push(this.selectedNodes[n]);
	}
	
	for (var i=0, ii=otherNodes.length; i<ii; i++) {
		if (otherNodes[i].links) {
			for (var j=0, jj=otherNodes[i].links.length; j<jj; j++) {
				if (otherNodes[i].links[j].to == node) {
					allLinks.push(otherNodes[i].links[j]);
				}	
			}
		}
	}
	
	return allLinks;
}

Pelagios.SelectionManager.prototype.getLink = function(srcNode, destNode) {
	
}

Pelagios.SelectionManager.prototype.fetchLinkData = function(node) {
	for (var n in this.selectedNodes) {
		this.async.computeOverlap(node, this.selectedNodes[n], this);
	}
}

Pelagios.SelectionManager.prototype.setLinkWeight = function(srcNode, destNode, weight) {
	if (weight > this.maxOverlapWeight) {
		this.maxOverlapWeight = weight;
		this.normalizeLinkWeights();
	}
	
	var link = this.getLink(srcNode, destNode);
	if (link) {
		// Link already on screen - change line width
		link.weight = weight;
		var strokeWidth = 12 * link.weight / this.maxOverlapWeight;
		if (strokeWidth < 2)
			strokeWidth = 2;
		link.line.attr({ "stroke-width" : strokeWidth });
	} else {
		// New link - draw
		var fromX = srcNode.set[0].attr("cx");
		var fromY = srcNode.set[0].attr("cy");
		var toX = destNode.set[0].attr("cx");
		var toY = destNode.set[0].attr("cy");
		
		link = {
			"from" : srcNode,
			"to" : destNode,
			"line" : this.raphael.path(
				"M" + fromX + " " + fromY + 
				"L" + toX + " " + toY)
			.attr({
				"stroke" : "#FF8000",
				"stroke-width" : 12 * weight / this.maxOverlapWeight,
				"opacity" : 0.8,
				"stroke-dasharray" : "-"
			}).toBack(),
			"weight" : weight
		}
		
		// Links are always attached to the source nodes!
		if (!srcNode.set.links)
			srcNode.set.links = new Array();
		
		srcNode.set.links.push(link);
	}	
}

Pelagios.SelectionManager.prototype.normalizeLinkWeights = function() {
	/*
	var srcLinks = srcNode.set.links;
	var destLinks = destNode.set.links;
	var link = null;
	
	if (srcLinks) {
		for (var i=0, ii=srcLinks.length; i<ii; i++) {
			if ((srcLinks[i].from == srcNode) && (srcLinks[i].to == destNode)) {
				link = srcLinks[i];
				break;
			}
		}
	}
	
	if ((link == null) && (destLinks)) {
		for (var i=0, ii=destLinks.length; i<ii; i++) {
			if ((destLinks[i].from == srcNode) && (destLinks[i].to == destNode)) {
				link = destLinks[i];
				break;
			}
		}		
	}
	 */
}

