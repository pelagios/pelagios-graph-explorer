/**
 * Connection between two nodes in the Pelagios graph
 * view, based on the Raphael 'graffle' demo at
 * http://raphaeljs.com/graffle.html  
 */
Raphael.fn.Connection = function (obj1, obj2, line, width) {
    if (obj1.line && obj1.from && obj1.to) {
        line = obj1;
        obj1 = line.from;
        obj2 = line.to;
    }
    var bb1 = obj1.getBBox(),
        bb2 = obj2.getBBox();

    var path = "M" + 
    	(bb1.x + bb1.width / 2) + " " +
    	(bb1.y + bb1.height / 2) + "L" + 
    	(bb2.x + bb2.width / 2) + " " + 
    	(bb2.y + bb2.height / 2);
    
    if (line && line.line) {
        line.bg && line.bg.attr({path: path});
        line.line.attr({path: path});
    } else {
        return {
            line: this.path(path).attr({"stroke-width":width, opacity:.3, stroke: "#777", fill: "none"}),
            from: obj1,
            to: obj2
        };
    }
};