// Utility methods needed for embed functionality
Pelagios.Embed = function() { }

//Utility function to get URL query params
//http://www.zrinity.com/developers/code_samples/code.cfm/CodeID/59/JavaScript/Get_Query_String_variables_in_JavaScript
Pelagios.Embed.getQueryParameter = function(param) {
	var query = window.location.href;
	if (query.indexOf('#') < 0)
		return;

	query = query.substring(query.indexOf('#') + 1);
	var vars = query.split("&");
	for (var i=0; i<vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == param) {
			return pair[1];
		}
	} 	
}

Pelagios.Embed.searchPlaces = function(query) {
	var terms = query.split(",");
	var places = new Array();
	
	for (var i=0; i<terms.length; i++) {
		Pelagios.Async.getInstance().search(terms[i], function(i){
			return function(data) {
				var p = data[0];
				for (var j=0; j<data.length; j++) {
					if (data[j].label == terms[i])
						p = data[j];
				}
				
				places.push(p);
				if (places.length == terms.length)
					Pelagios.Embed.switchView(places);
			}
		}(i));
	}
}

Pelagios.Embed.viewPlaces = function(pleiadesIDs) {
	var ids = pleiadesIDs.split(",");
	var places = new Array();
	
	var async = Pelagios.Async.getInstance();
	for (var i=0; i<ids.length; i++) {
		async.getPlace("http://pleiades.stoa.org/places/" + ids[i], function(i){
			return function(data) {
				places.push(data);
				if (places.length == ids.length)
					Pelagios.Embed.switchView(places);
			}
		}(i));
	}
}

Pelagios.Embed.switchView = function(places) {
	var lGraph = Pelagios.Graph.Local.getInstance();
	lGraph.show();
	for (var i=0, ii=places.length; i<ii; i++) {
		var map = Pelagios.Map.getInstance();
		map.addPlace(places[i]);
		map.showFeature(places[i].uri);
		Pelagios.Async.getInstance().occurences(places[i], lGraph, map);
		Pelagios.SearchList.getInstance().add(places[i]);
	}
}