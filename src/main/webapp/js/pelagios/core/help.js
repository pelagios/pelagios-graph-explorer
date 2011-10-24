Pelagios.Help = { }

Pelagios.Help.TEXT_SEARCHFORM =
	  "Start typing to search the Pleiades directory ancient places.";

Pelagios.Help.TEXT_SEARCHLIST =
	  "This panel shows the list of Places you are currently viewing. "
	+ "Click the 'CLEAR' button below to remove all places.";

Pelagios.Help.TEXT_DATAPANEL =
	  "The data panel lists the references to the places in the selected data set. "
	+ "Clicking on a reference will take you to the original data "
	+ "provider's page.";

Pelagios.Help.TEXT_DATASETS =
	  "This panel shows the available datasets. Use the checkboxes to filter the graph.";

Pelagios.Help.TEXT_GRAPH =
	  "The Graph displays the places you have searched for, and the " +
	  "datasets connecting them. Hover over a dataset (one of the coloured " +
	  "bubbles) to see more information. Click a dataset to see the references " +
	  "in the data view.";

Pelagios.Help.TEXT_MAP =
	  "The map shows the places you have searched for. Hovering over a place in " +
	  "the graph will highlight this place. Hovering over a dataset in the graph " +
	  "will show the 'geographical footprint' of that dataset on the map.";

Pelagios.Help.bubbles = new Array();
Pelagios.Help.shown = false;

Pelagios.Help.init = function() {
	var attachBubble = function(id, text, x, y) {
		var bubble = new Pelagios.InfoBubble(x, y);
		bubble.setText(text);
		
		$('#' + id).mouseover(function() {
			bubble.show();
		});
		
		$('#' + id).mouseout(function() {
			if (!Pelagios.Help.shown)
				bubble.hide();
		});
		
		Pelagios.Help.bubbles.push(bubble);
	}
	
	// Mouseover bubbles
	attachBubble('search-form', Pelagios.Help.TEXT_SEARCHFORM, 255, 215); 
	attachBubble('search-list', Pelagios.Help.TEXT_SEARCHLIST, 255, 300);
	attachBubble('data-panel', Pelagios.Help.TEXT_DATAPANEL, 255, 500);
	attachBubble('dataset-list', Pelagios.Help.TEXT_DATASETS, 255, 720);
	
	// 'Static' bubbles
	var graphBubble = new Pelagios.InfoBubble("50%", "10%", document.getElementById('graph-canvas'));
	graphBubble.setText(Pelagios.Help.TEXT_GRAPH);
	Pelagios.Help.bubbles.push(graphBubble);
	
	var mapBubble = new Pelagios.InfoBubble("50%", "10%", document.getElementById('map-panel'));
	mapBubble.setText(Pelagios.Help.TEXT_MAP);
	Pelagios.Help.bubbles.push(mapBubble);
}

Pelagios.Help.toggleHelp = function() {
	var b = Pelagios.Help.bubbles;
	
	for (var i=0; i<b.length; i++) {
		if (Pelagios.Help.shown) {
			b[i].hide();
		} else {
			b[i].show();
		}
	}
	
	Pelagios.Help.shown = !Pelagios.Help.shown;
}
