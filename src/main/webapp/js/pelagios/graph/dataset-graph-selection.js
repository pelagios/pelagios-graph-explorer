// An attempt to clean up the selection code mess in the dataset graph
Pelagios.SelectionManager = function(raphael) {
	this.raphael = raphael;
	this.selectedNodes = new Array();
	this.maxOverlapWeight = 0;
	
	this.getWidthFromWeigth = function(weight) {
		var w = 12 * weight / this.maxOverlapWeight;
		if (w < 2)
			w = 2;
		return w;
	}
}

Pelagios.SelectionManager.prototype.isSelected = function(name) {
	if (this.selectedNodes[name]) {
		return true;
	}
	return false;
}

Pelagios.SelectionManager.prototype.toggleSelect = function(node) {
	node.selected = !node.selected;
	if (node.selected) {
		// Highlight node on screen
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
		
		// Show on map
		Pelagios.Map.getInstance().showFeature(node.name);
		
		// Show in data view
		Pelagios.DataPanel.getInstance().showDatasetInfo({
			name: node.name,
			geoAnnotations: node.records,
			places: node.place
		});
		
		// Fetch link data for this node
		this.fetchLinkData(node);
		
		// Store in 'selectedNodes' array
		this.selectedNodes[node.name] = node;
	} else {		
		// Remove highlight on screen
		node.set.selection.remove();
		
		// Remove from map
		this.map.hideFeature(node.name);
		
		// Remove links on screen
		var links = this.getLinksFor(node);
		for (var i=0, ii=links.length; i<ii; i++) {
			links[i].line.remove();
			links[i].tooltip.remove();
		}
		
		// Remove from 'selectedNodes' array
		delete this.selectedNodes[node.name];
		
		// Re-normalize
		this.maxOverlapWeight = 0;
		var allLinks = this.getAllLinks();
		for (var i=0, ii=allLinks.length; i<ii; i++) {
			if (allLinks[i].weight > this.maxOverlapWeight)
				this.maxOverlapWeight = allLinks[i].weight;
		}
		
		this.normalizeLineWidths();
	}
}

Pelagios.SelectionManager.prototype.deselectAll = function() {
	Pelagios.Map.getInstance().clear();
	for (var sel in this.selectedNodes) {
		this.toggleSelect(this.selectedNodes[sel]);
	}
	
	Pelagios.DataPanel.getInstance().clear();
}

Pelagios.SelectionManager.prototype.getLinksFor = function(node) {
	if (!this.selectedNodes[node.name])
		return;
	
	var allLinks = new Array();
	
	// Outbound links
	var outbound = node.set.links;
	if (outbound) {
		for (var i=0, ii=outbound.length; i<ii; i++) {
			allLinks.push(outbound[i]);
		}
	}
	
	// Inbound links
	var otherNodes = new Array()
	for (var n in this.selectedNodes) {
		if (n != node.name)
			otherNodes.push(this.selectedNodes[n]);
	}
	
	for (var i=0, ii=otherNodes.length; i<ii; i++) {
		if (otherNodes[i].set.links) {
			for (var j=0, jj=otherNodes[i].set.links.length; j<jj; j++) {
				if (otherNodes[i].set.links[j].to == node) {
					allLinks.push(otherNodes[i].set.links[j]);
				}	
			}
		}
	}
	
	return allLinks;
}

Pelagios.SelectionManager.prototype.getAllLinks = function() {
	var allLinks = new Array();
	for (var n in this.selectedNodes) {
		// All links originating from this node
		var linksForN = this.selectedNodes[n].set.links;
		if (linksForN) {
			for (var i=0, ii=linksForN.length; i<ii; i++) {
				if (this.selectedNodes[linksForN[i].to.name])
					allLinks.push(linksForN[i]);
			}
		}
	}
	return allLinks;
}

Pelagios.SelectionManager.prototype.fetchLinkData = function(node) {
	for (var n in this.selectedNodes) {
		Pelagios.Async.getInstance().intersect(node, this.selectedNodes[n], this);
	}
}

Pelagios.SelectionManager.prototype.setLink = function(arg0, arg1, arg2) {
	if (arg1) {		
		// arg0 -> srcNode, arg1 -> destNode, arg2 -> data	
		if (arg2.commonPlaces.length > this.maxOverlapWeight) {
			this.maxOverlapWeight = arg2.commonPlaces.length;
			this.normalizeLineWidths();
		}
		
		var fromX = arg0.set[0].attr("cx");
		var fromY = arg0.set[0].attr("cy");
		var toX = arg1.set[0].attr("cx");
		var toY = arg1.set[0].attr("cy");
		
		var link = {
			"from" : arg0,
			"to" : arg1,
			"line" : this.raphael.path(
				"M" + fromX + " " + fromY + 
				"L" + toX + " " + toY)
			.attr({
				"stroke" : "#FF8000",
				"stroke-width" : this.getWidthFromWeigth(arg2.commonPlaces.length),
				"opacity" : 0.8,
				"stroke-dasharray" : "-"
			}).toBack(),
			"tooltip" : new Pelagios.Tooltip(
					arg2.commonPlaces.length + " places in common",
					(fromX + toX) / 2,
					(fromY + toY) / 2),
			"weight" : arg2.commonPlaces.length
		}
		
		// Add mouseover information to link line
		var map = Pelagios.Map.getInstance();		
		link.line.mouseover(function(event) {
			link.tooltip.show(event.clientX, event.clientY);
			map.showFeature(arg0.name + "-" + arg1.name);
		});
		
		link.line.mouseout(function (event) {
			link.tooltip.hide();
			map.hideFeature(arg0.name + "-" + arg1.name);
		});
		
		link.line.click(function (event) {
			map.clear();
			map.setVisible(true);
			var places = arg2.commonPlaces;
			for (var i=0, ii=places.length; i<ii; i++) {
				map.addGeoJSON(places[i].label, places[i].geometry);
				map.showFeature(places[i].label);
			}
			map.zoomTo(arg0.name);
		});
		
		// Links are always attached to the source nodes!
		if (!arg0.set.links)
			arg0.set.links = new Array();
		
		arg0.set.links.push(link);
		
		// Add to map
		map.addPolygon(arg0.name + "-" + arg1.name, arg2.footprint, "#FF8000");
	} else {
		// arg0 -> line
		arg0.line.animate({ "stroke-width" : this.getWidthFromWeigth(arg0.weight) }, 500);
	}
}

Pelagios.SelectionManager.prototype.normalizeLineWidths = function() {
	var allLinks = this.getAllLinks();	
	for (var i=0, ii=allLinks.length; i<ii; i++) {
		this.setLink(allLinks[i]);
	}
}

