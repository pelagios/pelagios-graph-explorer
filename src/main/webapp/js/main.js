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
					Pelagios.SearchList.getInstance().add(ui.item);
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
	
	// Init the dataset list
	Pelagios.DatasetList.getInstance().init();
	
	// Check if there are query params
	var search = Pelagios.Embed.getQueryParameter('search');
	var pleiadesIDs = Pelagios.Embed.getQueryParameter('pleiadesID');
	if (search) {
		Pelagios.Embed.searchPlaces(search);
	} else if (pleiadesIDs) {
		Pelagios.Embed.viewPlaces(pleiadesIDs);
	} else {
		// Launch with 'Athens' pre-set
		Pelagios.Embed.viewPlaces("579885");
	}
}

