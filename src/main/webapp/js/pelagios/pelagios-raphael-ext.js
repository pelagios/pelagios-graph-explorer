// PELAGIOS-specific drawing elements, implemented as a 'namespaced' Raphael extension.
Raphael.fn.pelagios = {
		
	// A graph node that represents a Pelagios data set
	dataset : function(arg0, arg1, arg2) {
		if (arg2) {
			// arg0 -> set, arg1 -> x, arg2 -> y
			arg0[0].attr({cx: arg1, cy: arg2});
			arg0[1].attr({x: arg1, y: arg2 + arg0.size + 10});
			return;
		} else {
			// arg0 -> name, arg1 -> size;
		    var s = this.set();
			s.size = arg1;
		    s.push(
		    	this.ellipse(this.width / 2, this.height / 2, arg1, arg1)
					.attr({
						"fill" : "#9C9EDE", 
						"stroke" : "#777", 
						"fill-opacity" : 1,
						"stroke-width" : 1,
						"cursor": "move"}));
		    
			s[0].mouseover(function (event) {
			    this.animate({
			    	"scale" : "1.25, 1.25",
			    	"stroke" : "#555",
					"stroke-width" : 2
			    }, 100);
			});
			s[0].mouseout(function (event) {
			    this.animate({
			    	"scale" : "1.0, 1.0",
				    "stroke" : "#777",
					"stroke-width" : 1
			    }, 100);
			});

		    s.push(this.text(this.width / 2, this.height / 2, arg0));
			return s;
		}
	},

	// Connection between two graph nodes, based on the Raphael 'graffle'
	// demo at http://raphaeljs.com/graffle.html  
	connection : function (obj1, obj2, line, width) {
		if (obj1.line && obj1.from && obj1.to) {
			line = obj1;
			obj1 = line.from;
			obj2 = line.to;
		}
		
		var bb1 = obj1.getBBox(), bb2 = obj2.getBBox();

		var path = "M" + 
    		(bb1.x + bb1.width / 2) + " " +
    		(bb1.y + bb1.height / 2) + "L" + 
    		(bb2.x + bb2.width / 2) + " " + 
    		(bb2.y + bb2.height / 2);
    
		if (line && line.line) {
			line.line.attr({path: path});
		} else {
			return {
				line: this.path(path).attr({
					"stroke-width" : width,
					"opacity" : .3,
					"stroke": "#777",
					fill: "none"}).toBack(),
				from: obj1,
				to: obj2
			};
		}
	}

}