window.onresize = function(event) {
	Pelagios.Graph.Local.getInstance().refresh();
}

window.onload = function() {	
	// Get references to the Big Five
	var lGraph = Pelagios.Graph.Local.getInstance();
	lGraph.show();
	var map = Pelagios.Map.getInstance();
	var dataPanel = Pelagios.DataPanel.getInstance();
	var async = Pelagios.Async.getInstance();

	// Initialize auto-completion search field
	$("#searchfield").autocomplete({
		source: function(textfield, callback) { 
					async.search(textfield.term, callback); 
				},
				
		select: function(event, ui) {
					lGraph.show();
					map.addPlace(ui.item);
					map.showFeature(ui.item.uri);
					map.zoomToFeature(ui.item.uri);
					async.occurences(ui.item);
				}
	});
	
	$("#searchfield").focus(function() {
		$(this).val(''); return false;
	});

	// Init the help system
	Pelagios.Help.init();
	
	// Check if there are query params (and switch to embed mode if so)
	var search = Pelagios.Embed.getQueryParameter('search');
	var pleiadesID = Pelagios.Embed.getQueryParameter('pleiadesID');
	if (search) {
		Pelagios.Embed.searchPlaces(search);
	} else if (pleiadesID) {
		Pelagios.Embed.viewPlace("http://pleiades.stoa.org/places/" + pleiadesID);
	}
}

