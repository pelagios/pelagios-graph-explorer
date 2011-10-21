// Map-related code
Pelagios.Map = {}

Pelagios.Map.getWindowPosition = function() {
	function findPosX(obj) {
		var curleft = 0;
		if(obj.offsetParent) {
			while(obj.offsetParent) {
				curleft += obj.offsetLeft
				obj = obj.offsetParent;
			}
		} else if(obj.x) { 
		     curleft = obj.x;
		}
		return curleft;
	}
		 
	function findPosY(obj) {
		var curtop = 0;
		if(obj.offsetParent) {
			while(obj.offsetParent) {
				curtop += obj.offsetTop
				obj = obj.offsetParent;
			}
		} else if(obj.y) {
			curtop = obj.y;
		}
		return curtop;
	}

	var x = findPosX(document.getElementById('map-panel'));
	var y = findPosY(document.getElementById('map-panel'));
	return {x:x, y:y};
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

Pelagios.Map.getInstance = function() {
	if (Pelagios.DataPanel.instance)
		return Pelagios.Map.instance;
	
	var options = {
		zoom: 3,
		center: new google.maps.LatLng(40, 18),
		mapTypeId: google.maps.MapTypeId.TERRAIN
	};
	
	var map = 
		new google.maps.Map(document.getElementById("map-panel"), options);
	
	var hilightStyle = {
		strokeColor: "#ff0000",
		strokeOpacity: 0.9,
		strokeWeight: 2,
		fillColor: "#ff0000",
		fillOpacity: 0.4
	} 
	
	// All features (markers and polygons)
	var features = new Array();
	
	// Just those features which are currently shown on the map
	var shown = new Array();
	
	function addTooltip(name, feature) {
		google.maps.event.addListener(feature, 'mouseover', function(event) {
			var xy = Pelagios.Map.fromLatLngToXY(map, event.latLng);
			feature.tooltip = new Pelagios.Tooltip(name);
			feature.tooltip.show(xy.x, xy.y);
		});
		
		google.maps.event.addListener(feature, 'mouseout', function(event) {
			var xy = map.getProjection().fromLatLngToPoint(event.latLng);
			feature.tooltip.hide(); 
		});
		
		google.maps.event.addListener(feature, 'mousemove', function(event) {
			var xy = Pelagios.Map.fromLatLngToXY(map, event.latLng);
			feature.tooltip.show(xy.x, xy.y);
		});
	}
	
	function addClickListener(place, feature, callback) {
		google.maps.event.addListener(feature, 'click', function(event) {
			callback(place, event);
		});
	}
	
	Pelagios.Map.instance = {
		zoomToFeature : function(name) {
			var geom = features[name];
				
			if (geom) {
				if (geom.getBounds) {
					map.fitBounds(geom.getBounds());
				} else {
					map.setCenter(geom.position);
					map.setZoom(7);
				}
			}
		},
		
		zoomToArea : function(coords) {
			var gCoords = new Array();
			for (var i=0, ii=coords.length; i<ii; i++) {
				gCoords.push(new google.maps.LatLng(coords[i][1], coords[i][0]));
			}
			var poly = new google.maps.Polygon({ paths: gCoords });
			map.fitBounds(poly.getBounds());
		},
	
		clear : function() {
			for (var f in features) {
				this.hideFeature(f);
			}
		},
	
		showFeature : function(name) {
			if (features[name]) {
				shown[name] = name;
				features[name].setMap(map);
			}
		},
	
		hideFeature : function(name) {
			if (features[name]) {
				features[name].setMap(null);
				delete shown[name];
			}
		},
	
		addPolygon : function(name, coords, fill) {
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
			
			features[name] = new google.maps.Polygon(pOptions);
		},
	
		addMarker : function(name, lat, lon) {
			var marker = new google.maps.Marker({
				position: new google.maps.LatLng(lat, lon), 
				title: name
			});
			feature[name] = marker
		},
	
		addPlace : function(place, clickCallback) {
			var options = {
				"strokeColor": "#FF7800",
				"strokeOpacity": 1,
				"strokeWeight": 2,
				"fillColor": "#46461F",
				"fillOpacity": 0.25
			};
			
			var geom = new GeoJSON(place.geometry, options);
			geom.options = options;
			addTooltip(place.label, geom);
			
			if (clickCallback)
				addClickListener(place, geom, clickCallback);
			
			features[place.uri] = geom;
		},
	
		highlight : function(uri, highlighted) {
			var geom = features[uri];
			if (geom) {
				if (highlighted) {
					geom.setOptions(hilightStyle);
				} else {
					geom.setOptions(geom.options);
				}
			}
		}
	}
	
	return Pelagios.Map.instance;
}



