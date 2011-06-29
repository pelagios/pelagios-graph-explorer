function initialize() {
	var latlng = new google.maps.LatLng(38, 18);
	var myOptions = {
		zoom: 4,
		center: latlng,
		mapTypeId: google.maps.MapTypeId.TERRAIN
	};
	var map = new google.maps.Map(document.getElementById("map"), myOptions);
}
