var map;

function initialize() {
	var latlng = new google.maps.LatLng(38, 18);
	var myOptions = {
		zoom: 4,
		center: latlng,
		mapTypeId: google.maps.MapTypeId.TERRAIN
	};
	map = new google.maps.Map(document.getElementById("map"), myOptions);
}

function addPoint(lat, lng, label) {
	var pt = new google.maps.LatLng(lat, lng);
	var marker = new google.maps.Marker({
		position: pt,
		title:label
	});
	marker.setMap(map); 
}


function addPolygon(points, color) {
	var coords = new Array();
	for (var i=0,ii=points.length; i<ii; i++) {
		coords.push(new google.maps.LatLng(points[i][1], points[i][0]));
	}
	
	var polygon = new google.maps.Polygon({
		paths: coords,
		strokeColor: color,
		strokeOpacity: 0.8,
		strokeWeight: 2,
		fillColor: color,
		fillOpacity: 0.35
	});
	
	polygon.setMap(map);
}