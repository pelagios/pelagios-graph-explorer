// PELAGIOS-specific drawing elements, implemented as a 'namespaced' Raphael extension.
Raphael.fn.pelagios = {
		
	// A graph node that represents a Pelagios data set
	dataset : function(arg0, arg1, arg2, arg3) {
		if (arg0.size) {
			// arg0 -> set, arg1 -> x, arg2 -> y
			arg0[0].attr({cx: arg1, cy: arg2});
			arg0[1].attr({x: arg1, y: arg2 + arg0.size + 10});
			if (arg0[2])
				arg0[2].attr({x: arg1, y: arg2 + arg0.size + 24});
			if (arg0[3])
				arg0[3].attr({x: arg1, y: arg2 + arg0.size + 38});
			
			if (arg0.selection)
				arg0.selection.attr({cx: arg1, cy: arg2});
			return;	
		} else {
			// arg0 -> name, arg1 -> size, arg2 -> records, arg3 -> places
			var raphael = this;
		    var s = this.set();
			s.size = arg1;
		    s.push(
		    	this.ellipse(this.width / 2, this.height / 2, arg1, arg1)
					.attr({
						"fill" : "#9C9EDE", 
						"stroke" : "#fff", 
						"stroke-width" : 2}));
		    
			s[0].mouseover(function(event) {
			    this.animate({
			    	"scale" : "1.3, 1.3",
			    }, 350, "bounce");
			    
			    s.push(raphael.text(this.attr("cx"), this.attr("cy") + s.size + 24, arg2 + " Records"));
			    s.push(raphael.text(this.attr("cx"), this.attr("cy") + s.size + 38, arg3 + " Places"));
			});
			s[0].mouseout(function (event) {
			    this.animate({
			    	"scale" : "1.0, 1.0",
			    }, 350, "bounce");
			    s.pop().remove();
			    s.pop().remove();
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
					"opacity" : 0.2,
					"stroke": "#777",
					"fill" : "none"}).toBack(),
				from: obj1,
				to: obj2
			};
		}
	}

}