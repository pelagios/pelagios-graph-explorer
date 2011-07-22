// Client-side code related to the server API calls
Pelagios.Async = { }

Pelagios.Async.getInstance = function() {
	if (Pelagios.Async.instance)
		return Pelagios.Async.instance;
	
	Pelagios.Async.instance = {
		getAutoCompleteHint : function(term, callback) {
			$.getJSON("places/search?q=" + term, function(data) {
				callback(data);
			});
		},

		fetchConvexHull : function(node) {
			var pMap = this.pMap;
			$.getJSON("datasets/" + node.name + "/places/convexhull", function(data) {
				Pelagios.Map.getInstance().addPolygon(node.name, data, node.stroke);
			})
			.error(function(data) { alert("Something went wrong: " + data.responseText); });	
		},

		fetchPlaceReferences : function(place) {
			Pelagios.Loadmask.getInstance().show();
			var pNode = personalGraph.newPlace(place);
			$.getJSON("places/occurences?place=" + encodeURI(place.uri), function(data) {
				for (var i=0, ii=data.length; i<ii; i++) {
					var di = data[i];
					var dNode = personalGraph.newDataset(di.dataset, di.datasetSize, di.rootDataset);
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

				for (var j=0, jj=data.length; j<jj; j++) {
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

				Pelagios.Loadmask.getInstance().hide();
			})
			.error(function(data) { alert("Something went wrong: " + data.responseText); });	
		},

		computeOverlap : function(srcNode, destNode, selectionManager) {
			Pelagios.Loadmask.getInstance().show();
			var url = "places/intersect?set1=" +
				srcNode.name + "&set2=" + destNode.name;
			
			$.getJSON(url, function(data) {
				selectionManager.setLink(srcNode, destNode, data);
				Pelagios.Loadmask.getInstance().hide();
			})
			.error(function(data) { alert("Something went wrong: " + data.responseText); });			
		},

		fetchGeoAnnotations : function(node) {
			if (node.records > 500) {
				
			} else {
				var pDataview = this.pDataview;
				$.getJSON("datasets/" + node.name + "/annotations", function(data) {
					pDataview.setGeoAnnotations(data);
				})
				.error(function(data) { alert("Something went wrong: " + data.responseText); });
			}
		}
			
	}
	
	return Pelagios.Async.instance;
}