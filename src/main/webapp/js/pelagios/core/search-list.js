Pelagios.SearchList = {}

Pelagios.SearchList.PANEL_DIV_ID = "search-list";

Pelagios.SearchList.getInstance = function() {
	if (Pelagios.SearchList.instance)
		return Pelagios.SearchList.instance;
	
	var ids = new Array();
	
	var getBaseURL = function() {
		var baseURL = window.location.href;
		if (baseURL.indexOf('#') > -1)
			baseURL = baseURL.substring(0, baseURL.indexOf('#'));
		return baseURL;
	}
	
	var toID = function(uri) {
		return uri.substring(uri.lastIndexOf('/') + 1);
	}
	
	var refreshURL = function() {
		var url = getBaseURL() + "#";
		if (ids.length > 0) {
			url += 'pleiadesID=';
			for (var i=0; i<ids.length; i++) {
				url += ids[i] + ",";
			}
			url = url.substring(0, url.length - 1);
		} 
		window.location.href=url;
	}
	
	Pelagios.SearchList.instance = {
		add : function(place) {
			document.getElementById(Pelagios.SearchList.PANEL_DIV_ID)
				.innerHTML += '<span>' + place.label + '</span>'; 
			
			ids.push(toID(place.uri));
			refreshURL();
		},
			  
	    clear : function() {
	    	document.getElementById(Pelagios.SearchList.PANEL_DIV_ID)
				.innerHTML = "";
				
		    ids = new Array();
		    refreshURL();
	    }
	}
	
	return Pelagios.SearchList.instance;
}