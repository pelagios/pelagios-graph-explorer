/**
 * A graph node that represents a Pelagios data set
 */
Raphael.fn.Dataset = function(size, name) {
	this.name = name;
	
	return this
		.ellipse(0, 0, size, size)
		.attr({
			"fill" : "#9C9EDE", 
			"stroke" : "#777", 
			"fill-opacity" : 1,
			"stroke-width" : 1,
			"cursor": "move"});
}