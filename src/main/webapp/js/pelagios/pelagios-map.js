// Map-related code
Pelagios.Map = function() {	
	var latlng = new google.maps.LatLng(40, 18);
	var options = {
		zoom: 3,
		center: latlng,
		mapTypeId: google.maps.MapTypeId.TERRAIN
	};
	
	this.map = new google.maps.Map(document.getElementById("map-panel"), options);
	
	this.hilightStyle = {
		strokeColor: "#ff0000",
		strokeOpacity: 0.9,
		strokeWeight: 2,
		fillColor: "#ff0000",
		fillOpacity: 0.4
	} 
	
	// All features (markers and polygons)
	this.features = new Array();
	
	// Just those features which are currently shown on the map
	this.shown = new Array();
	
	var self = this;
	$("#dialog").bind("dialogresize", function(event, ui) {
		self.refresh();
	});
	$("#dialog").bind("dialogopen", function(event, ui) {
		self.refresh();
	});
	
	var map = this.map;
	this.addTooltip = function(name, feature) {
		google.maps.event.addListener(feature, 'mouseover', function(event) {
			var xy = Pelagios.Map.fromLatLngToXY(map, event.latLng);
			feature.tooltip = new Pelagios.Tooltip(name);
			feature.tooltip.show(xy.x, xy.y);
		});
		
		google.maps.event.addListener(feature, 'mouseout', function(event) {
			var xy = map.getProjection().fromLatLngToPoint(event.latLng);
			feature.tooltip.hide(); 
		});
	}
}

Pelagios.Map.getWindowPosition = function() {
	var top = document.getElementById('dialog').parentNode.style.top;
	top = top.substring(0, top.length - 2);
	
	var left = document.getElementById('dialog').parentNode.style.left;
	left = left.substring(0, left.length - 2);	
	
	return {x:parseInt(left), y:parseInt(top)};
}

Pelagios.Map.fromLatLngToXY = function(map, latLng) {
	var topRight = map.getProjection().fromLatLngToPoint(map.getBounds().getNorthEast());
	var bottomLeft = map.getProjection().fromLatLngToPoint(map.getBounds().getSouthWest());
	var scale = Math.pow(2, map.getZoom());
	var worldPoint = map.getProjection().fromLatLngToPoint(latLng);
	
	var pos = Pelagios.Map.getWindowPosition();
	return new google.maps.Point(
		(worldPoint.x - bottomLeft.x) * scale + pos.x,
		(worldPoint.y - topRight.y) * scale + pos.y);
}

Pelagios.Map.prototype.setVisible = function(visible) {
	if (visible) {
		$("#dialog").dialog("open");
		for (var f in this.shown) {
			this.showFeature(f);
		}
	} else {
		$("#dialog").dialog("close");		
	}
}

Pelagios.Map.prototype.isVisible = function() {
	return $("#dialog").parents(".ui-dialog").is(":visible");
}

Pelagios.Map.prototype.refresh = function() {
	google.maps.event.trigger(this.map, 'resize');
}

Pelagios.Map.prototype.zoomTo = function(name) {
	var geom = this.features[name];
	if (!geom)
		geom = this.polygons[name]; 
		
	if (geom) {
		this.map.fitBounds(geom.getBounds());
	}
}

Pelagios.Map.prototype.clear = function() {
	for (var f in this.features) {
		this.hideFeature(f);
	}
}

Pelagios.Map.prototype.showFeature = function(name) {
	if (this.features[name]) {
		this.shown[name] = name;
		this.features[name].setMap(this.map);
	}
}

Pelagios.Map.prototype.hideFeature = function(name) {
	if (this.features[name]) {
		delete this.shown[name];
		this.features[name].setMap(null);
	}
}

Pelagios.Map.prototype.addPolygon = function(name, coords, fill) {
	var gCoords = new Array();
	for (var i=0, ii=coords.length; i<ii; i++) {
		gCoords.push(new google.maps.LatLng(coords[i][1], coords[i][0]));
	}
	
	var pOptions = {
		paths: gCoords,
		strokeColor: fill,
		strokeOpacity: 0.8,
		strokeWeight: 1,
		fillColor: fill,
		fillOpacity: 0.5
	} 
	
	this.features[name] = new google.maps.Polygon(pOptions);
}

Pelagios.Map.prototype.addMarker = function(name, lat, lon) {
	var marker = new google.maps.Marker({
		position: new google.maps.LatLng(lat, lon), 
		title: name
	});
	this.feature[name] = marker
}

Pelagios.Map.prototype.addGeoJSON = function(name, json) {
	var options = {
		"strokeColor": "#FF7800",
		"strokeOpacity": 1,
		"strokeWeight": 2,
		"fillColor": "#46461F",
		"fillOpacity": 0.25
	};
	
	var geom = new GeoJSON(json, options);
	geom.options = options;
	this.addTooltip(name, geom);
	this.features[name] = geom;
}

Pelagios.Map.prototype.highlight = function(name, highlighted) {
	var geom = this.features[name];
	if (geom) {
		if (highlighted) {
			geom.setOptions(this.hilightStyle);
		} else {
			geom.setOptions(geom.options);
		}
	}
}