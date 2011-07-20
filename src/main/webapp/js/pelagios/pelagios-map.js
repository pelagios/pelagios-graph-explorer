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
	
	/*
	var noLabelStyle = [ { featureType: "administrative", elementType: "labels", stylers: [ { visibility: "off" } ] },{ featureType: "administrative.province", elementType: "all", stylers: [ { visibility: "off" } ] },{ featureType: "water", elementType: "labels", stylers: [ { visibility: "off" } ] },{ featureType: "road", elementType: "all", stylers: [ { visibility: "off" } ] },{ featureType: "poi", elementType: "all", stylers: [ { visibility: "off" } ] },{ featureType: "transit", elementType: "all", stylers: [ { visibility: "off" } ] },{ featureType: "administrative.locality", elementType: "all", stylers: [ { visibility: "off" } ] },{ featureType: "administrative.province", elementType: "all", stylers: [ { visibility: "off" } ] } ]
	
	var styledMapOptions = {
		map: this.map,
		name: "no-labels",
	}

	var noLabelsMapType = new google.maps.StyledMapType(noLabelStyle,styledMapOptions);
	this.map.mapTypes.set('no-labels', noLabelsMapType);
	this.map.setMapTypeId('no-labels');
	*/ 
	
	// All polygons
	this.polygons = new Array();
	
	// All GeoJSON features
	this.features = new Array();
	
	// Just those which are currently shown on the map
	this.shown = new Array();
	
	var map = this;
	$("#dialog").bind("dialogresize", function(event, ui) {
		map.refresh();
	});
	$("#dialog").bind("dialogopen", function(event, ui) {
		map.refresh();
	});
}

Pelagios.Map.prototype.setVisible = function(visible) {
	if (visible) {
		$("#dialog").dialog("open");
		for (var p in this.shown) {
			this.showPolygon(p);
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
	
	this.polygons[name] = new google.maps.Polygon(pOptions);
}

Pelagios.Map.prototype.showPolygon = function(name) {
	if (this.polygons[name]) {
		this.shown[name] = name;
		this.polygons[name].setMap(this.map);
	}
}

Pelagios.Map.prototype.hidePolygon = function(name) {
	if (this.polygons[name]) {
		delete this.shown[name];
		this.polygons[name].setMap(null);
	}
}

Pelagios.Map.prototype.addMarker = function(name, lat, lon) {
	var marker = new google.maps.Marker({
		position: new google.maps.LatLng(lat, lon), 
		map: this.map, 
		title: name
	});  
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
	geom.tooltip = new Pelagios.Tooltip(name);
	geom.setMap(this.map);	
	
	this.features[name] = geom;
}

Pelagios.Map.prototype.setHighlight = function(name, highlighted) {
	var geom = this.features[name];
	if (geom) {
		if (highlighted) {
			geom.setOptions(this.hilightStyle);
		} else {
			geom.setOptions(geom.options);
		}
	}
}

Pelagios.Map.prototype.zoomTo = function(name) {
	var geom = this.features[name];
	if (!geom)
		geom = this.polygons[name]; 
		
	if (geom) {
		this.map.fitBounds(geom.getBounds());
	}
}
