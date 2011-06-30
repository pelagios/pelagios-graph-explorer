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
