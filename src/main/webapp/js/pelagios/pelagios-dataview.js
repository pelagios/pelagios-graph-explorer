// Code related to the data view panel
Pelagios.DataPanel = function() {
	
}

Pelagios.DataPanel.prototype.setVisible = function(visible) {
	if (visible) {
		$("#data-panel").dialog("open");
	} else {
		$("#data-panel").dialog("close");		
	}
}

Pelagios.DataPanel.prototype.isVisible = function() {
	return $("#data-panel").parents(".ui-dialog").is(":visible");
}

Pelagios.DataPanel.prototype.setGeoAnnotations = function(annotations) {
	var innerHTML = '';
	for (var i=0, ii=annotations.length; i<ii; i++) {
		innerHTML += '<p><a target="_blank" href="' + annotations[i].uri + '">' + annotations[i].uri + '</a></p>';
	}
	document.getElementById('data-panel-records').innerHTML = innerHTML;
}