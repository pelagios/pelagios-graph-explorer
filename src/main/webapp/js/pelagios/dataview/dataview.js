// Code related to the data view panel
Pelagios.DataPanel = {}

Pelagios.DataPanel.getInstance = function() {
	if (Pelagios.DataPanel.instance)
		return Pelagios.DataPanel.instance;
	
	function set(innerHTML) {
		document.getElementById('data-panel-records').innerHTML = innerHTML;
	}
	
	Pelagios.DataPanel.instance = {
		setVisible : function(visible) {
			if (visible) {
				$("#data-panel").dialog("open");
			} else {
				$("#data-panel").dialog("close");		
			}
		},
	
		isVisible : function() {
			return $("#data-panel").parents(".ui-dialog").is(":visible");
		},
		
		clear : function() {
			set('');
		},
	
		showDatasetInfo : function(dataset) {
			set('<div class="dataset-info">' +
				'<h1>' + dataset.name + '</h1>' +
				'<p>Contains ' + dataset.geoAnnotations + ' geo-annotations<br/>' +
				'referencing ' + dataset.places + ' unique places.</p>' +
				'<input id="btnShowOnMap" type="button" value="Show on Map"/></div>');
			
			$("#btnShowOnMap").click(function() {
				
			});
		},
		
		showMultiDatasetInfo : function(datasets) {
			
		},
		
		showOverlapInfo : function() {
			
		},
		
		showGeoAnnotations : function(annotations) {
			var innerHTML = '';
			for (var i=0, ii=annotations.length; i<ii; i++) {
				innerHTML += '<p><a target="_blank" href="' + annotations[i].uri + '">' + annotations[i].uri + '</a></p>';
			}
			set(innerHTML);
		}
	}
	
	return Pelagios.DataPanel.instance;
}
