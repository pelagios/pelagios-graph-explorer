// Code related to the data view panel
Pelagios.DataPanel = {}

Pelagios.DataPanel.PANEL_DIV_ID = "#data-panel";
Pelagios.DataPanel.CONTENT_DIV_ID = "data-panel-records";

Pelagios.DataPanel.getInstance = function() {
	if (Pelagios.DataPanel.instance)
		return Pelagios.DataPanel.instance;
	
	function set(innerHTML) {
		document.getElementById(Pelagios.DataPanel.CONTENT_DIV_ID)
			.innerHTML = innerHTML;
	}
	
	Pelagios.DataPanel.instance = {
		setVisible : function(visible) {
			if (visible) {
				$(Pelagios.DataPanel.PANEL_DIV_ID).dialog("open");
			} else {
				$(Pelagios.DataPanel.PANEL_DIV_ID).dialog("close");		
			}
		},
	
		isVisible : function() {
			return $(Pelagios.DataPanel.PANEL_DIV_ID)
				.parents(".ui-dialog").is(":visible");
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
							async.getAnnotations(place, dataset);
						});
						map.showFeature(data[i].uri);
					}
				});
			});
		},
		
		showMultiSelectionMessage : function() {
			set('<div class="dataset-info">' +
			'<h1>Multiple Datasets Selected</h1>' +
			'<p>Click on a link between two datasets to show information ' +
			'about their common places.<b>');
		},
		
		showOverlapInfo : function(overlap) {
			var innerHTML = '<h1>Intersection between ' + overlap.srcSet +
				' and ' + overlap.destSet + '</h1>' +
				'<p>' + overlap.commonPlaces.length + ' in common.</p>'
			
		},
		
		showGeoAnnotations : function(place, dataset, annotations) {
			var innerHTML = '';
			for (var i=0, ii=annotations.length; i<ii; i++) {
				innerHTML += '<p>Annotation: ' + place.label + ' (<a target="_blank" href="' 
					+ place.uri + '">Pleiades</a>) in ' + dataset.name
					+ '<br/><a target="_blank" href="' + annotations[i].uri + '">' 
					+ annotations[i].uri + '</a></p>';
			}
			set(innerHTML);
		}
	}
	
	return Pelagios.DataPanel.instance;
}
