Pelagios.InfoBubble = { }
	
Pelagios.InfoBubble = function(x, y, element) {
	this.container = document.createElement("div");
	this.container.setAttribute("class", "pelagios-info-bubble");
	this.container.style.position = "absolute";
	if (x && y) {
		this.container.style.top = y;
		this.container.style.left = x;
	}
	
	this.box = document.createElement("div");
	this.box.setAttribute("class", "pelagios-info-bubble-inner");
	this.box.style.marginLeft = "12px";
	this.box.style.width = "270px";
	this.container.appendChild(this.box);
	
	this.hide();
	if (element) {
		element.appendChild(this.container);
	} else {
		document.body.appendChild(this.container);
	}
}

Pelagios.InfoBubble.prototype.setText = function(text) {
	this.box.innerHTML = text;
}

Pelagios.InfoBubble.prototype.show = function(x, y) {
	this.container.style.visibility = 'visible';
	if (x && y) {
		this.container.style.top = y + window.pageYOffset;
		this.container.style.left = x + window.pageXOffset;;
	}
}

Pelagios.InfoBubble.prototype.hide = function() {
	this.container.style.visibility = 'hidden';
}