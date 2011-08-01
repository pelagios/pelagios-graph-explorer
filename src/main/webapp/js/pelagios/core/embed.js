// Utility methods needed for embed functionality
Pelagios.Embed = function() { }

//Utility function to get URL query params
//http://www.zrinity.com/developers/code_samples/code.cfm/CodeID/59/JavaScript/Get_Query_String_variables_in_JavaScript
Pelagios.Embed.getQueryParameter = function(param) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i=0; i<vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == param) {
			return pair[1];
		}
	} 	
}

Pelagios.Embed.searchPlaces = function(query, async, personalGraph, map) {
	var terms = query.split(",");
	var places = new Array();
	
	for (var i=0; i<terms.length; i++) {
		async.search(terms[i], function(i){
			return function(data) {
				var p = data[0];
				for (var j=0; j<data.length; j++) {
					if (data[j].label == terms[i])
						p = data[j];
				}
				
				places.push(p);
				if (places.length == terms.length)
					Pelagios.Embed.switchView(places, async, personalGraph, map);
			}
		}(i));
	}
}

Pelagios.Embed.switchView = function(places, async, personalGraph, map) {
	personalGraph.show();
	for (var i=0, ii=places.length; i<ii; i++) {
		map.addPlace(places[i]);
		async.occurences(places[i], personalGraph, map);
	}
}