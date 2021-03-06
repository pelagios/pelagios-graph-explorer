window.onresize = function(event) {
	Pelagios.Graph.Dataset.getInstance().refresh();
	Pelagios.Graph.Local.getInstance().refresh();
}

window.onload = function() {	
	// Get references to the Big Five
	var dGraph = Pelagios.Graph.Dataset.getInstance();
	var lGraph = Pelagios.Graph.Local.getInstance();
	var map = Pelagios.Map.getInstance();
	var dataPanel = Pelagios.DataPanel.getInstance();
	var async = Pelagios.Async.getInstance();
	
	// Attach click handler to 'toggle map' button
    $("#toggle-map").click(function() {
    	map.setVisible(!map.isVisible())
    });
    
    // Attach click handler to 'toogle dataview' button
    $("#toggle-dataview").click(function() {
    	dataPanel.setVisible(!dataPanel.isVisible())
    });    
    
    // Attach click handler to local graph 'back' button
	$("#back-button").click(function(){
		lGraph.close();
		map.clear();
	});

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
	
	// Check if there are query params (and switch to embed mode if so)
	var search = Pelagios.Embed.getQueryParameter('search');
	var pleiadesID = Pelagios.Embed.getQueryParameter('pleiadesID');
	if (search) {
		Pelagios.Embed.searchPlaces(search);
	} else if (pleiadesID) {
		Pelagios.Embed.viewPlace("http://pleiades.stoa.org/places/" + pleiadesID);
	}
}
