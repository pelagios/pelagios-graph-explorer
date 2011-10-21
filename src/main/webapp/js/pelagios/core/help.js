Pelagios.Help = { }

Pelagios.Help.TEXT_SEARCHFORM =
	  "Start typing to search the Pleiades directory ancient places.";

Pelagios.Help.TEXT_SEARCHLIST =
	  "Displays the list of Places currently being searched. "
	+ "Click the 'CLEAR' button below to remove all places.";

Pelagios.Help.TEXT_DATAPANEL =
	  "Lists the references to the place in the selected data set. "
	+ "Clicking on a reference will take you to the original data "
	+ "provider's page.";

Pelagios.Help.init = function() {
	var attachBubble = function(id, text, x, y) {
		var bubble = new Pelagios.InfoBubble(x, y);
		bubble.setText(text);
		
		$('#' + id).mouseover(function() {
			bubble.show();
		});
		
		$('#' + id).mouseout(function() {
			bubble.hide();
		});
	}
	
	attachBubble('search-form', Pelagios.Help.TEXT_SEARCHFORM, 255, 215); 
	attachBubble('search-list',Pelagios.Help.TEXT_SEARCHLIST, 255, 300);
	attachBubble('data-panel', Pelagios.Help.TEXT_DATAPANEL, 255, 500);
}
