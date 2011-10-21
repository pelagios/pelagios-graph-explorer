Pelagios.SearchList = {}

Pelagios.SearchList.PANEL_DIV_ID = "search-list";

Pelagios.SearchList.getInstance = function() {
	if (Pelagios.SearchList.instance)
		return Pelagios.SearchList.instance;
	
	Pelagios.SearchList.instance = {
		add : function(placeLabel) {
			document.getElementById(Pelagios.SearchList.PANEL_DIV_ID)
				.innerHTML += '<span>' + placeLabel + '</span>';
		},
			  
	    clear : function() {
	    	document.getElementById(Pelagios.SearchList.PANEL_DIV_ID)
				.innerHTML = "";
	    }
	}
	
	return Pelagios.SearchList.instance;
}