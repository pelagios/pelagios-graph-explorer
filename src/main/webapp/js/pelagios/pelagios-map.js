// Map-related code
Pelagios.Map = function() {	
	var latlng = new google.maps.LatLng(40, 18);
	var options = {
		zoom: 3,
		center: latlng,
		mapTypeId: google.maps.MapTypeId.TERRAIN
	};
	
	this.map = new google.maps.Map(document.getElementById("map-panel"), options);
	
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
	
	this.polygons = new Array();
	
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
	
	this.polygons[name] = new google.maps.Polygon({
		paths: gCoords,
		strokeColor: fill,
		strokeOpacity: 0.8,
		strokeWeight: 1,
		fillColor: fill,
		fillOpacity: 0.5
	});
}

Pelagios.Map.prototype.showPolygon = function(name) {
	if (this.polygons[name])
		this.polygons[name].setMap(this.map);	
}

Pelagios.Map.prototype.hidePolygon = function(name) {
	if (this.polygons[name])
		this.polygons[name].setMap(null);	
}

Pelagios.Map.prototype.addMarker = function(name, lat, lon) {
	var marker = new google.maps.Marker({
		position: new google.maps.LatLng(lat, lon), 
		map: this.map, 
		title: name
	});  
}
