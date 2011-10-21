Pelagios.Graph.Local.Tooltip = function(label, rootSet, size) {
	this.info = "";
	if (rootSet && rootSet != label)
		this.info += '<span class="ttip-rootset">' + rootSet + '</span><br/>';

	this.info += '<span class="ttip-dataset">';
	
	var parts = label.split(':');
	this.info += parts[0] + '</span><br/>';
	
	
	if (parts.length > 1) {
		this.info += '<span class="ttip-subset">';
		
		for (var i=1; i<parts.length; i++) {
			this.info += parts[i] + ' ';
		}
		
		this.info += '</span><br/>';
	}
	
	this.info += '<span class="ttip-totalsize">This dataset contains <strong>' + size + '</strong> place references in total.</span>';
	this.help = '<span class="ttip-help">Click to view details in the Data View!</span>';
	
	this.tooltip = new Pelagios.InfoBubble();
	this.tooltip.setText(this.info + this.help);
	
	this.references = new Array();
}

Pelagios.Graph.Local.Tooltip.prototype.show = function(x, y) {
	this.tooltip.show(x + 20,y + 5);
}

Pelagios.Graph.Local.Tooltip.prototype.hide = function() {
	this.tooltip.hide();
}

Pelagios.Graph.Local.Tooltip.prototype.setReferencesTo = function(placename, count) {
	this.references.push('<li><strong>' + count + '</strong> references ' + 'to <strong>' + placename + '</strong></li>');
	
	var innerHTML = this.info + '<ul>';
	for (var i=0; i<this.references.length; i++) {
		innerHTML += this.references[i];
	}
	this.tooltip.setText(innerHTML + '</ul>' + this.help);
}
