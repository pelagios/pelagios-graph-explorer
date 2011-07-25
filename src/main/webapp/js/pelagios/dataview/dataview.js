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
			var innerHTML = '<div class="dataset-info">' +
				'<h1>' + dataset.name + '</h1>' +
				'<p>This data set contains <b>' + dataset.geoAnnotations + '</b> geo-annotations ';
			
			if (dataset.subsets > 0)
				innerHTML += 'in <b>' + dataset.subsets + '</b> sub-sets, ';
			
			innerHTML += 'referencing <b>' + dataset.places + '</b> unique places.</p>' +
				'<input id="btnShowOnMap" type="button" value="Show on Map"/></div>';
			
			set(innerHTML);
			
			$("#btnShowOnMap").click(function() {
				var map = Pelagios.Map.getInstance();
				map.clear();
				map.setVisible(true);
				
				var async = Pelagios.Async.getInstance();
				async.getPlaces(dataset.name, function(data) {
					for (var i=0, ii=data.length; i<ii; i++) {
						map.addPlace(data[i], function(place, event) {
							async.getAnnotations(place.uri, dataset.name);
						});
						map.showFeature(data[i].uri);
					}
				});
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
