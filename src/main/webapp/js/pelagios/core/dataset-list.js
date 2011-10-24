Pelagios.DatasetList = {}

Pelagios.DatasetList.PANEL_DIV_ID = "dataset-list";

Pelagios.DatasetList.getInstance = function() {
	if (Pelagios.DatasetList.instance)
		return Pelagios.DatasetList.instance;
	
	var hidden = new Array();
	
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
				+ 'onclick="javascript:Pelagios.DatasetList.getInstance().toggleVisible(\'' + dataset.name + '\');" '
				+ 'value="' + dataset.name + '" />' 
				+ dataset.name + '</span><br/>'; 
			document.getElementById(Pelagios.DatasetList.PANEL_DIV_ID).innerHTML += s;
		},
		
		toggleVisible : function(name) {
			var idx = hidden.indexOf(name);
			if (idx > -1) {
				Pelagios.Graph.Local.getInstance().setVisible(name, true);
				hidden.splice(idx, 1);
			} else {
				Pelagios.Graph.Local.getInstance().setVisible(name, false);
				hidden.push(name);
			}
		},
		
		isHidden : function(name) {
			return (hidden.indexOf(name) > -1);
		}
	}
	
	return Pelagios.DatasetList.instance;
}