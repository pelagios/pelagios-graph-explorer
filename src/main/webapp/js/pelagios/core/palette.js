Pelagios.Palette = {}

Pelagios.Palette.getInstance = function() {
	if (Pelagios.Palette.instance)
		return Pelagios.Palette.instance;
	
	var bright = ["#9c9ede", "#cedb9c", "#e7cb94", "#e7969c", "#de9ed6"];
	var dark = {
		"#9c9ede" : "#4a5584",
		"#cedb9c" : "#637939",
		"#e7cb94" : "#8c6d31",
		"#e7969c" : "#843c39",
		"#de9ed6" : "#7b4173"
	}
	
	var colors = new Array();
    var ctr = -1;
    
    Pelagios.Palette.instance = {
		getColor : function(name) {
			// Return color stored for this name
			if (colors[name])
				return colors[name];
			
			// If not yet in list, pick the next...
			ctr++;
			if (ctr > bright.length)
				ctr = 0;
			
			// and store
			colors[name] = bright[ctr];
			
			return bright[ctr];
		},

		darker : function(color) {
			return dark[color];
		}   		
    }
    
    return Pelagios.Palette.instance;
}