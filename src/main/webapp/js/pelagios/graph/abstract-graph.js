Pelagios.Graph = function() { }
Pelagios.Graph.Abstract = function() { 
	this.offset = {x: 0, y:0};
}

Pelagios.Graph.Abstract.prototype.toScreen = function(p, layout) {
	var viewport = Pelagios.getViewport();
	var g = layout.getBoundingBox();
		
	var graphSize = g.topright.subtract(g.bottomleft);
	var sx = p.subtract(g.bottomleft).divide(graphSize.x).x * viewport.x - this.offset.x;
	var sy = p.subtract(g.bottomleft).divide(graphSize.y).y * viewport.y - this.offset.y;

	return new Vector(sx, sy);
}

Pelagios.Graph.Abstract.prototype.fromScreen = function(s, layout) {
	var viewport = Pelagios.getViewport();
	var g = layout.getBoundingBox();
	
    var graphSize = g.topright.subtract(g.bottomleft);
    var px = ((s.x + this.offset.x) / viewport.x) * graphSize.x + g.bottomleft.x;
    var py = ((s.y + this.offset.y) / viewport.y) * graphSize.y + g.bottomleft.y;
    return new Vector(px, py);
}

Pelagios.Graph.Abstract.prototype.shiftCenter = function(dx, dy) {
	var x = this.offset.x + dx;
	var y = this.offset.y + dy;
	this.offset = {x: x, y: y};
}