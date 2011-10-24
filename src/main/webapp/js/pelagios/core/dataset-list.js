Pelagios.DatasetList = {}

Pelagios.DatasetList.PANEL_DIV_ID = "dataset-list";

Pelagios.DatasetList.getInstance = function() {
	if (Pelagios.DatasetList.instance)
		return Pelagios.DatasetList.instance;
	
	var toggleDataset = function(name) {
		
	}
	
	Pelagios.DatasetList.instance = {
		init : function() {
		    Pelagios.Async.getInstance().datasets(null, function(data) {
				for (var i=0, ii=data.length; i<ii; i++) {
					Pelagios.DatasetList.instance.add(data[i]);
				}
		    });	
		},
		
		add : function(dataset) {
			var s = '<span><input type="checkbox" checked="true" '
				+ 'onclick="javascript:Pelagios.Graph.Local.getInstance().toggleVisible(\'' + dataset.name + '\');" '
				+ 'value="' + dataset.name + '" />' 
				+ dataset.name + '</span><br/>'; 
			document.getElementById(Pelagios.DatasetList.PANEL_DIV_ID).innerHTML += s;
		}
	}
	
	return Pelagios.DatasetList.instance;
}