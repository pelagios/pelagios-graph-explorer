Pelagios.Loadmask = { }

Pelagios.Loadmask.getInstance = function() {
	if (Pelagios.Loadmask.instance)
		return Pelagios.Loadmask.instance;
	
	var div = document.createElement("div");
	div.style.visibility = "visible";
	div.innerHTML = "<img class=\"ajax-loader\" src=\"img/ajax-loader.gif\"/>";
	document.body.appendChild(div);
	
	var ctr = 0;
	
	Pelagios.Loadmask.instance = {		 
		show : function() {
			ctr++;
			div.style.visibility = "visible";
		},

		hide : function() {
			ctr--;
			if (ctr == 0)
				div.style.visibility = "hidden";
		}			
	}
	
	return Pelagios.Loadmask.instance;
}
