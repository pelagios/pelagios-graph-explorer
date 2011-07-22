Pelagios.Tooltip = function(text) {
	this.div = document.createElement("div");
	this.div.innerHTML = text;
	this.div.setAttribute("class", "pelagios-tooltip");
	this.div.style.position = "absolute";
	this.hide();
	document.body.appendChild(this.div);
}

Pelagios.Tooltip.OFFSET_X = 15;
Pelagios.Tooltip.OFFSET_Y = 5;

Pelagios.Tooltip.prototype.show = function(x, y) {
	this.div.style.left = x + Pelagios.Tooltip.OFFSET_X;
	this.div.style.top = y + Pelagios.Tooltip.OFFSET_Y;
	this.div.style.visibility = "visible";
}

Pelagios.Tooltip.prototype.hide = function() {
	this.div.style.visibility = "hidden";
}

Pelagios.Tooltip.prototype.remove = function() {
	// TODO
}