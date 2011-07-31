// Client-side code related to the server API calls
Pelagios.Async = { }

Pelagios.Async.getInstance = function() {
	if (Pelagios.Async.instance)
		return Pelagios.Async.instance;
	
	Pelagios.Async.instance = {
		datasets : function(parent, callback) {
	    	var url = "datasets/"
	    	if (parent)
	    		url += parent.name;
	    	
	    	$.getJSON(url, function(data) {
	    		callback(data);
	    	})
	    	.error(function() { alert("Error."); });
	    },
			
		search : function(term, callback) {
			$.getJSON("places/search?q=" + term, function(data) {
				callback(data);
			});
		},
		
		getPlaces : function(datasetName, callback) {
			$.getJSON("datasets/" + datasetName + "/places", function(data) {
				callback(data);
			})
			.error(function(data) { 
				alert("Sorry. Something went wrong while fetching places contained in "
					+ datasetName + ": " + data.responseText); 
			});	
		},

		fetchConvexHull : function(dataset) {
			$.getJSON("datasets/" + dataset.name + "/places/convexhull", function(data) {
				var palette = Pelagios.Palette.getInstance();
				Pelagios.Map.getInstance().addPolygon(dataset.name, data, 
					palette.darker(palette.getColor(dataset.rootDataset)));
			})
			.error(function(data) { alert("Something went wrong: " + data.responseText); });	
		},

		occurences : function(place) {
			Pelagios.Loadmask.getInstance().show();
			var personalGraph = Pelagios.Graph.Local.getInstance(); 
			var pNode = personalGraph.newPlace(place);
			$.getJSON("places/occurences?place=" + encodeURI(place.uri), function(data) {
				for (var i=0, ii=data.length; i<ii; i++) {
					var di = data[i];
					var dNode = personalGraph.newDataset(di.dataset, di.datasetSize, di.rootDataset);
					// alert(pNode.name + " - " + dNode.name);
					personalGraph.setEdge(pNode, dNode, di.occurences);
					
					var palette = Pelagios.Palette.getInstance();
					Pelagios.Map.getInstance().addPolygon(di.dataset, di.datasetFootprint,
							palette.darker(palette.getColor(di.rootDataset)));
				}
				personalGraph.purgeGraph();
				Pelagios.Loadmask.getInstance().hide();
			})
			.error(function(data) { alert("Something went wrong: " + data.responseText); });
		},

		findShortestPaths : function(from, to) {
			Pelagios.Loadmask.getInstance().show();
			$.getJSON("places/shortestpaths?from=" + encodeURI(from.place.uri) + 
				"&to=" + encodeURI(to.place.uri), function(data) {
				
				var personalGraph = Pelagios.Graph.Local.getInstance();
				for (var j=0, jj=data.length; j<jj; j++) {
					if (data[j].via) {
						var lastNode = from;
						var lastWeight = data[j].start.annotations;
						for (var i=0, ii=data[j].via.length; i<ii; i++) {
							var dset = data[j].via[i];
							newNode = personalGraph.newDataset(dset.dataset, dset.datasetSize, dset.rootDataset);
							personalGraph.setEdge(lastNode, newNode, lastWeight);
							lastWeight = 1;
							lastNode = newNode;
						}
				
						personalGraph.setEdge(to, lastNode, data[j].end.annotations);
					}
				}

				Pelagios.Loadmask.getInstance().hide();
			})
			.error(function(data) { alert("Something went wrong: " + data.responseText); });	
		},

		intersect : function(srcNode, destNode, selectionManager) {
			Pelagios.Loadmask.getInstance().show();
			var url = "places/intersect?set1=" +
				srcNode.dataset.name + "&set2=" + destNode.dataset.name;
			
			$.getJSON(url, function(data) {
				selectionManager.setLink(srcNode, destNode, data);
				Pelagios.Loadmask.getInstance().hide();
			})
			.error(function(data) { alert("Something went wrong: " + data.responseText); });			
		},
		
		// TODO quick hack -> different behaviors should be covered by a callback parameter!
		getAnnotations : function(place, datasetName) {
			$.getJSON("annotations?place=" + encodeURI(place.uri) + "&dataset=" + datasetName,
				function(data) {
					Pelagios.DataPanel.getInstance().showGeoAnnotations(place, datasetName, data);
				});
		},
		
		getAnnotationsForDataset : function(place, datasetName) {
			$.getJSON("annotations?place=" + encodeURI(place.uri) + "&dataset=" + datasetName, 
				function(data) {
					Pelagios.DataPanel.getInstance().showGeoAnnotations_local(place, datasetName, data);
				});
		},
		
		stronglyRelated : function(place, callback) {
			$.getJSON("places/stronglyRelated?place=" + encodeURI(place.uri), function(data) {
				callback(data);
			});
		}
			
	}
	
	return Pelagios.Async.instance;
}