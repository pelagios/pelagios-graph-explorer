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
	
	function toCSS(col) {
		var css =
			'background-color: ' + col + '; '; // +
		/*
			'background-image: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#fff), to(' + col + ')); ' +
			'background-image: -webkit-linear-gradient(top, #fff, ' + col + '); ' +  
			'background-image: -moz-linear-gradient(top, #fff, ' + col + '); ' +
			'background-image: -ms-linear-gradient(top, #fff, ' + col + '); ' +
			'background-image: -o-linear-gradient(top, #fff, ' + col + ');' +
			'border-top: 1px solid ' + col + ';';
		*/
		return css;
	}
	
	var annotationList = new Array();
	
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
			annotationList.length = 0;
			set('');
		},
	
		showDatasetInfo : function(dataset) {
			annotationList.length = 0;
			
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
				map.zoomToFeature(dataset.name);
				
				var async = Pelagios.Async.getInstance();
				async.getPlaces(dataset.name, function(data) {
					annotationList.length = 0;
					for (var i=0, ii=data.length; i<ii; i++) {
						map.addPlace(data[i], function(place, event) {
							annotationList.length = 0;
							async.getAnnotations(place, dataset.name);
						});
						map.showFeature(data[i].uri);
					}
				});
			});
		},
		
		showMultiSelectionMessage : function() {
			annotationList.length = 0;
			Pelagios.Map.getInstance().clear();
			
			set('<div class="dataset-info">' +
			'<h1>Multiple Datasets Selected</h1>' +
			'<p>Click on a link between two datasets to show information ' +
			'about their common places.<b>');
		},
		
		showOverlapInfo : function(overlap) {
			annotationList.length = 0;
			
			var innerHTML = '<div class="dataset-info">' +
				'<h1>Intersection between ' + overlap.srcSet +
				' and ' + overlap.destSet + '</h1>' +
				'<p>' + overlap.commonPlaces.length + ' places in common.</p>' +
				'<input id="btnShowOnMap" type="button" value="Show on Map"/></div>';
			
			set(innerHTML);
			
			$("#btnShowOnMap").click(function() {				
				var map = Pelagios.Map.getInstance();
				map.clear();
				map.setVisible(true);
				map.zoomToArea(overlap.footprint);
				
				var async = Pelagios.Async.getInstance();
				var places = overlap.commonPlaces;
				for (var i=0, ii= places.length; i<ii; i++) {
					map.addPlace(places[i], function(place, event) {	
						annotationList.length = 0;
						async.getAnnotations(place, overlap.srcSet);
						async.getAnnotations(place, overlap.destSet);
					});
					map.showFeature(places[i].uri);
				}
			});
		},
		
		showGeoAnnotations : function(place, datasetName, annotations) {
			// Add to annotation list
			for (var i=0, ii=annotations.length; i<ii; i++) {
				annotationList.push({annotation: annotations[i], place: place, dataset:datasetName});
			}
			
			// Redraw
			var innerHTML = '<div class="annotation-list-header"><h1>' + place.label + '</h1>'
			+ '<span class="annotation-list-info">' 
			+ '[<a href="' + place.uri + '" target="_blank">View Pleiades Record</a>] '
			+ '[<a id="view-graph" href="">View Local Graph</a>]'
			+ '</span>'
			+ '<p>' + annotationList.length + ' references in selected data sets</p></div>'
			+ '<div class="annotation-list">';

			var palette = Pelagios.Palette.getInstance();
			for (var i=0, ii=annotationList.length; i<ii; i++) {
				var a = annotationList[i];
				
				innerHTML += '<p style="' + toCSS(palette.darker(palette.getColor(a.annotation.rootDataset)))
					+ '">Reference in ' + a.annotation.dataset + '<br/>'
					+ '<a target="_blank" href="' + a.annotation.uri + '">' 
					+ a.annotation.label + '</a>';
				
				if (a.annotation.properties.Type)
					innerHTML += "<br/>Type: " + a.annotation.properties.Type;
				
				if (a.annotation.properties.Material)
					innerHTML += "<br/>Material: " + a.annotation.properties.Material;
				
				innerHTML += '</p>';
			}
			
			innerHTML += '</div>';
			set(innerHTML);
			
			var map = Pelagios.Map.getInstance();
			$("#view-graph").click(function() {				
				Pelagios.Graph.Local.getInstance().show();
				map.clear();
				map.addPlace(place);
				map.showFeature(place.uri);
				Pelagios.Async.getInstance().occurences(place);
				return false;
			});
		}
	}
	
	return Pelagios.DataPanel.instance;
}
